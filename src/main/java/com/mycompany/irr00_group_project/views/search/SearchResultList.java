package com.mycompany.irr00_group_project.views.search;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.mycompany.irr00_group_project.models.reviewables.Reviewable;
import com.mycompany.irr00_group_project.models.reviewables.ReviewableType;
import com.mycompany.irr00_group_project.models.reviewables.targets.Album;
import com.mycompany.irr00_group_project.models.reviewables.targets.Artist;
import com.mycompany.irr00_group_project.models.reviewables.targets.Song;
import com.mycompany.irr00_group_project.utils.GuiHelper;
import com.mycompany.irr00_group_project.utils.ThemeUtils;
import com.mycompany.irr00_group_project.views.cards.ArtistCard;
import com.mycompany.irr00_group_project.views.cards.SongCard;
import com.mycompany.irr00_group_project.views.cards.TopResultCard;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

/**
 * SearchResultList is a custom VBox layout that displays search results.
 * It is used to show a list of search results in a scrollable view.
 */
public class SearchResultList extends VBox {

    private boolean hasActualContent = false;
    private Consumer<Reviewable> onReviewableSelected;

    /**
     * Constructor for the SearchResultList class.
     * Initializes the layout and components.
     */
    public SearchResultList() {
        this.setSpacing(10);
    }

    /**
     * Updates the displayed results based on the provided list of reviewables.
     *
     * @param reviewables The list of reviewables to display.
     */
    public void updateResults(List<Reviewable> reviewables) {
        this.getChildren().clear();

        if (reviewables == null || reviewables.isEmpty()) {
            showNoResults();
            return;
        }

        hasActualContent = true;
        addTopResultSection(reviewables.get(0));
        addSongsSection(reviewables);
        addAlbumsSection(reviewables);
        addArtistsSection(reviewables);
    }

    /**
     * Shows the no results message.
     */
    private void showNoResults() {
        Label noResultsLabel = GuiHelper.createTitleLabel("No results found.");
        ThemeUtils.setTextFont(noResultsLabel);
        this.getChildren().add(noResultsLabel);
        hasActualContent = false;
    }

    /**
     * Adds the top result section.
     *
     * @param topResult The top result to display.
     */
    private void addTopResultSection(Reviewable topResult) {
        TopResultCard topResultCard = new TopResultCard(topResult);
        topResultCard.setOnMouseClicked(e -> onReviewableSelected.accept(topResult));

        Label topResultLabel = GuiHelper.createTitleLabel("Top result");
        ThemeUtils.setSmallTitleFont(topResultLabel);

        VBox topSection = GuiHelper.createPaddedVBox(10, 0);
        topSection.getChildren().addAll(topResultLabel, topResultCard);
        this.getChildren().add(topSection);
    }

    /**
     * Adds the songs section if there are songs to display.
     *
     * @param reviewables The list of all reviewables.
     */
    private void addSongsSection(List<Reviewable> reviewables) {
        List<Song> songs = getItemsByType(reviewables, ReviewableType.SONG, Song.class);
        if (songs.isEmpty()) {
            return;
        }

        Label songsLabel = GuiHelper.createTitleLabel("Songs");
        ThemeUtils.setSmallTitleFont(songsLabel);
        VBox songsBox = GuiHelper.createPaddedVBox(10, 0);

        Reviewable topResult = reviewables.get(0);
        for (Song song : songs) {
            if (song != topResult) { // Don't duplicate the top result
                SongCard songCard = new SongCard(song);
                songCard.setOnMouseClicked(e -> onReviewableSelected.accept(song));
                songsBox.getChildren().add(songCard);
            }
        }

        if (!songsBox.getChildren().isEmpty()) {
            VBox songSection = GuiHelper.createPaddedVBox(10, 0);
            songSection.getChildren().addAll(songsLabel, songsBox);
            this.getChildren().add(songSection);
        }
    }

    /**
     * Adds the albums section if there are albums to display.
     *
     * @param reviewables The list of all reviewables.
     */
    private void addAlbumsSection(List<Reviewable> reviewables) {
        List<Album> albums = getItemsByType(reviewables, ReviewableType.ALBUM, Album.class);
        if (albums.isEmpty()) {
            return;
        }

        Label albumsLabel = GuiHelper.createTitleLabel("Albums");
        ThemeUtils.setSmallTitleFont(albumsLabel);
        VBox albumsBox = GuiHelper.createPaddedVBox(10, 0);

        Reviewable topResult = reviewables.get(0);
        for (Album album : albums) {
            if (album != topResult) { // Don't duplicate the top result
                // Use SongCard as a generic card for albums - create wrapper song
                Song albumAsSong = createAlbumWrapper(album);
                SongCard albumCard = new SongCard(albumAsSong);
                albumCard.setOnMouseClicked(e -> onReviewableSelected.accept(album));
                albumsBox.getChildren().add(albumCard);
            }
        }

        if (!albumsBox.getChildren().isEmpty()) {
            VBox albumSection = GuiHelper.createPaddedVBox(10, 0);
            albumSection.getChildren().addAll(albumsLabel, albumsBox);
            this.getChildren().add(albumSection);
        }
    }

    /**
     * Adds the artists section if there are artists to display.
     *
     * @param reviewables The list of all reviewables.
     */
    private void addArtistsSection(List<Reviewable> reviewables) {
        List<Artist> artists = getItemsByType(reviewables, ReviewableType.ARTIST, Artist.class);
        if (artists.isEmpty()) {
            return;
        }

        Label artistsLabel = GuiHelper.createTitleLabel("Artists");
        ThemeUtils.setSmallTitleFont(artistsLabel);
        FlowPane artistBox = new FlowPane();
        artistBox.setHgap(10);  
        artistBox.setVgap(10);
        artistBox.setPadding(new Insets(10));
        artistBox.setPrefWrapLength(350); 
        artistBox.setAlignment(Pos.CENTER);
        Reviewable topResult = reviewables.get(0);
        for (Artist artist : artists) {
            if (artist != topResult) { 
                ArtistCard artistCard = new ArtistCard(artist);
                artistCard.setOnMouseClicked(e -> onReviewableSelected.accept(artist));
                artistBox.getChildren().add(artistCard);
            }
        }

        if (!artistBox.getChildren().isEmpty()) {
            VBox artistSection = GuiHelper.createPaddedVBox(10, 0);
            artistSection.getChildren().addAll(artistsLabel, artistBox);
            this.getChildren().add(artistSection);
        }
    }

    /**
     * Gets items of a specific type from the reviewables list.
     *
     * @param <T> The type of items to extract
     * @param reviewables The list of reviewables
     * @param type The type to filter by
     * @param clazz The class of the type
     * @return List of items of the specified type
     */
    private <T> List<T> getItemsByType(List<Reviewable> reviewables, 
        ReviewableType type, Class<T> clazz) {

        return reviewables.stream()
            .filter(r -> r.getType() == type)
            .map(clazz::cast)
            .collect(Collectors.toList());
    }

    /**
     * Creates a song wrapper for album display.
     *
     * @param album The album to wrap
     * @return A song representing the album
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

    /**
     * Sets the listener for reviewable selection events.
     *
     * @param listener The listener to be notified when a reviewable is selected.
     */
    public void setOnReviewableSelected(Consumer<Reviewable> listener) {
        this.onReviewableSelected = listener;
    }

    /**
     * Checks if the ResultsView has actual content (reviewables).
     *
     * @return true if there are reviewables displayed, false otherwise.
     */
    public boolean hasContent() {
        return hasActualContent;
    }
}
