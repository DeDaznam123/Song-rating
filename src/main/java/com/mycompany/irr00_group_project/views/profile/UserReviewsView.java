package com.mycompany.irr00_group_project.views.profile;

import java.io.IOException;
import java.util.List;

import com.mycompany.irr00_group_project.controllers.ReviewController;
import com.mycompany.irr00_group_project.controllers.managers.CentralContentManager;
import com.mycompany.irr00_group_project.models.User;
import com.mycompany.irr00_group_project.models.reviewables.Review;
import com.mycompany.irr00_group_project.models.reviewables.ReviewableType;
import com.mycompany.irr00_group_project.models.reviewables.targets.Album;
import com.mycompany.irr00_group_project.models.reviewables.targets.Artist;
import com.mycompany.irr00_group_project.models.reviewables.targets.Song;
import com.mycompany.irr00_group_project.utils.ThemeUtils;
import com.mycompany.irr00_group_project.views.cards.SongCard;
import com.mycompany.irr00_group_project.views.homepage.SlidingNotification;
import com.mycompany.irr00_group_project.views.reviewpage.comment_section.CommentView;

import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

/**
 * UserReviewsView displays a user's reviews in a scrollable view.
 * It allows users to see their reviews and interact with them.
 */
public class UserReviewsView extends VBox {
    
    private VBox reviewsContainer;
    private ReviewController controller;

    /**
     * Constructor for UserReviewsView.
     * Initializes the view with the user's reviews.
     *
     * @param user The user whose reviews are to be displayed.
     */
    public UserReviewsView(User user) {
        try {
            this.controller = new ReviewController(user, 
                this::refreshComments,
                msg -> {
                    if (msg.contains("Rating")) {
                        SlidingNotification.showError(getScene(), msg);
                    } else {
                        SlidingNotification.showSuccess(getScene(), msg);
                    }
                }
            );
        } catch (IOException e) {
            e.printStackTrace();
        } 
            
        reviewsContainer = new VBox(15);

        // Create a scroll pane to hold the reviews
        ThemeUtils.setSecondaryBackgroundRoundedInsets(reviewsContainer);
        reviewsContainer.setPadding(new javafx.geometry.Insets(20));
        ScrollPane scrollPane = new ScrollPane(reviewsContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        getChildren().add(scrollPane);
        List<Review> reviews = controller.getReviews();
        refreshComments(reviews);
    }
    
    /**
     * Refreshes the comments section with the latest reviews.
     * @param reviews The list of reviews to display.
     * This method clears the existing reviews and adds the new ones.
     */
    private void refreshComments(List<Review> reviews) {
        reviewsContainer.getChildren().clear();
        for (Review review : reviews) {
            createReviewContainer(review);
        }
    }

    /**
     * Creates a container for a single review.
     * This method creates a VBox that contains the reviewable item card and the comment view.
     *
     * @param review The review to be displayed.
     * @return A VBox containing the review and its comments.
     */
    private VBox createReviewContainer(Review review) {
        VBox reviewBox = new VBox(10);
        ThemeUtils.setPrimaryBackgroundRoundedInsets(reviewBox);
        reviewBox.setPadding(new javafx.geometry.Insets(20));
        SongCard reviewableCard;
        
        if (review.getTargetType().equals(ReviewableType.SONG)) {
            Song song = (Song) review.getTarget();
            reviewableCard = new SongCard(song);
            reviewableCard.setOnMouseClicked(e -> openSongDetail(song));
        } else if (review.getTargetType().equals(ReviewableType.ALBUM)) {
            Album album = (Album) review.getTarget();
            reviewableCard = new SongCard(createAlbumWrapper(album));
            reviewableCard.setOnMouseClicked(e -> openAlbumDetail(album));
        } else {
            Artist artist = (Artist) review.getTarget();
            reviewableCard = new SongCard(createArtistWrapper(artist));
            reviewableCard.setOnMouseClicked(e -> openArtistDetail(artist));
        }

        CommentView commentView = new CommentView(
            review, controller::handleLike, controller::editReview);
        reviewsContainer.getChildren().add(reviewBox);
        reviewBox.getChildren().addAll(reviewableCard, commentView);
        return reviewBox;
    }

    /**
     * Opens the detail view for a song, album, or artist.
     * This method is called when a reviewable item is clicked.
     *
     * @param reviewable The reviewable item to open in detail view.
     */
    private void openSongDetail(Song song) {
        CentralContentManager.getInstance().showSongDetailView(song);
    }

    /**
     * Opens the detail view for an album.
     * This method is called when an album reviewable item is clicked.
     *
     * @param album The album to open in detail view.
     */
    private void openAlbumDetail(Album album) {
        CentralContentManager.getInstance().showAlbumDetailView(album);
    }
    
    /**
     * Opens the detail view for an artist.
     * This method is called when an artist reviewable item is clicked.
     *
     * @param artist The artist to open in detail view.
     */
    private void openArtistDetail(Artist artist) {
        CentralContentManager.getInstance().showArtistDetailView(artist);
    }

    /**
     * Creates a wrapper song object for an album.
     * This is used to display the album in the same way as a song.
     *
     * @param album The album to wrap.
     * @return A Song object that represents the album.
     */
    private Song createAlbumWrapper(Album album) {
        return new Song(
            album.getId(), 
            album.getName(), 
            album.getImage().getUrl(),
            album.getArtist(), 
            album // Pass the album itself as the artist
        );
    }

    /**
     * Creates a wrapper song object for an artist.
     * This is used to display the artist in the same way as a song.
     *
     * @param artist The artist to wrap.
     * @return A Song object that represents the artist.
     */
    private Song createArtistWrapper(Artist artist) {
        return new Song(
            artist.getId(), 
            artist.getName(), 
            artist.getImage().getUrl(),
            artist, 
            null
        );
    }
}