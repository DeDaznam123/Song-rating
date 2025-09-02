package com.mycompany.irr00_group_project.models.reviewables;

import com.mycompany.irr00_group_project.models.User;

/**
 * Represents a review for a song in the application.
 */
public class Review {
    private final String id;
    private final User user;
    private final String text;
    private int likes;
    private final Reviewable target;
    private final int rating;
    private final String timestamp;

    
    /**
     * Constructor for a new review.
     * @param user   The user writing the review.
     * @param text   The text of the review.
     * @param target The reviewable item being reviewed (e.g., a song).
     * @param rating   The rating given to the song (1-5).
     */
    public Review(User user, String text, Reviewable target, int rating) {
        this.id = null; // Will be set when added to database
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        this.user = user;
        this.text = text;
        this.likes = 0;
        this.target = target;
        this.rating = rating;
        this.timestamp = java.time.LocalDateTime.now().toString();
    }

    /**
     * Constructor for an existing review.
     * @param id     The unique identifier for the review.
     * @param user   The user writing the review.
     * @param text   The text of the review.
     * @param likes  The number of likes for this review.
     * @param target The reviewable item being reviewed (e.g., a song).
     * @param rating   The rating given to the song (1-5).
     * @param timestamp The timestamp of when the review was created.
     */
    public Review(String id, User user, String text, int likes, 
        String timestamp, int rating, Reviewable target) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        this.id = id;
        this.user = user;
        this.text = text;
        this.likes = likes;
        this.target = target;
        this.rating = rating;
        this.timestamp = timestamp; 
    }

    // Accessors.

    public Reviewable getTarget() {
        return target;
    }

    public ReviewableType getTargetType() {
        return target.getType();
    }

    public User getUser() {
        return user;
    }

    public int getRating() {
        return rating;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public int getLikes() {
        return likes;
    }

    // Mutators.
    
    /**
     * Increases the like count for this review by one.
     */
    public void like() {
        likes++;
    }

    /**
     * Decreases the like count for this review by one.
     */
    public void unlike() {
        if (likes > 0) {
            likes--;
        }
    }
}
