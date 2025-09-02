package com.mycompany.irr00_group_project.controllers.managers;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.mycompany.irr00_group_project.models.User;
import com.mycompany.irr00_group_project.models.reviewables.Reviewable;
import com.mycompany.irr00_group_project.services.CommentRecommenderService;
import com.mycompany.irr00_group_project.services.RatingRecommenderService;

/**
 * SessionManager is a singleton class that manages the user session.
 * It stores the current user and provides methods to set, get, and clear the session.
 */
public class SessionManager {
    private static SessionManager instance;
    private User currentUser;
    private List<Reviewable> recommendations = new ArrayList<>();

    /**
     * Private constructor to prevent instantiation.
     */
    private SessionManager() {
        this.currentUser = null;
    }

    /**
     * Singleton instance of SessionManager.
     * @return The single instance of SessionManager.
     */
    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    /**
     * Sets the current user for the session.
     * @param user The user to set as the current user.
     */
    public void setCurrentUser(User user) {
        this.currentUser = user;
        computeAndSetRecommendationsBlocking();
        try {
            RatingRecommenderService recommenderService = new RatingRecommenderService();
            List<Reviewable> newRecs = recommenderService.getRecommendations(user.getId(), 10);

            Set<String> existingKeys = recommendations.stream()
                .map(r -> r.getId() + "|" + r.getType())
                .collect(Collectors.toSet());

            List<Reviewable> filtered = newRecs.stream()
                .filter(r -> !existingKeys.contains(r.getId() + "|" + r.getType()))
                .collect(Collectors.toList());

            recommendations.addAll(filtered);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Waits for recommendations to be computed and adds them to the list.
     * This method blocks until recommendations are ready.
     */
    private void computeAndSetRecommendationsBlocking() {
        if (currentUser == null) {
            System.err.println("No user set in session.");
            return;
        }
        try {
            CommentRecommenderService.setCurrentUserId(currentUser.getId());
            List<Reviewable> recs = CommentRecommenderService.getRecommendations();
            synchronized (this) {
                recommendations = recs;
            }
        } catch (Exception e) {
            e.printStackTrace();
            recommendations = new ArrayList<>();
        }
    }

    /**
     * Adds a list of reviewable items to the recommendations.
     * @param reviewable The list of reviewable items to add.
     */
    public void addRecommendations(List<Reviewable> reviewable) {
        if (recommendations == null) {
            recommendations = new ArrayList<>();
        }
        recommendations.addAll(reviewable);
    }

    public List<Reviewable> getRecommendations() {
        return recommendations;
    }

    /**
     * Gets the current user for the session.
     * @return The current user.
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Clears the session.
     */
    public void clearSession() {
        this.currentUser = null;
    }
}
