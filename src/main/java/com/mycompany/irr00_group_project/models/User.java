package com.mycompany.irr00_group_project.models;

import java.util.List;

import com.mycompany.irr00_group_project.services.Database;
import com.mycompany.irr00_group_project.services.Table;
import com.mycompany.irr00_group_project.utils.ImageUtils;

import javafx.scene.image.Image;

/**
 * Represents a user in the application.
 */
public class User {
    private String id;
    private String username;
    private String email;
    private Image avatar;
    private String avatarPath;

    /**
     * Constructor for the user.
     * @param id the id of the user
     * @param username the users username
     * @param email the users email
     */
    public User(String id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.avatarPath = "/db/images/missing_assets/default_avatar.jpg";
        this.avatar = ImageUtils.loadAvatar(avatarPath);
    }

    /**
     * Unfollows a user.
     *
     * @param friendId The ID of the user to unfollow.
     * 
     */
    public void unfollow(String friendId) {
        try {
            if (friendId == null || this.id == null) {
                throw new IllegalArgumentException("Friend ID or User ID cannot be null");
            }
            Table followedTable = Database.getInstance().getFollowsTable();
            String lineId = followedTable.getLineId(new String[] {this.id, friendId});
            if (lineId == null) {
                throw new IllegalArgumentException("You are not following this user");
            }
            followedTable.deleteLine(lineId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Follows a user.
     *
     * @param friendId The ID of the user to Follow.
     */
    public void follow(String friendId) {
        try {
            if (friendId == null || this.id == null) {
                throw new IllegalArgumentException("Friend ID or User ID cannot be null");
            }
            Table followedTable = Database.getInstance().getFollowsTable();
            String lineId = followedTable.getLineId(new String[] {this.id, friendId});
            if (lineId != null) {
                throw new IllegalArgumentException("You are following this user");
            }
            followedTable.addLine(new String[] {this.id, friendId});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public Image getAvatar() {
        return avatar;
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    /**
     * Returns the followed users of this user.
     *
     * @return A list of users that this user follows.
     */
    public List<User> getFollowedUsers() {
        try {
            return Database.getInstance().getFollowedUsers(this.id);
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    // Mutators.

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAvatar(Image avatar) {
        this.avatar = avatar;
    }
}
