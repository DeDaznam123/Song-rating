package com.mycompany.irr00_group_project.controllers;

import com.mycompany.irr00_group_project.controllers.managers.SessionManager;
import com.mycompany.irr00_group_project.models.User;
import com.mycompany.irr00_group_project.services.AuthenticationService;
import com.mycompany.irr00_group_project.views.homepage.DefaultViewFactory;
import com.mycompany.irr00_group_project.views.homepage.HomePageView;
import com.mycompany.irr00_group_project.views.login.LoginForm;
import com.mycompany.irr00_group_project.views.signup.SignUpView;
import com.mycompany.irr00_group_project.views.homepage.SlidingNotification;

import javafx.stage.Stage;

/**
 * The LoginController class is responsible for handling the login process.
 */
public class LoginController {
    private final Stage stage;
    private final LoginForm loginForm;

    /**
     * Constructor for the LoginController class.
     *
     * @param stage     The primary stage for this application.
     * @param loginForm The login form to be used for user input.
     */
    public LoginController(Stage stage, LoginForm loginForm) {
        this.loginForm = loginForm;
        this.stage = stage;
    }

    /**
     * Handles the login process.
     */
    public void handleLogin() {
        
        User user = AuthenticationService.login(
            loginForm.getUsername(),
            loginForm.getPassword()
        );

        if (user == null) {
            SlidingNotification.showError(
                stage.getScene(), "Invalid username or password");
        } else {
            SessionManager.getInstance().setCurrentUser(user);
            new HomePageView(stage, new DefaultViewFactory()).show();
            SlidingNotification.showSuccess(
                stage.getScene(), "Login successful");
        }
    }

    /**
     * Switches to the sign-up view if the user is not registered.
     */
    public void switchToSignUpView() {
        new SignUpView(stage).show();
    }
}
