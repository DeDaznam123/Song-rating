package com.mycompany.irr00_group_project.views.reviewpage.detail_views;

import com.mycompany.irr00_group_project.models.reviewables.Reviewable;
import com.mycompany.irr00_group_project.models.reviewables.targets.Album;
import com.mycompany.irr00_group_project.utils.ThemeUtils;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * AlbumDetailView displays the details of a selected album.
 */
public class AlbumDetailView extends DetailView {

    /**
     * Constructor for AlbumDetailView.
     * @param album The album to display.
     * @param onBack The callback to execute when the back button is pressed.
     */
    public AlbumDetailView(Album album, Runnable onBack) {
        super(album, onBack);
        this.setPadding(new Insets(20));
    }

    /**
     * Creates the album info box with image and basic information.
     * @param album The album
     * @return HBox containing album image and info
     */
    @Override
    protected HBox createInfoBox(Reviewable album) {
        HBox albumInfoBox = new HBox(20);
        albumInfoBox.setAlignment(Pos.CENTER_LEFT);

        // Album artwork
        ImageView albumArt = createItemPhoto(album);
        VBox albumDetails = new VBox(10);
        albumDetails.setAlignment(Pos.CENTER_LEFT);

        Label albumTitle = new Label(album.getName());
        ThemeUtils.setTitleFont(albumTitle);

        // Create HBox to hold title and star rating side by side
        HBox titleAndRatingBox = new HBox(10);
        titleAndRatingBox.setAlignment(Pos.CENTER_LEFT);
        titleAndRatingBox.getChildren().addAll(albumTitle, createStarRating(album));

        Label artistLabel = new Label("Album by " + ((Album) album).getArtist().getName());
        ThemeUtils.setTextFont(artistLabel);

        albumDetails.getChildren().addAll(titleAndRatingBox, artistLabel);
        
        albumInfoBox.getChildren().addAll(albumArt, albumDetails);
        return albumInfoBox;
    }
}
