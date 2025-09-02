package com.mycompany.irr00_group_project.utils;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Utility class for creating styled JavaFX GUI components.
 * Helps with coupling
 */
public class GuiHelper {

    /**
     * Creates a styled button with the specified text and action.
     * @param text the text to display on the button
     * @param onClick the action to perform when the button is clicked
     * @return a styled Button instance
     */
    public static Button createStyledButton(String text, Runnable onClick) {
        Button button = new Button(text);
        ThemeUtils.styleButton(button);
        button.setOnAction(e -> onClick.run());
        return button;
    }

    /**
     * Creates Title Label.
     * @param text the text to display on the label
     * @return a Label instance with title font
     */
    public static Label createTitleLabel(String text) {
        Label label = new Label(text);
        ThemeUtils.setTitleFont(label);
        return label;
    }

    /**
     * Creates a VBox with specified spacing and padding.
     * @param spacing the spacing between children
     * @param padding the padding around the VBox
     * @return a VBox instance with specified properties
     */
    public static VBox createPaddedVBox(double spacing, double padding) {
        VBox vbox = new VBox(spacing);
        vbox.setPadding(new Insets(padding));
        return vbox;
    }

    /**
     * Creates an HBox with specified spacing, padding, and alignment.
     * @param spacing the spacing between children
     * @param padding the padding around the HBox
     * @param alignment the alignment of children within the HBox
     * @return an HBox instance with specified properties
     */
    public static HBox createAlignedHBox(double spacing, double padding, Pos alignment) {
        HBox hbox = new HBox(spacing);
        hbox.setPadding(new Insets(padding));
        hbox.setAlignment(alignment);
        return hbox;
    }

    /**
     * Creates a TextField with a prompt text and specified width.
     * @param prompt the prompt text to display in the TextField
     * @param width the preferred width of the TextField
     * @return a TextField instance with specified properties
     */
    public static TextField createSearchField(String prompt, double width) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setPrefWidth(width);
        return field;
    }

    /**
     * Creates a BorderPane for a navigation bar with left and right components.
     * @param left the Label to display on the left side
     * @param right the HBox to display on the right side
     * @param padding the padding around the BorderPane
     * @return a BorderPane instance configured as a navigation bar
     */
    public static BorderPane createNavBar(Node left, HBox right, Insets padding) {
        BorderPane navBar = new BorderPane();
        navBar.setPadding(padding);
        navBar.setLeft(left);
        navBar.setRight(right);
        BorderPane.setMargin(right, new Insets(0, 20, 0, 20));
        return navBar;
    }
}