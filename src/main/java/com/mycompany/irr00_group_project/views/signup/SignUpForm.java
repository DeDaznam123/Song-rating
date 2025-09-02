package com.mycompany.irr00_group_project.views.signup;

import com.mycompany.irr00_group_project.utils.ThemeUtils;

import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

/**
 * The SignUpForm class represents the sign-up form of the application.
 */
public class SignUpForm {
    private final TextField usernameField = new TextField();
    private final TextField emailField = new TextField();
    private final PasswordField passwordField = new PasswordField();
    private final PasswordField confirmPasswordField = new PasswordField();

    private final Text actiontarget = new Text();

    /**
     * Constructor for the SignUpForm class.
     * Initializes the username and password fields.
     *
     * @param grid The grid pane to which the form will be added.
     */
    public void addToGrid(GridPane grid) {
        Label userName = new Label("User Name:");
        ThemeUtils.setTextFont(userName);
        grid.add(userName, 0, 1);
        grid.add(usernameField, 1, 1);

        Label email = new Label("Email:");
        ThemeUtils.setTextFont(email);
        grid.add(email, 0, 2);
        grid.add(emailField, 1, 2);

        Label pw = new Label("Password:");
        ThemeUtils.setTextFont(pw);
        grid.add(pw, 0, 4);
        grid.add(passwordField, 1, 4);

        Label cpw = new Label("Confirm Password:");
        ThemeUtils.setTextFont(cpw);
        grid.add(cpw, 0, 5);
        grid.add(confirmPasswordField, 1, 5);

        // // Action target message (like "Passwords don't match")
        // grid.add(actiontarget, 1, 7);
    }

    /**
     * Gets the username entered in the form.
     *
     * @return The username as a String.
     */
    public String getUsername() {
        return usernameField.getText();
    }

    /**
     * Gets the email entered in the form.
     *
     * @return The email as a String.
     */
    public String getEmail() {
        return emailField.getText();
    }

    /**
     * Gets the password entered in the form.
     *
     * @return The password as a String.
     */
    public String getPassword() {
        return passwordField.getText();
    }

    /**
     * Creates and returns a Scene containing the sign-up form.
     *
     * @return a Scene with the sign-up form.
     */
    public javafx.scene.Scene getScene() {
        GridPane grid = new GridPane();
        addToGrid(grid);
        return new javafx.scene.Scene(grid, 400, 300);
    }

    /**
     * Gets the confirmed password entered in the form.
     *
     * @return The confirmed password as a String.
     */

    public String getConfirmPassword() {
        return confirmPasswordField.getText();
    }

    /**
     * Gets the action target text.
     *
     * @return The action target as a Text object.
     */
    public Text getActionTarget() {
        return actiontarget;
    }
}
