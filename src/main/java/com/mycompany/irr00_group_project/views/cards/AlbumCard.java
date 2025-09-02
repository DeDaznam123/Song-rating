package com.mycompany.irr00_group_project.views.cards;

import com.mycompany.irr00_group_project.models.reviewables.targets.Album;
import com.mycompany.irr00_group_project.utils.ThemeUtils;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * AlbumCard displays an album card with album art, title, artist, and action buttons.
 * It is used in the search results view to represent individual albums.
 */
public class AlbumCard extends HBox {

    /**
     * Constructor for the AlbumCard class.
     * Initializes the layout and components for displaying an album card.
     *
     * @param album The album object containing information to display.
     */
    public AlbumCard(Album album) {
        super(10);
        setPadding(new Insets(10));
        setAlignment(Pos.CENTER_LEFT);
        setPrefHeight(50);
        ThemeUtils.setSecondaryBackgroundRounded(this);

        ImageView albumArtView = new ImageView();
        albumArtView.setFitWidth(30);
        albumArtView.setFitHeight(30);
        albumArtView.setPreserveRatio(true);
        albumArtView.setSmooth(true);
        albumArtView.setImage(
            new Image(
                getClass().getResourceAsStream("/db/images/missing_assets/missing.jpg")
            )
        );

        VBox textBox = new VBox(2);
        Label nameLabel = new Label(album.getName());
        ThemeUtils.setTextFont(nameLabel);

        Label artistLabel = new Label(album.getArtist().getName());
        ThemeUtils.setTextFont(artistLabel);

        textBox.getChildren().addAll(nameLabel, artistLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        getChildren().addAll(albumArtView, textBox, spacer);
    }
}
