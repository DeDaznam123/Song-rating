package com.mycompany.irr00_group_project.views.login;

import com.mycompany.irr00_group_project.controllers.LoginController;
import com.mycompany.irr00_group_project.utils.GuiHelper;
import com.mycompany.irr00_group_project.utils.LogSignGuiHelper;
import com.mycompany.irr00_group_project.utils.ThemeUtils;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * The LoginView class is responsible for displaying the login interface of the
 * application.
 * It allows users to enter their username and password to log in.
 */
public class LoginView {
    private final Stage stage;
    private final LoginController loginController;
    private final LoginForm loginForm = new LoginForm();

    /**
     * Constructor for the LoginView class.
     *
     * @param stage The primary stage for this application, onto which the
     *              application scene can be set.
     */
    public LoginView(Stage stage) {
        this.stage = stage;
        this.loginController = new LoginController(stage, loginForm);
    }

    /**
     * Displays the login view.
     */
    public void show() {
        GridPane grid = LogSignGuiHelper.createFormGrid();
        ThemeUtils.setPrimaryBackground(grid);

        Label scenetitle = GuiHelper.createTitleLabel("Fentify - Login");
        grid.add(scenetitle, 0, 0, 2, 1);

        loginForm.addToGrid(grid);

        Button btn = GuiHelper.createStyledButton("Sign in", () -> loginController.handleLogin());

        HBox hbBtn = GuiHelper.createAlignedHBox(10, 0, Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 1, 4);

        Hyperlink signInHyperlink = new Hyperlink("Not registered? Sign up");
        signInHyperlink.setOnAction(e -> loginController.switchToSignUpView());

        HBox signInLinkBox = GuiHelper.createAlignedHBox(10, 0, Pos.BOTTOM_LEFT);
        signInLinkBox.getChildren().add(signInHyperlink);
        grid.add(signInLinkBox, 0, 6);

        StackPane root = LogSignGuiHelper.wrapInStackPane(grid);
        stage.setTitle("Fentify - Login");
        stage.setScene(new Scene(root, 1300, 700));
        stage.centerOnScreen();
        stage.show();
    }
}
