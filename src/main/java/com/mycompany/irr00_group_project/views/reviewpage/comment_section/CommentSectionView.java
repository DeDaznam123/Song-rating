package com.mycompany.irr00_group_project.views.reviewpage.comment_section;

import java.util.List;

import com.mycompany.irr00_group_project.controllers.ReviewController;
import com.mycompany.irr00_group_project.controllers.managers.SessionManager;
import com.mycompany.irr00_group_project.models.reviewables.Reviewable;
import com.mycompany.irr00_group_project.utils.ThemeUtils;
import com.mycompany.irr00_group_project.views.homepage.SlidingNotification;
import com.mycompany.irr00_group_project.models.User;
import com.mycompany.irr00_group_project.models.reviewables.Review;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

/**
 * The CommentSectionView class represents a section of the user interface
 * that displays a list of comments. It allows for the display and interaction
 * with comments, including liking them.
 */
public class CommentSectionView extends VBox {
    private ReviewController controller;
    private VBox commentsContainer;
    private ScrollPane scrollPane;

    /**
     * Constructs a CommentSectionView with a controller to handle comment actions.
     *
     * @param target The Reviewable target for which comments are displayed.
     */
    public CommentSectionView(Reviewable target) {
        setSpacing(15);
        setPadding(new Insets(20));
        ThemeUtils.setPrimaryBackgroundRounded(this);

        // Reviews label.
        Label reviewsLabel = new Label("Reviews");
        ThemeUtils.setSmallTitleFont(reviewsLabel);        

        CommentInputView inputView = new CommentInputView((text, rating) -> {
            controller.addReview(text, rating);
        });

        // Create a container for comments
        commentsContainer = new VBox(15);
        
        // Create a scroll pane to hold the comments
        scrollPane = new ScrollPane(commentsContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        // Add the input view and scroll pane to the main container
        

        // Create the controller and pass a callback to update the view when comments change
        try {
            this.controller = new ReviewController(target, 
                this::refreshComments,
                msg -> {
                    if (msg.contains("Rating")) {
                        SlidingNotification.showError(getScene(), msg);
                    } else {
                        SlidingNotification.showSuccess(getScene(), msg);
                    }
                },
                SessionManager.getInstance().getCurrentUser()
            );
            
            // Initial display of comments
            displayComments(controller.getReviews());
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        User currentUser = SessionManager.getInstance().getCurrentUser();
        boolean hasReviewed = controller.getReviews().stream()
            .anyMatch(r -> r.getUser().getId().equals(currentUser.getId()));
        if (!hasReviewed) {
            getChildren().addAll(reviewsLabel, inputView, scrollPane);
        } else {
            getChildren().addAll(reviewsLabel, scrollPane);
        }
        
    }

    /**
     * Returns the controller associated with this view.
     *
     * @return The CommentController.
     */
    public ReviewController getController() {
        return controller;
    }

    /**
     * Refreshes the comment section with a new list of comments.
     *
     * @param comments The new list of comments to display.
     */
    public void refreshComments(List<Review> comments) {
        commentsContainer.getChildren().clear();
        displayComments(comments);
    }
    
    /**
     * Displays the given list of comments in the view.
     *
     * @param comments The list of comments to display.
     */
    private void displayComments(List<Review> comments) {
        for (Review comment : comments) {
            CommentView view = new CommentView(
                comment, controller::handleLike, controller::editReview);
            commentsContainer.getChildren().add(view);
        }
    }
}
