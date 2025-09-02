package com.mycompany.irr00_group_project.views.reviewpage.detail_views;

import com.mycompany.irr00_group_project.models.reviewables.Reviewable;
import com.mycompany.irr00_group_project.services.Database;
import com.mycompany.irr00_group_project.utils.ThemeUtils;
import com.mycompany.irr00_group_project.views.reviewpage.StarRatingView;
import com.mycompany.irr00_group_project.views.reviewpage.comment_section.CommentSectionView;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * DetailView is an abstract class that provides common functionality for displaying
 * detailed views of reviewable items such as artists, albums, and songs.
 * It includes methods to create the artist info box with image and basic information.
 */
public abstract class DetailView extends VBox {
    
    /**
     * Constructor for DetailView.
     */
    public DetailView(Reviewable reviewable, Runnable onBack) {
        this.setSpacing(20);
        this.setAlignment(Pos.TOP_LEFT);
        ThemeUtils.setPrimaryBackgroundRoundedInsets(this);

        // Back button.
        Button backButton = createBackButton(onBack);

        // Reviewable info section with image and details.
        HBox reviewableInfoBox = createInfoBox(reviewable);

        // Comment section.
        CommentSectionView commentSection = new CommentSectionView(reviewable);

        // Add all components to the main view
        getChildren().addAll(backButton, reviewableInfoBox, commentSection);
    }
    
    /**
     * Shows the ratings for the reviewable item.
     *
     * @param reviewable The reviewable item (artist, album, or song)
     * @return VBox containing the rating controls
     */
    protected FlowPane createRatingBox(Reviewable reviewable) {
        FlowPane ratingBox = new FlowPane(10, 10);
        ratingBox.setAlignment(Pos.CENTER_LEFT);

        // Create rating controls for each category
        VBox categoryBox = new VBox();
        categoryBox.setPrefHeight(Region.USE_COMPUTED_SIZE);
        categoryBox.setMaxHeight(Region.USE_PREF_SIZE);
        categoryBox.setMinHeight(Region.USE_PREF_SIZE);

        Label categoryLabel = new Label("Rating");
        ThemeUtils.setSmallTitleFont(categoryLabel);
        StarRatingView starRatingView = new StarRatingView(false);
        try {
            starRatingView.setRating(Database.getInstance().getReviewRating(reviewable));
        } catch (Exception e) {
            e.printStackTrace();
        }
        categoryBox.getChildren().addAll(categoryLabel, starRatingView);

        ratingBox.getChildren().add(categoryBox);

        return ratingBox;
    }

    /**
     * Creates just the star rating component without label or container.
     * Used to display rating next to the title.
     *
     * @param reviewable The reviewable item (artist, album, or song)
     * @return StarRatingView component
     */
    protected StarRatingView createStarRating(Reviewable reviewable) {
        StarRatingView starRatingView = new StarRatingView(false);
        try {
            starRatingView.setRating(Database.getInstance().getReviewRating(reviewable));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return starRatingView;
    }

    /**
     * Creates the info box with image and basic information.
     * @param reviewable The reviewable item (artist, album, or song)
     * @return HBox containing artist image and info
     */
    protected abstract HBox createInfoBox(Reviewable reviewable);

    /**
     * Creates the artist photo image view.
     * @param reviewable The reviewable item (artist, album, or song)
     * @return ImageView for item photo
     */
    protected ImageView createItemPhoto(Reviewable reviewable) {
        ImageView itemPhoto = new ImageView(reviewable.getImage());

        itemPhoto.setFitWidth(150);
        itemPhoto.setFitHeight(150);
        itemPhoto.setPreserveRatio(true);
        itemPhoto.setSmooth(true);

        // Add rounded corners
        ThemeUtils.applyCircularClip(itemPhoto, 75);

        return itemPhoto;
    }

    /**
     * Creates the back button for navigating to the previous view.
     * @param onBack The action to perform when the back button is clicked
     * @return Button for going back
     */
    protected Button createBackButton(Runnable onBack) {
        Button backButton = new Button("← Back to main menu");
        ThemeUtils.styleButton(backButton);
        backButton.setOnAction(e -> onBack.run());
        return backButton;
    }
}
