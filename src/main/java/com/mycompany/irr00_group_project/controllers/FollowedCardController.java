package com.mycompany.irr00_group_project.controllers;

import java.util.function.Consumer;

import com.mycompany.irr00_group_project.controllers.managers.SessionManager;
import com.mycompany.irr00_group_project.models.User;
import com.mycompany.irr00_group_project.services.Database;
import com.mycompany.irr00_group_project.views.homepage.friendslist.FollowedCard;
import com.mycompany.irr00_group_project.views.homepage.friendslist.FollowedDropdown;

/**
 * The FollowedCardController class is responsible for handling the
 * logic of a followed user's card in the followed list.
 * It manages the interactions and updates related to a specific followed user.
 */
public class FollowedCardController {
    private final User followedUser;
    private final FollowedCard view;
    private final FollowedDropdown menu;
    private final Runnable onFollowedListUpdate;
    private final Runnable onUnfollowSuccess;
    private final Runnable onFollowSuccess;
    private final Consumer<User> onViewReviews;
   

    /**
     * Constructor for the FollowedCardController.
     *
     * @param followedUser The followed user associated with this controller.
     * @param onFollowedListUpdate A callback to run when the followed list is updated.
     */
    public FollowedCardController(User followedUser, Runnable onFollowedListUpdate, 
        Runnable onUnfollowSuccess, Runnable onFollowSuccess, Consumer<User> onViewReviews) {
        this.followedUser = followedUser;
        this.onViewReviews = onViewReviews;
        this.view = new FollowedCard(followedUser);
        User currentUser = SessionManager.getInstance().getCurrentUser();
        boolean isFollowing = true;
        try {
            if (Database.getInstance().getFollowsTable()
                .getLineId(new String[] {currentUser.getId(), followedUser.getId()}) == null) {
                isFollowing = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.menu = new FollowedDropdown(isFollowing);
        this.menu.setOnViewReviews(() -> {
            if (onViewReviews != null) {
                System.out.println("Switching to user reviews for: " + followedUser.getUsername());
                onViewReviews.accept(followedUser);
            }
        });
        this.onFollowedListUpdate = onFollowedListUpdate;
        this.onUnfollowSuccess = onUnfollowSuccess;
        this.onFollowSuccess = onFollowSuccess;

        view.onMoreClicked(() -> menu.show(view.getdropdownButton()));
        menu.setOnViewReviews(() -> handleViewReviews());
        menu.setOnUnfollow(() -> handleUnfollow());
        menu.setOnFollow(() -> handleFollow());
    }

    /**
     * Handles the action when the "View Reviews" option is selected.
     */
    private void handleViewReviews() {
        if (onViewReviews != null) {
            onViewReviews.accept(followedUser);
        }
    }
    
    /**
     * Handles the action when the "unfollow" option is selected.
     */
    private void handleUnfollow() {
        SessionManager.getInstance().getCurrentUser().unfollow(followedUser.getId());
        onFollowedListUpdate.run();
        onUnfollowSuccess.run();
    }

    /**
     * Handles the action when the "unfollow" option is selected.
     */
    private void handleFollow() {
        SessionManager.getInstance().getCurrentUser().follow(followedUser.getId());
        onFollowedListUpdate.run();
        onFollowSuccess.run();
    }

    /**
     * Gets the followed user associated with this controller.
     *
     * @return The followed user associated with this controller.
     */
    public User getFollowedUser() {
        return followedUser;
    }

    public FollowedDropdown getMenu() {
        return menu;
    }

    /**
     * Gets the view associated with this controller.
     *
     * @return The view associated with this controller.
     */
    public FollowedCard getView() {
        return view;
    }

    public Consumer<User> getOnViewReviews() {
        return this.onViewReviews;
    }

    public Runnable getOnFollowedListUpdate() {
        return this.onFollowedListUpdate;
    }

    public Runnable getOnUnfollowSuccess() {
        return this.onUnfollowSuccess;
    }

    public Runnable getOnFollowSuccess() {
        return this.onFollowSuccess;
    }

}
