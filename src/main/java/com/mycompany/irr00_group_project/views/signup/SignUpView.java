package com.mycompany.irr00_group_project.views.signup;

import com.mycompany.irr00_group_project.controllers.SignUpController;
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
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * The SignUpView class is responsible for displaying the sign-up interface of the
 * application.
 * It allows users to enter their details to create a new account.
 */
public class SignUpView {
    private final Stage stage;
    private final SignUpForm signUpForm = new SignUpForm();
    private final SignUpController signUpController;

    /**
     * Constructor for the SignUpView class.
     * @param stage The primary stage for this application, onto which the
     *              application scene can be set.
     */
    public SignUpView(Stage stage) {
        this.stage = stage;
        this.signUpController = new SignUpController(stage, signUpForm);
    }

    /**
     * Displays the sign-up view.
     */
    public void show() {
        GridPane grid = LogSignGuiHelper.createFormGrid();
        ThemeUtils.setPrimaryBackground(grid);

        Label scenetitle = GuiHelper.createTitleLabel("Fentify - Sign Up");
        grid.add(scenetitle, 0, 0, 2, 1);

        signUpForm.addToGrid(grid);

        Button btn = GuiHelper.createStyledButton("Sign up", () -> signUpController.handleSignUp());

        HBox hbBtn = GuiHelper.createAlignedHBox(10, 0, Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 1, 6);

        Hyperlink signInHyperlink = new Hyperlink("Already registered? Sign in");
        signInHyperlink.setOnAction(e -> signUpController.switchToLoginView());

        Label passwordInfo = new Label(
            "Password must be at least 8 characters long,\n" 
            + "contain an uppercase letter, lowercase letter,\n" 
            + "digit, and special character."
        );
        passwordInfo.setWrapText(true);
        passwordInfo.setStyle("-fx-text-fill: #888; -fx-font-size: 12px;");

        VBox bottomLeftBox = GuiHelper.createPaddedVBox(5, 0);
        bottomLeftBox.setAlignment(Pos.BOTTOM_LEFT);
        bottomLeftBox.getChildren().addAll(signInHyperlink, passwordInfo);
        grid.add(bottomLeftBox, 0, 6);

        StackPane root = LogSignGuiHelper.wrapInStackPane(grid);

        stage.setTitle("Fentify - Sign Up");
        stage.setScene(new Scene(root, 1300, 700));
        stage.centerOnScreen();
        stage.show();
    }
}
