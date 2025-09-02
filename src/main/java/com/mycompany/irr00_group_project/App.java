package com.mycompany.irr00_group_project;

import java.io.IOException;

import com.mycompany.irr00_group_project.services.CommentRecommenderService;
import com.mycompany.irr00_group_project.utils.ImageUtils;
import com.mycompany.irr00_group_project.views.signup.SignUpView;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Main App.
 */
public class App extends Application {

    public static void main(String[] args) {
        try {
            CommentRecommenderService.setAllReviewables();
        } catch (IOException e) {
            e.printStackTrace();
        }
        launch(args);
    }

    /**
     * This is the main method that starts the JavaFX application.
     *
     * @param primaryStage The primary stage for this application, onto which
     *                     the application scene can be set.
     */
    @Override
    public void start(Stage primaryStage) {
        // Set window Icon.
        Image icon = ImageUtils.loadAppLogo();
        primaryStage.getIcons().add(icon);
        
        // Show the sign up view.
        new SignUpView(primaryStage).show();
    }
}
