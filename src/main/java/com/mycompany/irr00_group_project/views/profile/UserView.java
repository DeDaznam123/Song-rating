package com.mycompany.irr00_group_project.views.profile;

import com.mycompany.irr00_group_project.models.User;
import com.mycompany.irr00_group_project.utils.ThemeUtils;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * userDetailView displays the details of a selected user.
 */
public class UserView extends VBox {

    /**
     * Constructor for UserView.
     * @param user The user to display.
     * @param onBack A Runnable that defines the action to take when the back button is pressed.
     */
    public UserView(User user, Runnable onBack) {
        ThemeUtils.setPrimaryBackgroundRoundedInsets(this);
        setPadding(new javafx.geometry.Insets(20)); 
        setSpacing(20);
        Button backButton = createBackButton(onBack);
        HBox userInfoBox = createInfoBox(user);
        UserReviewsView userReviewsView = new UserReviewsView(user);
        getChildren().addAll(backButton, userInfoBox, userReviewsView);

    }

    /**
     * Creates back button to return to the main menu.
     * @param onBack Runnable that defines the action to take when the back button is pressed.
     */
    private Button createBackButton(Runnable onBack) {
        Button backButton = new Button("← Back main menu");
        ThemeUtils.styleButton(backButton);
        backButton.setOnAction(e -> onBack.run());
        return backButton;
    }

    /**
     * Creates the user info box with image and basic information.
     * @param user The user to display.
     */
    private HBox createInfoBox(User user) {
        HBox userInfoBox = new HBox(20);
        ThemeUtils.setSecondaryBackgroundRoundedInsets(userInfoBox);
        userInfoBox.setPadding(new javafx.geometry.Insets(20));
        userInfoBox.setAlignment(Pos.CENTER_LEFT);

        // User avatar
        ImageView userArt = new ImageView(user.getAvatar());
        userArt.setFitWidth(100);
        userArt.setFitHeight(100);
        userArt.setPreserveRatio(true);
        userArt.setSmooth(true);
        ThemeUtils.applyCircularClip(userArt, 50);

        // User details
        VBox userDetails = new VBox(5);
        userDetails.setAlignment(Pos.CENTER_LEFT);

        Label userName = new Label(user.getUsername());
        ThemeUtils.setTitleFont(userName);

        Label typeLabel = new Label("User");
        ThemeUtils.setSmallTitleFont(typeLabel);

        userDetails.getChildren().addAll(userName, typeLabel);

        userInfoBox.getChildren().addAll(userArt, userDetails);
        return userInfoBox;
    }
}
