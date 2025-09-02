package com.mycompany.irr00_group_project.views.homepage.friendslist;

import com.mycompany.irr00_group_project.controllers.FollowedCardController;
import com.mycompany.irr00_group_project.controllers.UserSearchController;
import com.mycompany.irr00_group_project.models.User;
import com.mycompany.irr00_group_project.utils.GuiHelper;
import com.mycompany.irr00_group_project.utils.LogSignGuiHelper;
import com.mycompany.irr00_group_project.utils.ThemeUtils;
import com.mycompany.irr00_group_project.views.homepage.RecommendationsView;
import com.mycompany.irr00_group_project.views.homepage.SlidingNotification;
import com.mycompany.irr00_group_project.views.profile.UserView;

import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;

/**
 * usersListView shows a list of followed users with their status and actions.
 */
public class SearchUsersView extends VBox {

    private final VBox usersList = GuiHelper.createPaddedVBox(10, 0);
    private final javafx.scene.control.TextField searchField;
    private final PauseTransition debounceTimer;
    private final UserSearchController controller;

    /**
     * Creates the search users layout.
     */
    public SearchUsersView() {
        searchField = GuiHelper.createSearchField("Search users...", 180);
        controller = new UserSearchController(() -> {
            try {
                refreshToDefault();
            } catch (IllegalArgumentException e) {
                SlidingNotification.showError(this.getScene(), "Error searching users.");
            }
        }, this::showResults);

        debounceTimer = new PauseTransition(javafx.util.Duration.millis(300));
        debounceTimer.setOnFinished(event -> {
            try {
                controller.handleSearch(searchField.getText().trim());
            } catch (Exception e) {
                SlidingNotification.showError(this.getScene(), "Error searching users.");
            }
        });

        Label usersListLabel = GuiHelper.createTitleLabel("Search Users");
        setupSearchFieldListeners();

        Button exitButton = GuiHelper.createStyledButton("x", this::switchToFollowedListView);
        ThemeUtils.makeCircleButton(exitButton);
        exitButton.setStyle("-fx-font-size: 20px;");

        HBox rightBox = GuiHelper.createAlignedHBox(0, 0, Pos.CENTER_RIGHT);
        rightBox.getChildren().add(exitButton);

        VBox leftBox = GuiHelper.createPaddedVBox(5, 0);
        leftBox.getChildren().addAll(usersListLabel, searchField);
        leftBox.setAlignment(Pos.CENTER_LEFT);

        // Use helper for nav bar and scroll pane
        Node navBar = GuiHelper.createNavBar(leftBox, rightBox, new Insets(10));
        Node scrollPane = LogSignGuiHelper.createStyledScrollPane(usersList);

        this.setSpacing(10);
        this.setPadding(new Insets(20));
        this.getChildren().addAll(navBar, scrollPane);
        this.setPrefWidth(350);
        ThemeUtils.setPrimaryBackgroundRoundedInsets(this);

        loadDefaultResults();
    }

    /**
     * Sets up listeners for the search field to handle user input and focus changes.
     */
    private void setupSearchFieldListeners() {
        searchField.setOnKeyReleased(e -> debounceTimer.playFromStart());
        searchField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal && searchField.getText().trim().isEmpty()) {
                controller.handleClearSearch(searchField);
            }
        });
    }

    /**
     * Loads the default results when the view is initialized.
     * This method is called to ensure that the users list is populated with followed users.
     */
    private void loadDefaultResults() {
        try {
            refreshToDefault();
        } catch (IllegalArgumentException e) {
            System.err.println("Error loading followed users: " + e.getMessage());
        }
    }

    /**
     * Displays the search results in the users list.
     * @param users the list of users to display
     */
    public void showResults(List<User> users) {
        usersList.getChildren().clear();
        for (User user : users) {
            FollowedCardController card = new FollowedCardController(user, 
                () -> showResults(users),
                () -> SlidingNotification.showSuccess(this.getScene(), 
                    "Unfollowed " + user.getUsername() + " successfully."),
                () -> SlidingNotification.showSuccess(this.getScene(), 
                    "Followed " + user.getUsername() + " successfully."),
                userToShow -> switchToUsersView(user)
            );
            usersList.getChildren().add(card.getView());
        }
    }

    /**
     * Refreshes the users list to show the default followed users.
     * This method clears the current list and repopulates it with followed users.
     * @throws IllegalArgumentException if the search query is invalid
     * @throws IOException if there is an error accessing the database
     */
    private void refreshToDefault() throws IllegalArgumentException {
        usersList.getChildren().clear();
    }

    /**
     * Switches the view to the followed list view.
     * This method is called when the user clicks the exit button.
     */
    private void switchToFollowedListView() {
        if (this.getParent() instanceof javafx.scene.layout.BorderPane borderPane) {
            borderPane.setRight(null);
            borderPane.setRight(new FollowedListView());
        }
    }

    /**
     * Switches to the user view for the specified user.
     * This method is called when a user card is clicked.
     * 
     * @param user The user to display in the UserView.
     */
    private void switchToUsersView(User user) {
        if (this.getParent() instanceof javafx.scene.layout.BorderPane borderPane) {
            borderPane.setCenter(null);
            borderPane.setCenter(new UserView(user,
                () -> borderPane.setCenter(new RecommendationsView())
            ));
        }
    }
}