package com.mycompany.irr00_group_project.controllers;

import com.mycompany.irr00_group_project.views.signup.SignUpForm;
import com.mycompany.irr00_group_project.controllers.managers.SessionManager;
import com.mycompany.irr00_group_project.models.User;
import com.mycompany.irr00_group_project.services.AuthenticationService;
import com.mycompany.irr00_group_project.services.Database;
import com.mycompany.irr00_group_project.views.homepage.DefaultViewFactory;
import com.mycompany.irr00_group_project.views.homepage.HomePageView;
import com.mycompany.irr00_group_project.views.login.LoginView;
import com.mycompany.irr00_group_project.views.homepage.SlidingNotification;


import javafx.stage.Stage;

/**
 * The SignUpController class is responsible for handling the sign-up process.
 */
public class SignUpController {
    private final Stage stage;
    private final SignUpForm signUpForm;

    /**
     * Constructor for the SignUpController class.
     *
     * @param stage     The primary stage for this application.
     * @param signUpForm The sign-up form to be used for user input.
     */
    public SignUpController(Stage stage, SignUpForm signUpForm) {
        this.stage = stage;
        this.signUpForm = signUpForm;
    }

    /**
     * Handles the sign-up process.
     */
    public void handleSignUp() {
        String username = signUpForm.getUsername();
        String email = signUpForm.getEmail();
        String password = signUpForm.getPassword();
        String confirmPassword = signUpForm.getConfirmPassword();

        if (!password.equals(confirmPassword)) {
            SlidingNotification.showError(
                stage.getScene(), "Sign-up failed: Passwords do not match");
            return;
        }

        User user = new User(
            Database.getInstance().getUsersTable().getNextId() + "",
            username, 
            email
        );
 
        try {
            AuthenticationService.signUp(user, password);
        } catch (Exception e) {
            SlidingNotification.showError(
                stage.getScene(), "Sign-up failed: " + e.getMessage());
            return;
        }
    
        SessionManager.getInstance().setCurrentUser(user);
        new HomePageView(stage, new DefaultViewFactory()).show();
        SlidingNotification.showSuccess(stage.getScene(), "Sign up successful!");
    }

    /**
     * Switches to the login view if the user is already registered.
     */
    public void switchToLoginView() {
        LoginView loginView = new LoginView(stage);
        loginView.show();
    }
}
