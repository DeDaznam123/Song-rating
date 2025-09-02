package com.mycompany.irr00_group_project.views.homepage;

import com.mycompany.irr00_group_project.controllers.TopBarController;
import com.mycompany.irr00_group_project.models.User;
import com.mycompany.irr00_group_project.views.homepage.friendslist.FollowedListView;
import com.mycompany.irr00_group_project.views.profile.UserView;
import com.mycompany.irr00_group_project.views.topbar.TopBarView;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

/**
 * Factory interface for creating various views in the application.
 * This allows for easy instantiation of views with dependencies injected.
 */
public interface ViewFactory {
    CentralView createCentralView();

    FollowedListView createFollowedListView();

    TopBarView createTopBarView(TopBarController controller);

    UserView createUserView(User user, Runnable onBack);

    BorderPane createMainBorderPane();
    
    RecommendationsView createRecommendationsView();

    StackPane createMainStackPane(BorderPane root);
}