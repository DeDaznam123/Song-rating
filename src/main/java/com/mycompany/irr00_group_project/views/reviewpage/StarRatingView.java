package com.mycompany.irr00_group_project.views.reviewpage;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.util.ArrayList;
import java.util.List;

import com.mycompany.irr00_group_project.utils.ImageUtils;

/**
 * StarRatingView is a custom control that displays a 5-star rating system.
 * Users can click on the stars to set their rating, and the stars will update accordingly.
 */
public class StarRatingView extends HBox {
    private final List<ImageView> stars = new ArrayList<>();
    private final Image fullStar;
    private final Image emptyStar;
    private final IntegerProperty rating = new SimpleIntegerProperty(0);

    /**
     * Creates a 5-star rating view.
     * @param editable Whether users can click to change the rating.
     */
    public StarRatingView(boolean editable) {
        this.fullStar = ImageUtils.loadFullStarImage();
        this.emptyStar = ImageUtils.loadEmptyStarImage();

        setSpacing(5);
        setAlignment(Pos.CENTER_LEFT);

        for (int i = 0; i < 5; i++) {
            ImageView star = new ImageView(emptyStar);
            star.setFitWidth(24);
            star.setFitHeight(24);
            star.setPreserveRatio(true);
            if (editable) {
                int starValue = i + 1;
                star.setCursor(Cursor.HAND);

                star.setOnMouseClicked(e -> setRating(starValue));
                star.setOnMouseEntered(e -> updateStars(starValue));
                star.setOnMouseExited(e -> updateStars(getRating()));
            }
            stars.add(star);
            getChildren().add(star);
        }

        rating.addListener((obs, oldVal, newVal) -> updateStars(newVal.intValue()));
    }

    /**
     * Updates the star images based on the current rating value.
     * @param value The new rating value (0 to 5).
     */
    private void updateStars(int value) {
        for (int i = 0; i < stars.size(); i++) {
            stars.get(i).setImage(i < value ? fullStar : emptyStar);
        }
    }

    /**
     * Gets the current rating value.
     * @return The rating (0 to 5).
     */
    public int getRating() {
        return rating.get();
    }

    /**
     * Sets the rating value, updating the star images accordingly.
     * @param value The new rating value (0 to 5).
     */
    public void setRating(int value) {
        if (value >= 0 && value <= 5) {
            rating.set(value);
        }
    }

    /**
     * Returns the rating property for binding.
     * @return IntegerProperty representing the rating.
     */
    public IntegerProperty ratingProperty() {
        return rating;
    }
}