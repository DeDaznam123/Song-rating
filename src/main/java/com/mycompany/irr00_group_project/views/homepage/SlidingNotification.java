package com.mycompany.irr00_group_project.views.homepage;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

/**
 * SlidingNotification provides a way to show temporary notifications that slide in and out
 * from the bottom right corner of the application window.
 * It can be used to display success messages, error messages, or general notifications.
 */
public class SlidingNotification {

    /**
     * Shows a sliding notification in the bottom right corner of the given scene.
     * The notification slides in from below, stays for a few seconds, then slides out.
     * @param scene the scene to show the notification in
     * @param message the message to display in the notification
     * @param backgroundColor the background color of the notification
     */
    public static void show(Scene scene, String message, Color backgroundColor) {
        if (!(scene.getRoot() instanceof StackPane)) {
            System.err.println(
                "SlidingNotification: Root is not StackPane, cannot show notification.");
            return;
        }

        StackPane notificationBox = new StackPane();
        notificationBox.setMaxWidth(300);
        notificationBox.setMaxHeight(Region.USE_PREF_SIZE);
        notificationBox.setPrefWidth(300);
        notificationBox.setPrefHeight(80);
        notificationBox.setStyle(
            "-fx-background-color: " + toRgbString(backgroundColor) + ";" 
            + "-fx-background-radius: 10;" 
            + "-fx-padding: 15px;" 
            + "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 5);"
        );

        Label label = new Label(message);
        label.setTextFill(Color.WHITE);
        label.setFont(Font.font("Arial", 16));
        label.setWrapText(true);
        label.setTextAlignment(TextAlignment.CENTER);
        label.setMaxWidth(280);
        notificationBox.getChildren().add(label);

        StackPane.setAlignment(notificationBox, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(notificationBox, new Insets(0, 20, 20, 0)); // bottom margin

        notificationBox.setTranslateY(100);

        StackPane root = (StackPane) scene.getRoot();
        root.getChildren().add(notificationBox);

        Timeline slideIn = new Timeline(
            new KeyFrame(Duration.seconds(0.3), 
                new KeyValue(notificationBox.translateYProperty(), 0, Interpolator.EASE_OUT))
        );

        PauseTransition pause = new PauseTransition(Duration.seconds(3));

        Timeline slideOut = new Timeline(
            new KeyFrame(Duration.seconds(0.3), 
                    new KeyValue(notificationBox.translateYProperty(), 100, Interpolator.EASE_IN))
        );
        slideOut.setOnFinished(e -> root.getChildren().remove(notificationBox));

        slideIn.setOnFinished(e -> pause.play());
        pause.setOnFinished(e -> slideOut.play());

        slideIn.play();
    }

    /**
     * Convenience method to show an error notification with a specific message.
     * @param scene the scene to show the notification in
     * @param message the error message to display
     */
    public static void showError(Scene scene, String message) {
        show(scene, message, Color.web("#e74c3c"));
    }

    /**
     * Convenience method to show a success notification with a specific message.
     * @param scene the scene to show the notification in
     * @param message the success message to display
     */
    public static void showSuccess(Scene scene, String message) {
        show(scene, message, Color.web("#2ecc71"));
    }

    /**
     * Converts a Color object to an RGBA string format for CSS.
     * @param c the Color object to convert
     * @return a string in the format "rgba(r, g, b, a)"
     */
    private static String toRgbString(Color c) {
        return String.format("rgba(%d, %d, %d, %.2f)",
            (int) (c.getRed() * 255),
            (int) (c.getGreen() * 255),
            (int) (c.getBlue() * 255),
            c.getOpacity()
        );
    }
}
