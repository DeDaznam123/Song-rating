package com.mycompany.irr00_group_project.utils;

import javafx.scene.image.Image;

/**
 * Utility class to load images.
 * This class now uses FallbackAssets for consistent asset management.
 */
public class ImageUtils {

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private ImageUtils() { }

    /**
     * Loads an avatar image from the specified filename or path.
     * Uses FallbackAssets for consistent fallback handling.
     *
     * @param avatarPath The path or filename of the avatar.
     * @return The loaded Image object, or the default avatar if loading fails.
     */
    public static Image loadAvatar(String avatarPath) {
        return Assets.loadAvatarWithFallback(avatarPath);
    }

    /**
     * Loads a Reviewable image from the specified filename or path.
     * Uses FallbackAssets for consistent fallback handling.
     * 
     * @param reviewablePath The path or filename of the reviewable image.
     * @return The loaded Image object, or a placeholder if loading fails.
     */
    public static Image loadImage(String reviewablePath) {
        return Assets.loadImageWithFallback(reviewablePath);
    }

    /**
     * Loads the application logo image.
     * Uses FallbackAssets for consistent fallback handling.
     *
     * @return The loaded Image object for the app logo.
     */
    public static Image loadAppLogo() {
        return Assets.getAppLogo();
    }

    /**
     * Gets the full star image for rating views.
     * Uses FallbackAssets for consistent fallback handling.
     *
     * @return The loaded Image object for a full star.
     */
    public static Image loadFullStarImage() {
        return Assets.getFullStarImage();
    }

    /**
     * Gets the empty star image for rating views.
     * Uses FallbackAssets for consistent fallback handling.
     *
     * @return The loaded Image object for an empty star.
     */
    public static Image loadEmptyStarImage() {
        return Assets.getEmptyStarImage();
    }
}