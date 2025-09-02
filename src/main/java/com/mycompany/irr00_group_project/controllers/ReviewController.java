package com.mycompany.irr00_group_project.controllers;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

import com.mycompany.irr00_group_project.services.Database;
import com.mycompany.irr00_group_project.models.User;
import com.mycompany.irr00_group_project.models.reviewables.Review;
import com.mycompany.irr00_group_project.services.Table;

import com.mycompany.irr00_group_project.models.reviewables.Reviewable;

/**
 * The ReviewController class handles the logic for managing reviews
 * in the review section of the application. It allows users to like reviews
 * and add new reviews.
 */
public class ReviewController {

    private final List<Review> reviewList;
    private final Consumer<List<Review>> onReviewsChanged;
    private final Consumer<String> notification;
    private final Reviewable target;
    private final User currentUser;
    private final Table reviewsTable;

    /**
     * Constructs a REviewController with a target ID and a callback for when reviews change.
     *
     * @param target The ID of the target for which reviews are managed.
     * @param onReviewsChanged A callback that is triggered when the reviews list changes.
     */
    public ReviewController(Reviewable target, Consumer<List<Review>> onReviewsChanged, 
        Consumer<String> notification, User currentUser) throws IOException {
        this.target = target;
        this.currentUser = currentUser;
        this.notification = notification;
        this.onReviewsChanged = onReviewsChanged;
        this.reviewList = Database.getInstance().getReviews(target);
        this.reviewsTable = Database.getInstance().getReviewsTable();
    }

    /**
     * Constructs a REviewController with a user ID and a callback for when reviews change.
     *
     * @param user The ID of the user for which reviews are managed.
     * @param onReviewsChanged A callback that is triggered when the reviews list changes.
     */
    public ReviewController(User user, Consumer<List<Review>> onReviewsChanged, 
        Consumer<String> notification) throws IOException {
        this.notification = notification;
        this.currentUser = user;
        this.target = null; 
        this.onReviewsChanged = onReviewsChanged;
        this.reviewList = Database.getInstance().getReviewsByUser(user);
        this.reviewsTable = Database.getInstance().getReviewsTable();
    }
    
    /**
     * Returns the current list of reviews.
     *
     * @return The list of reviews.
     */
    public List<Review> getReviews() {
        return reviewList;
    }

    /**
     * Handles the like action for a reviews.
     * Toggles the like status - if user has already liked the reviews, it unlikes it.
     * If user hasn't liked the reviews, it adds a like.
     *
     * @param review The reviews to like/unlike.
     */
    public void handleLike(Review review) {
        // Get current user
        
        if (currentUser == null) {
            System.err.println("No user logged in - cannot like review");
            return;
        }
        if (review.getText() == null) {
            System.err.println("There isnt a comment - cannot like");
            return;
        }

        String userId = currentUser.getId();
        String reviewId = review.getId();
        
        if (reviewId == null) {
            System.err.println("Review has no ID - cannot like");
            return;
        }
        
        try {
            Database db = Database.getInstance();
            String likeId = db.getLikesTable().getLineId(new String[]{userId, reviewId});
            if (likeId != null) {
                db.getLikesTable().deleteLine(likeId);
                review.unlike();
            } else {
                // Add like.
                db.getLikesTable().addLine(new String[]{userId, reviewId});
                review.like();
            }
            notifyReviewsChanged();
        
        } catch (Exception e) {
            System.err.println("Error handling a like: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Adds a new comment to the comment section.
    //  * @param rating The rating given to the review (1-5).
     * @param text The text of the comment.
     */
    public void addReview(String text, int rating) {
        
        if (currentUser == null) {
            System.err.println("No user logged in - cannot add review");
            return;
        }

        try {
            Review review = new Review(currentUser, text, target, rating);
            reviewList.add(0, review);
            notifyReviewsChanged();
            reviewsTable.addLine(new String[]{
                review.getUser().getId(),
                review.getText(),
                review.getTimestamp(),
                review.getTarget().getId(),
                review.getRating() + "",
                review.getTargetType().toString()
            });
        } catch (IllegalArgumentException e) {
            notification.accept(e.getMessage());
            return;
        } catch (Exception e) {
            System.err.println("Error adding review: Plese inout a valid rating.");
            e.printStackTrace();
            notification.accept("An error occurred while adding the review.");
            return;
        }
        
    }

    /**
     * Edits an existing review.
     * @param review The review to edit.
     * @param newText The new text for the review.
     * @param newRating The new rating for the review (1-5).
     */
    public void editReview(Review review, String newText, int newRating) {
        
        if (currentUser == null) {
            System.err.println("No user logged in - cannot add review");
            return;
        }
        reviewList.remove(review);
        Review newReview = new Review(currentUser, newText, review.getTarget(), newRating);
        reviewList.add(0, newReview);
        try {
            String id = reviewsTable.getLineId(new String[]{
                review.getUser().getId(),
                review.getText(),
                review.getTimestamp(),
                review.getTarget().getId(),
                review.getRating() + "",
                review.getTargetType().toString()
            });

            if (id == null) {
                System.err.println("Review not found in database - cannot edit");
                notification.accept("Review not found in database.");
                return;
            }

            reviewsTable.changeLine(id, new String[]{
                newReview.getUser().getId(),
                newReview.getText(),
                newReview.getTimestamp(),
                newReview.getTarget().getId(),
                newReview.getRating() + "",
                newReview.getTargetType().toString()
            });

            notifyReviewsChanged();
            
        } catch (IOException e) {
            System.err.println("Error editing review: " + e.getMessage());
            e.printStackTrace();
            notification.accept("An error occurred while editing the review.");
            return;
        }

    }
    
    /**
     * Notifies observers that the reviews list has changed.
     */
    private void notifyReviewsChanged() {
        if (onReviewsChanged != null) {
            onReviewsChanged.accept(reviewList);
            notification.accept("Review posted successfully.");
        }
    }
}
