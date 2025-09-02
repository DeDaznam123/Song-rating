package com.mycompany.irr00_group_project.views.reviewpage.comment_section;

import com.mycompany.irr00_group_project.views.reviewpage.StarRatingView;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * UserRatingBox is a UI component that allows users to rate different aspects of a song.
 * It includes star ratings for lyrics, vocals, instrumentals, production, and overall rating.
 */
public class UserRatingBox extends VBox {
    private final StarRatingView overallRating;

    /**
     * Constructs a UserRatingBox with star ratings for various song aspects.
     */
    public UserRatingBox() {
        setSpacing(10);
        setPadding(new Insets(10));
        setStyle("-fx-background-color: #1e1e2f; -fx-background-radius: 10;");

        overallRating = new StarRatingView(true);

        getChildren().addAll(
            createRow("Overall", overallRating)
        );
    }

    /**
     * Creates a row with a label and a star rating view.
     *
     * @param label The text label for the row.
     * @param stars The StarRatingView to be displayed in the row.
     * @return A HBox containing the label and the star rating view.
     */
    private HBox createRow(String label, StarRatingView stars) {
        Label l = new Label(label);
        l.setMinWidth(100);
        l.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        HBox row = new HBox(l, stars);
        row.setSpacing(10);
        return row;
    }

    /**
     * Resets all ratings to zero.
     */
    public void reset() {
        overallRating.setRating(0);
    }

    public int getOverall() { 
        return overallRating.getRating(); 
    }
}

