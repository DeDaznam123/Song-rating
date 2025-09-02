package com.mycompany.irr00_group_project.views.homepage.friendslist;

import com.mycompany.irr00_group_project.utils.ThemeUtils;

import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Control;

/**
 * This class represents a dropdown menu for followed actions.
 * It provides options to view reviews and unfollow a friend.
 */
public class FollowedDropdown {
    private final ContextMenu menu;
    private final MenuItem viewReviewsItem;
    private final MenuItem unfollowItem;
    private final MenuItem followItem;

    /**
     * Constructor to initialize the dropdown menu and its items.
     */
    public FollowedDropdown(boolean isFollowing) {
        if (isFollowing) {
            viewReviewsItem = new MenuItem("View Reviews");
            ThemeUtils.styleMenuItem(viewReviewsItem);

            unfollowItem = new MenuItem("Unfollow");
            ThemeUtils.styleMenuItem(unfollowItem);

            menu = new ContextMenu();
            menu.getItems().addAll(viewReviewsItem, unfollowItem);
            ThemeUtils.styleContextMenu(menu);
            followItem = null;
        } else {
            followItem = new MenuItem("Follow");
            ThemeUtils.styleMenuItem(followItem);

            menu = new ContextMenu();
            menu.getItems().add(followItem);
            ThemeUtils.styleContextMenu(menu);
            viewReviewsItem = null; 
            unfollowItem = null;

        }
    }

    /**
     * Displays the dropdown menu anchored to the specified control.
     *
     * @param anchor The control to which the menu is anchored.
     */
    public void show(Control anchor) {
        menu.show(anchor, Side.BOTTOM, 0, 0);
    }

    /**
     * Sets the action handler for the "View Reviews" menu item.
     *
     * @param handler The action handler to be set.
     */
    public void setOnViewReviews(Runnable handler) {
        if (viewReviewsItem != null) {
            viewReviewsItem.setOnAction(e -> handler.run());
        }
    }

    /**
     * Sets the action handler for the "Unfollow" menu item.
     *
     * @param handler The action handler to be set.
     */
    public void setOnUnfollow(Runnable handler) {
        if (unfollowItem != null) {
            unfollowItem.setOnAction(e -> handler.run());
        }
        
    }

    /**
     * Sets the action handler for the "Unfollow" menu item.
     *
     * @param handler The action handler to be set.
     */
    public void setOnFollow(Runnable handler) {
        if (followItem != null) {
            followItem.setOnAction(e -> handler.run());
            
        }
    }

}