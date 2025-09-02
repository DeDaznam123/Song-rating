package com.mycompany.irr00_group_project.views.reviewpage.detail_views;

import com.mycompany.irr00_group_project.models.reviewables.Reviewable;
import com.mycompany.irr00_group_project.models.reviewables.targets.Artist;
import com.mycompany.irr00_group_project.utils.ThemeUtils;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * ArtistDetailView displays the details of a selected artist.
 */
public class ArtistDetailView extends DetailView {

    /**
     * Constructor for ArtistDetailView.
     * @param artist The artist to display.
     * @param onBack The callback to execute when the back button is pressed.
     */
    public ArtistDetailView(Artist artist, Runnable onBack) {
        super(artist, onBack);
        this.setPadding(new Insets(20));
    }

    /**
     * Creates the artist info box with image and basic information.
     * @param artist The artist
     * @return HBox containing artist image and info
     */
    @Override
    protected HBox createInfoBox(Reviewable artist) {
        HBox artistInfoBox = new HBox(20);
        artistInfoBox.setAlignment(Pos.CENTER_LEFT);

        // Artist photo
        ImageView artistPhoto = createItemPhoto(artist);
        VBox artistDetails = new VBox(10);
        artistDetails.setAlignment(Pos.CENTER_LEFT);

        Label artistName = new Label(artist.getName());
        ThemeUtils.setTitleFont(artistName);

        // Create HBox to hold title and star rating side by side
        HBox titleAndRatingBox = new HBox(10);
        titleAndRatingBox.setAlignment(Pos.CENTER_LEFT);
        titleAndRatingBox.getChildren().addAll(artistName, createStarRating(artist));

        Label typeLabel = new Label("Artist");
        ThemeUtils.setSmallTitleFont(typeLabel);

        artistDetails.getChildren().addAll(titleAndRatingBox, typeLabel);

        artistInfoBox.getChildren().addAll(artistPhoto, artistDetails);
        return artistInfoBox;
    }
}
