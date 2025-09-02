package com.mycompany.irr00_group_project.views.homepage.friendslist;

import com.mycompany.irr00_group_project.models.User;
import com.mycompany.irr00_group_project.utils.Colors;
import com.mycompany.irr00_group_project.utils.GuiHelper;
import com.mycompany.irr00_group_project.utils.ThemeUtils;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;

/**
 * FollowedCardView represents a single followed user's card in the followed list.
 * It displays the user's name, status, and action buttons.
 */
public class FollowedCard extends HBox {

    // Button field for controller access.
    private final Button dropdownButton;

    /**
     * Creates a followed card with the given followed user information.
     *
     * @param followedUser The followed user object containing the user's information.
     */
    public FollowedCard(User followedUser) {
        ThemeUtils.setSecondaryBackgroundRounded(this);
        this.setSpacing(10);
        this.setAlignment(Pos.CENTER_LEFT);
        this.setPrefHeight(50);
        this.setPadding(new javafx.geometry.Insets(10));

        // Name and Status
        VBox textBox = GuiHelper.createPaddedVBox(2, 0);

        Label nameLabel = GuiHelper.createTitleLabel(followedUser.getUsername());
        nameLabel.setFont(Font.font(16));
        nameLabel.setTextFill(Color.WHITE);

        textBox.getChildren().addAll(nameLabel);

        // More button
        dropdownButton = GuiHelper.createStyledButton("⋮", () -> {});
        dropdownButton.setStyle("-fx-background-color: transparent;");
        dropdownButton.setTextFill(Colors.TEXT);
        dropdownButton.setFont(new Font(20));

        // Avatar
        Circle avatar = new Circle();
        avatar.setRadius(20);

        ImageView avatarImage = new ImageView(followedUser.getAvatar());
        avatarImage.setFitWidth(100);
        avatarImage.setFitHeight(100);
        avatarImage.setPreserveRatio(true);
        avatarImage.setSmooth(true);

        avatar.setFill(new ImagePattern(avatarImage.getImage()));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        this.getChildren().addAll(avatar, textBox, spacer, dropdownButton);
    }

    /**
     * Sets the action to be performed when the more button is clicked.
     *
     * @param action The action to be performed.
     */
    public void onMoreClicked(Runnable action) {
        dropdownButton.setOnAction(e -> action.run());
    }

    /**
     * Gets the more button for external access.
     *
     * @return The more button.
     */
    public Button getdropdownButton() {
        return dropdownButton;
    }
}
