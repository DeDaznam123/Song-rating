package com.mycompany.irr00_group_project.views.homepage;

import com.mycompany.irr00_group_project.controllers.TopBarController;
import com.mycompany.irr00_group_project.controllers.managers.CentralContentManager;
import com.mycompany.irr00_group_project.models.User;
import com.mycompany.irr00_group_project.models.reviewables.Reviewable;
import com.mycompany.irr00_group_project.models.reviewables.targets.Album;
import com.mycompany.irr00_group_project.models.reviewables.targets.Artist;
import com.mycompany.irr00_group_project.models.reviewables.targets.Song;
import com.mycompany.irr00_group_project.utils.ThemeUtils;
import com.mycompany.irr00_group_project.views.homepage.friendslist.FollowedListView;
import com.mycompany.irr00_group_project.views.topbar.TopBarView;

import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Represents the home page view of the application.
 * This view contains the main layout, and can switch between Recommendations,
 * Search Results, and Song Details views.
 * It also includes a top bar for navigation and a friends list on the right.
 */
public class HomePageView {
    private final Stage stage;
    private final ViewFactory viewFactory;
    private TopBarView topBar;
    private BorderPane root;
    private StackPane rootStack;
    private FollowedListView friendsBox;
    private CentralView centralView;
    private TopBarController topBarController;

    /**
     * Constructor for the HomePageView class.
     * @param stage The primary stage of the application.
     * @param viewFactory The factory to create views.
     */
    public HomePageView(Stage stage, ViewFactory viewFactory) {
        this.stage = stage;
        this.viewFactory = viewFactory;
        this.centralView = viewFactory.createCentralView();
        this.root = viewFactory.createMainBorderPane();

        CentralContentManager.initialize(
            root,
            centralView,
            this::displayReviewableDetailView
        );

        this.friendsBox = viewFactory.createFollowedListView();

        this.topBarController = new TopBarController(
            this::displaySearchResultsView,
            this::resetToFriendsAndUpdateResults,
            centralView.getSearchResults().updateResults(),
            this::showUserProfile
        );
        this.topBar = viewFactory.createTopBarView(topBarController);

        this.rootStack = viewFactory.createMainStackPane(root);
    }

    private void showUserProfile(User user) {
        root.setCenter(viewFactory.createUserView(user, 
            () -> root.setCenter(viewFactory.createRecommendationsView()))); 
    }

    /**
     * Shows the home page view.
     */
    public void show() {
        centralView.getSearchResults().setVisible(false);
        centralView.getSearchResults().setManaged(false);

        root.setTop(topBar);
        root.setRight(friendsBox);
        ThemeUtils.setSecondaryBackgroundRounded(root);

        stage.setTitle("Fentify - Home Page");
        stage.setScene(new Scene(rootStack, 1300, 700));
        stage.centerOnScreen();
        stage.show();
    }

    /**
     * Displays the search results view. 
     */
    private void displaySearchResultsView() {
        CentralContentManager.getInstance().showSearchResultsView();
    }

    /**
     * Displays the detail view for a reviewable item.
     * @param reviewable The reviewable item to display.
     */
    private void displayReviewableDetailView(Reviewable reviewable) {
        switch (reviewable.getType()) {
            case SONG:
                CentralContentManager.getInstance().showSongDetailView((Song) reviewable);
                break;
            case ALBUM:
                CentralContentManager.getInstance().showAlbumDetailView((Album) reviewable);
                break;
            case ARTIST:
                CentralContentManager.getInstance().showArtistDetailView((Artist) reviewable);
                break;
            default:
                System.err.println("Unsupported reviewable type: " + reviewable.getType());
                break;
        }
    }

    /**
     * Resets the right side of the root layout to show the friends list and updates the results.
     */
    private void resetToFriendsAndUpdateResults() {
        root.setRight(friendsBox);
        CentralContentManager.getInstance().showRecommendationsView();
    }
}
