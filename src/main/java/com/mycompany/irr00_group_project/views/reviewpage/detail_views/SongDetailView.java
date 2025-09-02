package com.mycompany.irr00_group_project.views.reviewpage.detail_views;

import com.mycompany.irr00_group_project.models.reviewables.Reviewable;
import com.mycompany.irr00_group_project.models.reviewables.targets.Song;
import com.mycompany.irr00_group_project.utils.ThemeUtils;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * SongDetailView displays the details of a selected song.
 */
public class SongDetailView extends DetailView {

    /**
     * Constructor for SongDetailView.
     * @param song The song to display.
     * @param onBack The callback to execute when the back button is pressed.
     */
    public SongDetailView(Song song, Runnable onBack) {
        super(song, onBack);
        this.setPadding(new Insets(20));
    }

    /**
     * Creates the song info box with image and basic information.
     * @param song The song
     * @return HBox containing song image and info
     */
    @Override
    protected HBox createInfoBox(Reviewable song) {
        HBox songInfoBox = new HBox(20);
        songInfoBox.setAlignment(Pos.CENTER_LEFT);

        // Song artwork
        ImageView songArt = createItemPhoto(song);
        VBox songDetails = new VBox(10);
        songDetails.setAlignment(Pos.CENTER_LEFT);

        Label songTitle = new Label(song.getName());
        ThemeUtils.setTitleFont(songTitle);

        // Create HBox to hold title and star rating side by side
        HBox titleAndRatingBox = new HBox(10);
        titleAndRatingBox.setAlignment(Pos.CENTER_LEFT);
        titleAndRatingBox.getChildren().addAll(songTitle, createStarRating(song));

        Label artistLabel = new Label("Song by " + ((Song) song).getArtist().getName());
        ThemeUtils.setSmallTitleFont(artistLabel);

        songDetails.getChildren().addAll(titleAndRatingBox, artistLabel);

        songInfoBox.getChildren().addAll(songArt, songDetails);
        return songInfoBox;
    }
}
