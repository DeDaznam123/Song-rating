package com.mycompany.irr00_group_project.views.cards;

import com.mycompany.irr00_group_project.models.reviewables.targets.Artist;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * Represents an artist card in the search results.
 */
public class ArtistCard extends VBox {

    /**
     * Constructor for the ArtistCard class.
     *
     * @param artist The artist object containing information to display.
     */
    public ArtistCard(Artist artist) {
        setAlignment(Pos.CENTER);
        setSpacing(5);

        Circle clip = new Circle(50, 50, 50);
        ImageView imageView = new ImageView();
        imageView.setFitWidth(100);
        imageView.setFitHeight(100);
        imageView.setClip(clip);
        imageView.setImage(artist.getImage());

        Label label = new Label(artist.getName());
        label.setTextFill(Color.WHITE);

        getChildren().addAll(imageView, label);
    }
}
