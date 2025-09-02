package com.mycompany.irr00_group_project.views.homepage.friendslist;

import com.mycompany.irr00_group_project.controllers.FollowedCardController;
import com.mycompany.irr00_group_project.controllers.managers.SessionManager;
import com.mycompany.irr00_group_project.models.User;
import com.mycompany.irr00_group_project.utils.GuiHelper;
import com.mycompany.irr00_group_project.utils.ThemeUtils;
import com.mycompany.irr00_group_project.views.homepage.RecommendationsView;
import com.mycompany.irr00_group_project.views.homepage.SlidingNotification;
import com.mycompany.irr00_group_project.views.profile.UserView;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * FollowedListView shows a list of followed users with their status and actions.
 */
public class FollowedListView extends VBox {

    private final VBox followedList = GuiHelper.createPaddedVBox(10, 0);


    /**
     * Creates the followed list layout.
     */
    public FollowedListView() {
        Label followedListLabel = GuiHelper.createTitleLabel("Following");

        Button followButton = GuiHelper.createStyledButton("+", this::switchToSearchUsersView);
        ThemeUtils.makeCircleButton(followButton);
        followButton.setStyle("-fx-font-size: 20px;");

        HBox rightBox = GuiHelper.createAlignedHBox(0, 0, Pos.CENTER_RIGHT);
        rightBox.getChildren().add(followButton);

        BorderPane navBar = new BorderPane();
        navBar.setPadding(new Insets(10));
        navBar.setLeft(followedListLabel);
        navBar.setRight(rightBox);
        BorderPane.setMargin(rightBox, new Insets(0, 20, 0, 20));

        ScrollPane scrollPane = new ScrollPane(followedList);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        this.setSpacing(10);
        this.setPadding(new Insets(20));
        this.getChildren().addAll(navBar, scrollPane);
        this.setPrefWidth(350);
        ThemeUtils.setPrimaryBackgroundRoundedInsets(this);

        refresh();
    }

    /**
     * Refreshes the followed list display.
     */
    public void refresh() {
        followedList.getChildren().clear();

        User currentUser = SessionManager.getInstance().getCurrentUser();

        currentUser.getFollowedUsers().forEach(friend -> {
            FollowedCardController controller = new FollowedCardController(friend, this::refresh,
                () -> {
                    SlidingNotification.showSuccess(this.getScene(), 
                        "Unfollowed " + friend.getUsername() + " successfully.");
                },
                () -> {
                    SlidingNotification.showSuccess(this.getScene(), 
                        "Followed " + friend.getUsername() + " successfully.");
                },
                userToShow -> {
                    switchToUsersView(friend);
                }
            );
            followedList.getChildren().add(controller.getView());
        });
    }
    
    /**
     * Switches to the SearchUsersView to allow the user to search for other users.
     */
    private void switchToSearchUsersView() {
        if (this.getParent() instanceof BorderPane borderPane) {
            borderPane.setRight(null);
            borderPane.setRight(new SearchUsersView());
        }
    }

    /**
     * Switches to the user view for the specified user.
     * 
     * @param user The user to display in the UserView.
     */
    private void switchToUsersView(User user) {
        if (this.getParent() instanceof BorderPane borderPane) {
            borderPane.setCenter(null);
            borderPane.setCenter(new UserView(user, 
                () -> {
                    borderPane.setCenter(new RecommendationsView());
                    refresh();
                }
            ));
        } 
    }
}
