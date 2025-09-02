package com.mycompany.irr00_group_project.utils;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

/**
 * ThemeUtils provides utility methods for styling UI components in the
 * application.
 * It includes methods for setting fonts, backgrounds, and button shapes.
 */
public final class ThemeUtils {

    // === Font styles ===
    private static final Font SMALL_FONT = Font.font("Montserrat", FontWeight.NORMAL, 8);
    private static final Font TEXT_FONT = Font.font("Montserrat", FontWeight.NORMAL, 12);
    private static final Font SMALL_TITLE_FONT = Font.font("Montserrat", FontWeight.BOLD, 20);
    private static final Font TITLE_FONT = Font.font("Montserrat", FontWeight.BOLD, 35);

    // === Primary background styles ===
    private static final Background PRIMARY_BACKGROUND = 
        createBackground(Colors.PRIMARY, 0, Insets.EMPTY);

    private static final Background PRIMARY_BACKGROUND_ROUNDED = 
        createBackground(Colors.PRIMARY, 10, Insets.EMPTY);

    private static final Background PRIMARY_BACKGROUND_ROUNDED_INSETS = 
        createBackground(Colors.PRIMARY, 10,
            new Insets(10));

    // === Secondary background styles ===
    private static final Background SECONDARY_BACKGROUND = 
        createBackground(Colors.SECONDARY, 0, Insets.EMPTY);

    private static final Background SECONDARY_BACKGROUND_ROUNDED = 
        createBackground(Colors.SECONDARY, 10, Insets.EMPTY);

    private static final Background SECONDARY_BACKGROUND_ROUNDED_INSETS = 
        createBackground(Colors.SECONDARY, 10,
            new Insets(10));

    // === Tertiary background styles ===
    private static final Background TERTIARY_BACKGROUND = 
        createBackground(Colors.TERTIARY, 0, Insets.EMPTY);

    private static final Background TERTIARY_BACKGROUND_ROUNDED = 
        createBackground(Colors.TERTIARY, 10, Insets.EMPTY);

    private static final Background TERTIARY_BACKGROUND_ROUNDED_INSETS = 
        createBackground(Colors.TERTIARY, 10,
            new Insets(10));

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private ThemeUtils() {
    }

    /**
     * Creates a Background object with the specified color, radius, and insets.
     *
     * @param color  The color of the background.
     * @param radius The corner radius of the background.
     * @param insets The insets of the background.
     * @return A Background object with the specified properties.
     */
    private static Background createBackground(Paint color, double radius, Insets insets) {
        return new Background(new BackgroundFill(color, new CornerRadii(radius), insets));
    }

    /**
     * Applies the specified font to the given label.
     *
     * @param label The label to which the font will be applied.
     * @param font  The font to be applied to the label.
     */
    public static void applyFont(Label label, Font font) {
        label.setFont(font);
        label.setTextFill(Colors.TEXT);
    }

    /**
     * Sets the default font for text.
     */
    public static void setTextFont(Label label) {
        applyFont(label, TEXT_FONT);
    }

    /**
     * Sets the default font for titles.
     */
    public static void setTitleFont(Label label) {
        applyFont(label, TITLE_FONT);
    }

    /**
     * Sets the default font for small text.
     */
    public static void setSmallFont(Label label) {
        applyFont(label, SMALL_FONT);
    }

    /**
     * Sets the default font for small titles.
     */
    public static void setSmallTitleFont(Label label) {
        applyFont(label, SMALL_TITLE_FONT);
    }

    /**
     * Sets the primary background for the given region.
     *
     * @param region The region to which the primary background will be applied.
     */
    public static void setPrimaryBackground(Region region) {
        region.setBackground(PRIMARY_BACKGROUND);
    }

    /**
     * Sets the secondary background for the given region.
     *
     * @param region The region to which the secondary background will be applied.
     */
    public static void setSecondaryBackground(Region region) {
        region.setBackground(SECONDARY_BACKGROUND);
    }

    /**
     * Sets the tertiary background for the given region.
     *
     * @param region The region to which the tertiary background will be applied.
     */
    public static void setTertiaryBackground(Region region) {
        region.setBackground(TERTIARY_BACKGROUND);
    }

    /**
     * Sets the primary background and rounds the corners of the given region.
     */
    public static void setPrimaryBackgroundRounded(Region region) {
        region.setBackground(PRIMARY_BACKGROUND_ROUNDED);
    }

    /**
     * Sets the secondary background and rounds the corners of the given region.
     */
    public static void setSecondaryBackgroundRounded(Region region) {
        region.setBackground(SECONDARY_BACKGROUND_ROUNDED);
    }

    /**
     * Sets the tertiary background and rounds the corners of the given region.
     */
    public static void setTertiaryBackgroundRounded(Region region) {
        region.setBackground(TERTIARY_BACKGROUND_ROUNDED);
    }

    /**
     * Sets the primary background with rounded corners and insets for the given
     * region.
     */
    public static void setPrimaryBackgroundRoundedInsets(Region region) {
        region.setBackground(PRIMARY_BACKGROUND_ROUNDED_INSETS);
    }

    /**
     * Sets the secondary background with rounded corners and insets for the given
     * region.
     */
    public static void setSecondaryBackgroundRoundedInsets(Region region) {
        region.setBackground(SECONDARY_BACKGROUND_ROUNDED_INSETS);
    }

    /**
     * Sets the tertiary background with rounded corners and insets for the given
     * region.
     */
    public static void setTertiaryBackgroundRoundedInsets(Region region) {
        region.setBackground(TERTIARY_BACKGROUND_ROUNDED_INSETS);
    }

    /**
     * Applies a circular shape to the given button.
     *
     * @param btn The button to be styled as a circle.
     */
    public static void makeCircleButton(Button btn) {
        btn.setPrefSize(40, 40);
        btn.setShape(new Circle(20));
    }

    /**
     * Applies a small circular shape to the given button.
     *
     * @param btn The button to be styled as a circle.
     */
    public static void makeSmallCircleButton(Button btn) {
        btn.setPrefSize(20, 20);
        btn.setShape(new Circle(10));
    }

    /**
     * Applies style to menu items.
     */
    public static void styleMenuItem(MenuItem menuItem) {
        menuItem.setStyle(
                "-fx-background-color: transparent;"
                        + "-fx-text-fill: " + Colors.SECONDARY.toString().replace("0x", "#")
                        + "; -fx-padding: 6 12 6 12;"
                        + "-fx-text-fill: " + Colors.TEXT.toString().replace("0x", "#")
                        + "; -fx-background-radius: 12;"
                        + "-fx-border-radius: 12;");
    }

    /**
     * Applies style to the context menu.
     */
    public static void styleContextMenu(ContextMenu contextMenu) {
        contextMenu.setStyle(
                "-fx-background-color: "
                        + Colors.SECONDARY.toString().replace("0x", "#")
                        + "; -fx-background-radius: 10;"
                        + "-fx-padding: 5;");
    }

    /**
     * Applies style to buttons.
     */
    public static void styleButton(Button button) {
        button.setStyle(
                "-fx-background-color: " + Colors.TERTIARY.toString().replace("0x", "#") + ";"
                        + "-fx-text-fill: " + Colors.TEXT.toString().replace("0x", "#") + ";"
                        + "-fx-background-radius: 5;");
        button.setPadding(new Insets(5, 10, 5, 10));
    }

    /**
     * Applies a circular clip to an ImageView.
     * 
     * @param imageView The ImageView to apply the circular clip to.
     * @param radius    The radius of the circular clip.
     */
    public static void applyCircularClip(ImageView imageView, double radius) {
        Circle clip = new Circle(radius, radius, radius);
        imageView.setClip(clip);
    }

    /**
     * Apply the “primary” rounded-background style with insets.
     */
    public static void setRoundedPrimaryBackgroundWithInsets(Node node) {
        node.getStyleClass().add("rounded-primary-bg-with-insets");
    }

    /**
     * Apply the “secondary” rounded-background style.
     */
    public static void setRoundedSecondaryBackground(Pane pane) {
        pane.getStyleClass().add("rounded-secondary-bg");
    }

    /**
     * Apply the “secondary” rounded-background style with insets.
     */
    public static void applyRoundedClip(ImageView imageView, int i) {
        Rectangle clip = new Rectangle(imageView.getFitWidth(), imageView.getFitHeight());
        clip.setArcWidth(i);
        clip.setArcHeight(i);
        imageView.setClip(clip);
    }
}