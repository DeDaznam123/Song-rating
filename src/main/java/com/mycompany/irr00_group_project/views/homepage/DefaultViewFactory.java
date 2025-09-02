package com.mycompany.irr00_group_project.views.homepage;

import com.mycompany.irr00_group_project.controllers.TopBarController;
import com.mycompany.irr00_group_project.controllers.managers.CentralContentManager;
import com.mycompany.irr00_group_project.models.User;
import com.mycompany.irr00_group_project.models.reviewables.ReviewableType;
import com.mycompany.irr00_group_project.models.reviewables.targets.Album;
import com.mycompany.irr00_group_project.models.reviewables.targets.Artist;
import com.mycompany.irr00_group_project.models.reviewables.targets.Song;
import com.mycompany.irr00_group_project.views.homepage.friendslist.FollowedListView;
import com.mycompany.irr00_group_project.views.profile.UserView;
import com.mycompany.irr00_group_project.views.topbar.TopBarView;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

/**
 * Default implementation of the ViewFactory interface.
 * This class provides concrete implementations for creating various views
 * used in the application, such as CentralView, FollowedListView, TopBarView,
 * UserView, and RecommendationsView.
 */
public class DefaultViewFactory implements ViewFactory {
    /**
     * Creates a CentralView instance.
     * This view serves as the main content area of the application.
     *
     * @return A new instance of CentralView.
     */
    public CentralView createCentralView() { 
        return new CentralView(); 
    }

    /**
     * Creates a FollowedListView instance.
     * This view displays a list of users that the current user is following.
     *
     * @return A new instance of FollowedListView.
     */
    public FollowedListView createFollowedListView() { 
        return new FollowedListView(); 
    }

    /**
     * Creates a TopBarView instance.
     * This view contains the top navigation bar with search functionality and user profile access.
     *
     * @param controller The controller that manages the top bar interactions.
     * @return A new instance of TopBarView.
     */
    public TopBarView createTopBarView(TopBarController controller) { 
        return new TopBarView(controller); 
    }

    /**
     * Creates a UserView instance.
     * This view displays the profile of a specific user
     * and allows navigation back to the previous view.
     *
     * @param user The user whose profile is to be displayed.
     * @param onBack A runnable action to execute when navigating back.
     * @return A new instance of UserView for the specified user.
     */
    public UserView createUserView(User user, Runnable onBack) { 
        return new UserView(user, onBack); 
    }

    /**
     * Creates a RecommendationsView instance.
     * This view displays recommendations and allows users to select a reviewable item.
     * When a recommendation is selected, it navigates to the appropriate detail view
     */
    public RecommendationsView createRecommendationsView() {
        RecommendationsView recommendationsView = new RecommendationsView();
        recommendationsView.setOnRecommendationSelected(reviewable -> {
            if (ReviewableType.SONG.equals(reviewable.getType())) {
                CentralContentManager.getInstance().showSongDetailView((Song) reviewable);
            } else if (ReviewableType.ALBUM.equals(reviewable.getType())) {
                CentralContentManager.getInstance().showAlbumDetailView((Album) reviewable);
            } else if (ReviewableType.ARTIST.equals(reviewable.getType())) {
                CentralContentManager.getInstance().showArtistDetailView((Artist) reviewable);
            }
        });
        return recommendationsView;
    }

    /**
     * Creates the main BorderPane for the application.
     * This pane serves as the root layout for the application's UI.
     *
     * @return A new instance of BorderPane.
     */
    public BorderPane createMainBorderPane() { 
        return new BorderPane(); 
    }

    /**
     * Creates the main StackPane that contains the root BorderPane.
     * This StackPane serves as the primary container for the application's views.
     *
     * @param root The BorderPane to be wrapped in the StackPane.
     * @return A new instance of StackPane containing the root BorderPane.
     */
    public StackPane createMainStackPane(BorderPane root) { 
        return new StackPane(root); 
    }
}