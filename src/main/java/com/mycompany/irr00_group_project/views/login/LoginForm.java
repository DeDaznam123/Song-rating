package com.mycompany.irr00_group_project.views.login;

import com.mycompany.irr00_group_project.utils.ThemeUtils;

import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

/**
 * The LoginForm class represents the login form of the application.
 */
public class LoginForm {
    private final TextField usernameField = new TextField();
    private final PasswordField passwordField = new PasswordField();
    private final Text actiontarget = new Text();

    /**
     * Constructor for the LoginForm class.
     * Initializes the username and password fields.
     *
     * @param grid The grid pane to which the form will be added.
     */
    public void addToGrid(GridPane grid) {
        Label userName = new Label("User Name:");
        ThemeUtils.setTextFont(userName);
        grid.add(userName, 0, 1);
        grid.add(usernameField, 1, 1);

        Label pw = new Label("Password:");
        ThemeUtils.setTextFont(pw);
        grid.add(pw, 0, 2);
        grid.add(passwordField, 1, 2);

        // Text on which the action is displayed
        grid.add(actiontarget, 1, 6);
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
     * Gets the password entered in the form.
     *
     * @return The password as a String.
     */
    public String getPassword() {
        return passwordField.getText();
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
