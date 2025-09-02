package com.mycompany.irr00_group_project.utils;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;

/**
 * 
 * Utility class for creating styled JavaFX GUI components.
 * Helps with coupling
 *
 */
public class LogSignGuiHelper {

    /**
     * Creates and configures a GridPane for login or signup forms.
     * @return a configured GridPane
     */
    public static GridPane createFormGrid() {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(50));
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setHgrow(Priority.NEVER);
        col1.setMinWidth(Control.USE_PREF_SIZE);
        grid.getColumnConstraints().add(col1);

        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(Priority.NEVER);
        col2.setMinWidth(Control.USE_PREF_SIZE);
        grid.getColumnConstraints().add(col2);

        return grid;
    }

    /**
     * Creates a navigation bar with a left node and a right HBox.
     * @param left the left node (e.g., a label)
     * @param right the right HBox (e.g., containing buttons)
     * @param padding the padding for the nav bar
     * @return a BorderPane containing the nav bar
     */
    public static BorderPane createNavBar(Node left, HBox right, Insets padding) {
        BorderPane navBar = new BorderPane();
        navBar.setPadding(padding);
        navBar.setLeft(left);
        navBar.setRight(right);
        BorderPane.setMargin(right, new Insets(0, 20, 0, 20));
        return navBar;
    }

    /**
     * Creates a ScrollPane with specific styles.
     * @param content the content to be displayed in the ScrollPane
     * @return a styled ScrollPane
     */
    public static ScrollPane createStyledScrollPane(Node content) {
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        return scrollPane;
    }

    /**
     * Wraps a GridPane in a StackPane.
     * @param grid the GridPane to wrap
     * @return a StackPane containing the grid
     */
    public static StackPane wrapInStackPane(GridPane grid) {
        return new StackPane(grid);
    }
}
