package com.mycompany.irr00_group_project.controllers.managers;

import java.util.function.Consumer;

import com.mycompany.irr00_group_project.models.reviewables.Reviewable;
import com.mycompany.irr00_group_project.models.reviewables.targets.Album;
import com.mycompany.irr00_group_project.models.reviewables.targets.Artist;
import com.mycompany.irr00_group_project.models.reviewables.targets.Song;
import com.mycompany.irr00_group_project.views.homepage.CentralView;
import com.mycompany.irr00_group_project.views.homepage.friendslist.FollowedListView;
import com.mycompany.irr00_group_project.views.reviewpage.detail_views.AlbumDetailView;
import com.mycompany.irr00_group_project.views.reviewpage.detail_views.ArtistDetailView;
import com.mycompany.irr00_group_project.views.reviewpage.detail_views.ReviewableConnectionsView;
import com.mycompany.irr00_group_project.views.reviewpage.detail_views.SongDetailView;

import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;

/**
 * Manages the central content area of the application, allowing for
 * switching between different views such as recommendations,
 * search results, and song details.
 */
public class CentralContentManager {

    private static CentralContentManager instance;

    private final BorderPane rootPane;
    private Node currentView;
    private CentralView centralView;

    /**
     * Private constructor for singleton pattern.
     * @param rootPane The root pane of the application.
     * @param centralView The central view of the application.
     * @param onReviewableSelected The callback to be invoked when a reviewable is selected.
     */
    private CentralContentManager(
        BorderPane rootPane,
        CentralView centralView,
        Consumer<Reviewable> onReviewableSelected) {

        this.rootPane = rootPane;
        this.centralView = centralView;
        this.centralView.getSearchResults().setOnReviewableSelected(onReviewableSelected);

        this.currentView = centralView.getRecommendationsBox();
        rootPane.setCenter(currentView);
    }

    /**
     * Initializes the singleton instance. Call this once at application startup.
     */
    public static void initialize(BorderPane rootPane, 
        CentralView centralView, Consumer<Reviewable> onReviewableSelected) {
        if (instance == null) {
            instance = new CentralContentManager(rootPane, centralView, onReviewableSelected);
        }
    }

    /**
     * Returns the singleton instance. Throws if not initialized.
     */
    public static CentralContentManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("CentralContentManager " 
              +  "not initialized. Call initialize() first.");
        }
        return instance;
    }

    /**
     * Shows the recommendations view.
     * This view displays a list of recommended songs, albums, and artists.
     * Returns the right panel to the FollowedListView.
     */
    public void showRecommendationsView() {
        this.switchTo(centralView.getRecommendationsBox());
        this.setRightPanel(new FollowedListView());
    }

    /**
     * Shows the search results view.
     * This view displays a list of followed users and their recent activities.
     * Returns the right panel to the FollowedListView.
     */
    public void showSearchResultsView() {
        FollowedListView friendsBox = new FollowedListView();
        this.setRightPanel(friendsBox);
        this.switchTo(centralView.getSearchResults());
    }

    /**
     * Shows the details of the selected song.
     * @param song The selected song.
     */
    public void showSongDetailView(Song song) {
        SongDetailView detailView = new SongDetailView(song, this::showRecommendationsView);
        this.switchTo(detailView);
        ReviewableConnectionsView songConnectionsView = new ReviewableConnectionsView(song);
        songConnectionsView.setOnReviewableSelected(selected -> {
            if (selected instanceof Album selectedAlbum) {
                showAlbumDetailView(selectedAlbum);
            } else if (selected instanceof Artist selectedArtist) {
                showArtistDetailView(selectedArtist);
            }
        });
        ScrollPane pane = new ScrollPane(songConnectionsView);
        pane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        pane.setFitToWidth(true);
        pane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        this.setRightPanel(pane);
    }

    /**
     * Shows the details of the selected album.
     * @param album The selected album.
     */
    public void showAlbumDetailView(Album album) {
        AlbumDetailView detailView = new AlbumDetailView(album, this::showRecommendationsView);
        this.switchTo(detailView);
        ReviewableConnectionsView albumConnectionsView = new ReviewableConnectionsView(album);
        albumConnectionsView.setOnReviewableSelected(selected -> {
            if (selected instanceof Song selectedSong) {
                showSongDetailView(selectedSong);
            } else if (selected instanceof Artist selectedArtist) {
                showArtistDetailView(selectedArtist);
            }
        });
        ScrollPane pane = new ScrollPane(albumConnectionsView);
        pane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        pane.setFitToWidth(true);
        pane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        this.setRightPanel(pane);
    }

    /**
     * Shows the details of the selected artist.
     * @param artist The selected artist.
     */
    public void showArtistDetailView(Artist artist) {
        ArtistDetailView detailView = new ArtistDetailView(artist, this::showRecommendationsView);
        this.switchTo(detailView);
        ReviewableConnectionsView artistConnectionsView = new ReviewableConnectionsView(artist);
        artistConnectionsView.setOnReviewableSelected(selected -> {
            if (selected instanceof Song selectedSong) {
                showSongDetailView(selectedSong);
            } else if (selected instanceof Album selectedAlbum) {
                showAlbumDetailView(selectedAlbum);
            }
        });
        ScrollPane pane = new ScrollPane(artistConnectionsView);
        pane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        pane.setFitToWidth(true);
        pane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        this.setRightPanel(pane);
    }

    public void setRightPanel(Node node) {
        rootPane.setRight(node);
    }

    /**
     * Switches the current view to the specified new view.
     * @param newView The new view to display.
     */
    private void switchTo(Node newView) {
        if (currentView != newView) {
            rootPane.setCenter(newView);
            currentView.setVisible(false);
            currentView.setManaged(false);

            newView.setVisible(true);
            newView.setManaged(true);

            currentView = newView;
        }
    }
}
