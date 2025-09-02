package com.mycompany.irr00_group_project.views.cards;

import com.mycompany.irr00_group_project.models.reviewables.targets.Song;
import com.mycompany.irr00_group_project.utils.ThemeUtils;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

/**
 * SongCardView displays a song card with album art, title, artist, and action buttons.
 * It is used in the search results view to represent individual songs.
 */
public class SongCard extends HBox {

    /**
     * Constructor for the SongCardView class.
     * Initializes the layout and components for displaying a song card.
     *
     * @param song The song object containing information to display.
     */
    public SongCard(Song song) {
        super(10);
        setPadding(new Insets(10));
        setAlignment(Pos.CENTER_LEFT);
        setPrefHeight(50);
        ThemeUtils.setSecondaryBackgroundRounded(this);

        ImageView albumArtView = new ImageView(song.getImage());
        albumArtView.setFitWidth(30);
        albumArtView.setFitHeight(30);
        albumArtView.setPreserveRatio(true);
        albumArtView.setSmooth(true);
        Circle clip = new Circle(15, 15, 15);
        albumArtView.setClip(clip);

        VBox textBox = new VBox(2);
        Label nameLabel = new Label(song.getName());
        ThemeUtils.setTextFont(nameLabel);

        Label artistLabel = new Label(song.getArtist().getName());
        ThemeUtils.setTextFont(artistLabel);

        textBox.getChildren().addAll(nameLabel, artistLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        getChildren().addAll(albumArtView, textBox, spacer);
    }
}