package com.mycompany.irr00_group_project.utils;

import javafx.scene.image.Image;

/**
 * FallbackAssets provides centralized access to fallback images and assets
 * used throughout the application when primary assets are missing or fail to load.
 */
public final class Assets {

    // === Asset Paths ===
    private static final String ASSETS_BASE_PATH = "/db/images/";
    private static final String MISSING_ASSETS_PATH = ASSETS_BASE_PATH + "missing_assets/";
    private static final String LOGO_PATH = ASSETS_BASE_PATH + "logo/";

    // === Image Paths ===
    private static final String DEFAULT_AVATAR_PATH = MISSING_ASSETS_PATH + "default_avatar.jpg";
    private static final String MISSING_IMAGE_PATH = MISSING_ASSETS_PATH + "missing.jpg";
    private static final String APP_LOGO_PATH = LOGO_PATH + "fent.png";
    private static final String FULL_STAR_PATH = ASSETS_BASE_PATH + "stars/full_star.png";
    private static final String EMPTY_STAR_PATH = ASSETS_BASE_PATH + "stars/empty_star.png";

    // === Cached Images ===
    private static Image defaultAvatarImage;
    private static Image missingImage;
    private static Image appLogoImage;
    private static Image placeholderReviewableImage;

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private Assets() {}

    /**
     * Gets the default avatar image used when a user has no profile picture.
     *
     * @return The default avatar Image object.
     */
    public static Image getDefaultAvatar() {
        if (defaultAvatarImage == null) {
            defaultAvatarImage = loadImageSafely(DEFAULT_AVATAR_PATH);
        }
        return defaultAvatarImage;
    }

    /**
     * Gets the missing image placeholder used when an image fails to load.
     *
     * @return The missing image placeholder Image object.
     */
    public static Image getMissingImage() {
        if (missingImage == null) {
            missingImage = loadImageSafely(MISSING_IMAGE_PATH);
        }
        return missingImage;
    }

    /**
     * Gets the application logo image.
     *
     * @return The app logo Image object.
     */
    public static Image getAppLogo() {
        if (appLogoImage == null) {
            appLogoImage = loadImageSafely(APP_LOGO_PATH);
        }
        return appLogoImage;
    }

    /**
     * Gets the full star image used in rating views.
     *
     * @return The full star Image object.
     */
    public static Image getFullStarImage() {
        return loadImageSafely(FULL_STAR_PATH);
    }

    /**
     * Gets the empty star image used in rating views.
     *
     * @return The empty star Image object.
     */
    public static Image getEmptyStarImage() {
        return loadImageSafely(EMPTY_STAR_PATH);
    }

    /**
     * Gets the placeholder Reviewable art image.
     *
     * @return The placeholder Reviewable Image object.
     */
    public static Image getPlaceholderReviewable() {
        if (placeholderReviewableImage == null) {
            placeholderReviewableImage = loadImageSafely(MISSING_IMAGE_PATH);
        }
        return placeholderReviewableImage;
    }

    /**
     * Gets the path to the default avatar image.
     *
     * @return The default avatar image path as a String.
     */
    public static String getDefaultAvatarPath() {
        return DEFAULT_AVATAR_PATH;
    }

    /**
     * Gets the path to the missing image placeholder.
     *
     * @return The missing image path as a String.
     */
    public static String getMissingImagePath() {
        return MISSING_IMAGE_PATH;
    }

    /**
     * Gets the path to the application logo.
     *
     * @return The app logo path as a String.
     */
    public static String getAppLogoPath() {
        return APP_LOGO_PATH;
    }

    /**
     * Safely loads an image from the given path, returning a fallback if loading fails.
     *
     * @param imagePath The path to the image to load.
     * @return The loaded Image object, or a basic fallback if loading fails.
     */
    private static Image loadImageSafely(String imagePath) {
        try {
            return new Image(Assets.class.getResourceAsStream(imagePath));
        } catch (Exception e) {
            System.err.println("Failed to load image: " + imagePath + " - " + e.getMessage());
            // Return a simple colored rectangle as ultimate fallback
            return createFallbackImage();
        }
    }

    /**
     * Creates a basic fallback image when even the fallback assets fail to load.
     *
     * @return A simple Image object as the ultimate fallback.
     */
    private static Image createFallbackImage() {
        // Create a simple 1x1 pixel transparent image as the ultimate fallback
        try {
            return new Image(
                "data:image/png;base64,"
                + "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8/5+hHgAHggJ/"
                + "PchI7wAAAABJRU5ErkJggg=="
            );
        } catch (Exception e) {
            // If even this fails, return null and let JavaFX handle it
            return null;
        }
    }

    /**
     * Loads an image with fallback handling. If the primary image fails to load,
     * returns the missing image placeholder.
     *
     * @param imagePath The path to the primary image to load.
     * @return The loaded Image object, or the missing image placeholder if loading fails.
     */
    public static Image loadImageWithFallback(String imagePath) {
        try {
            if (imagePath == null || imagePath.isEmpty()) {
                return getMissingImage();
            }
            
            // Try to load the image
            Image image = new Image(imagePath);
            
            // Check if the image loaded successfully
            if (image.isError()) {
                return getMissingImage();
            }
            
            return image;
        } catch (Exception e) {
            return getMissingImage();
        }
    }

    /**
     * Loads an avatar image with fallback to default avatar.
     *
     * @param avatarPath The path to the avatar image to load.
     * @return The loaded avatar Image object, or the default avatar if loading fails.
     */
    public static Image loadAvatarWithFallback(String avatarPath) {
        try {
            if (avatarPath == null || avatarPath.isEmpty()) {
                return getDefaultAvatar();
            }
            
            // Try to load the avatar
            Image avatar = new Image(avatarPath);
            
            // Check if the avatar loaded successfully
            if (avatar.isError()) {
                return getDefaultAvatar();
            }
            
            return avatar;
        } catch (Exception e) {
            System.err.println("Failed to load avatar: " + avatarPath + " - " + e.getMessage());
            return getDefaultAvatar();
        }
    }

    /**
     * Clears all cached images. Useful for memory management or when assets are updated.
     */
    public static void clearCache() {
        defaultAvatarImage = null;
        missingImage = null;
        appLogoImage = null;
        placeholderReviewableImage = null;
    }
}
