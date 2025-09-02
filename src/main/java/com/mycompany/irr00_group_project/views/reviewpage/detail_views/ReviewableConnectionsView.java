package com.mycompany.irr00_group_project.views.reviewpage.detail_views;

import java.util.List;
import java.util.function.Consumer;

import com.mycompany.irr00_group_project.controllers.DetailsController;
import com.mycompany.irr00_group_project.models.reviewables.Reviewable;
import com.mycompany.irr00_group_project.models.reviewables.targets.Album;
import com.mycompany.irr00_group_project.models.reviewables.targets.Artist;
import com.mycompany.irr00_group_project.models.reviewables.targets.Song;
import com.mycompany.irr00_group_project.utils.GuiHelper;
import com.mycompany.irr00_group_project.utils.ThemeUtils;
import com.mycompany.irr00_group_project.views.cards.ArtistCard;
import com.mycompany.irr00_group_project.views.cards.SongCard;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

/**
 * ReviewableConnectionsView shows connections related to a song, artist, or album.
 */
public class ReviewableConnectionsView extends VBox {

    private Consumer<Reviewable> onReviewableSelected;

    /**
     * Creates a ReviewableConnectionsView for a song.
     * @param song The song to display connections for.
     */
    public ReviewableConnectionsView(Song song) {
        DetailsController controller = new DetailsController(song);
        this.setPrefWidth(350);
        this.setSpacing(20);
        this.setPadding(new Insets(20));
        this.setAlignment(Pos.TOP_CENTER);
        setArtistsView(song.getArtist());
        if (song.getAlbum() != null) {
            setAlbumsView(controller.getAlbums());
        }
    }

    /**
     * Creates a ReviewableConnectionsView for an artist.
     * @param artist The artist to display connections for.
     */
    public ReviewableConnectionsView(Artist artist) {
        DetailsController controller = new DetailsController(artist);
        this.setPrefWidth(350);
        this.setSpacing(20);
        this.setPadding(new Insets(20));
        this.setAlignment(Pos.TOP_CENTER);
        if (!controller.getAlbums().isEmpty()) {
            setAlbumsView(controller.getAlbums());
        }
        if (!controller.getSongs().isEmpty()) {
            setSongsView(controller.getSongs());
        }
    }

    /**
     * Creates a ReviewableConnectionsView for an album.
     * @param album The album to display connections for.
     */
    public ReviewableConnectionsView(Album album) {
        DetailsController controller = new DetailsController(album);
        this.setPrefWidth(350);
        this.setSpacing(20);
        this.setPadding(new Insets(20));
        this.setAlignment(Pos.TOP_CENTER);
        setArtistsView(album.getArtist());
        if (!controller.getSongs().isEmpty()) {
            setSongsView(controller.getSongs());
        }
    }

    /**
     * Sets the artists view for the given artist.
     * @param artist The artist to display.
     */
    private void setArtistsView(Artist artist) {
        Label artistsLabel = GuiHelper.createTitleLabel("Artist");
        ThemeUtils.setSmallTitleFont(artistsLabel);

        FlowPane artistsBox = new FlowPane();
        artistsBox.setHgap(10);
        artistsBox.setVgap(10);
        artistsBox.setPadding(new Insets(10));
        artistsBox.setPrefWrapLength(350);
        artistsBox.setAlignment(Pos.CENTER);

        ArtistCard artistCard = new ArtistCard(artist);
        artistCard.setOnMouseClicked(e -> {
            if (onReviewableSelected != null) {
                onReviewableSelected.accept(artist);
            }
        });
        artistsBox.getChildren().add(artistCard);

        if (!artistsBox.getChildren().isEmpty()) {
            VBox artistSection = GuiHelper.createPaddedVBox(10, 0);
            artistSection.getChildren().addAll(artistsLabel, artistsBox);
            this.getChildren().add(artistSection);
        }
    }

    /**
     * Sets the albums view for the given list of albums.
     * @param albums The list of albums to display.
     */
    private void setAlbumsView(List<Album> albums) {
        Label albumsLabel = GuiHelper.createTitleLabel(albums.size() == 1 ? "Album" : "Albums");
        ThemeUtils.setSmallTitleFont(albumsLabel);

        VBox albumsBox = GuiHelper.createPaddedVBox(10, 0);
        for (Album album : albums) {
            Song albumAsSong = createAlbumWrapper(album);
            SongCard albumCard = new SongCard(albumAsSong);
            albumCard.setStyle("-fx-background-color: #0D1B2A; -fx-background-radius: 10;");
            albumCard.setOnMouseClicked(e -> {
                if (onReviewableSelected != null) {
                    onReviewableSelected.accept(album);
                }
            });
            albumsBox.getChildren().add(albumCard);
        }

        if (!albumsBox.getChildren().isEmpty()) {
            VBox albumSection = GuiHelper.createPaddedVBox(10, 0);
            albumSection.getChildren().addAll(albumsLabel, albumsBox);
            this.getChildren().add(albumSection);
        }
    }

    /**
     * Sets the songs view for the given list of songs.
     * @param songs The list of songs to display.
     */
    private void setSongsView(List<Song> songs) {
        Label songsLabel = GuiHelper.createTitleLabel(songs.size() == 1 ? "Song" : "Songs");
        ThemeUtils.setSmallTitleFont(songsLabel);

        VBox songsBox = GuiHelper.createPaddedVBox(10, 0);
        for (Song song : songs) {
            SongCard songCard = new SongCard(song);
            songCard.setStyle("-fx-background-color: #0D1B2A; -fx-background-radius: 10;");
            songCard.setOnMouseClicked(e -> {
                if (onReviewableSelected != null) {
                    onReviewableSelected.accept(song);
                } 
            });
            songsBox.getChildren().add(songCard);
        }

        if (!songsBox.getChildren().isEmpty()) {
            VBox songSection = GuiHelper.createPaddedVBox(10, 0);
            songSection.getChildren().addAll(songsLabel, songsBox);
            this.getChildren().add(songSection);
        }
    }

    /**
     * Sets the listener for when a reviewable item is selected.
     * @param listener The consumer that will handle the selected reviewable.
     */
    public void setOnReviewableSelected(Consumer<Reviewable> listener) {
        this.onReviewableSelected = listener;
    }

    /**
     * Creates a Song wrapper for an album.
     * @param album The album to wrap.
     * @return A Song object representing the album.
     */
    private Song createAlbumWrapper(Album album) {
        return new Song(
            album.getId(),
            album.getName(),
            album.getImage().getUrl(),
            album.getArtist(),
            album
        );
    }
}
