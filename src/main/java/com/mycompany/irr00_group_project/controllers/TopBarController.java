package com.mycompany.irr00_group_project.controllers;

import java.util.List;
import java.util.function.Consumer;

import com.mycompany.irr00_group_project.controllers.managers.SessionManager;
import com.mycompany.irr00_group_project.models.User;
import com.mycompany.irr00_group_project.models.reviewables.Reviewable;

import javafx.scene.control.TextField;

/**
 * Controller for the top bar of the application.
 * It handles search functionality and user profile interactions.
 */
public class TopBarController {
    // Callbacks for showing search results and default content.
    private Runnable showSearchResults;
    private Runnable clearButton;

    // Callback for updating search results (used Consumer to accept an argument).
    private Consumer<List<Reviewable>> updateResults;
    private Consumer<User> showUserProfile;

    /**
     * Constructor for the TopBarController class.
     *
     * @param showSearchResults Callback to show search results.
     * @param updateResults Callback to update search results.
     * @param showUserProfile Callback to show user profile.
     */
    public TopBarController(Runnable showSearchResults, 
        Runnable clearButton,
        Consumer<List<Reviewable>> updateResults, Consumer<User> showUserProfile) {

        this.showUserProfile = showUserProfile;
        this.showSearchResults = showSearchResults;
        this.updateResults = updateResults;
        this.clearButton = clearButton;
    }

    /**
     * Handles the search functionality.
     * If the query is blank, it shows the default content.
     * Otherwise, it updates the search results and shows them.
     *
     * @param query The search query entered by the user.
     */
    public void handleSearch(String query) {
        if (query.isBlank()) {
            return;
        }

        SearchController.getInstance().updateSearchResultsReviewables(query);
        List<Reviewable> reviewables = SearchController.getInstance().getSearchResultsReviewables();
        updateResults.accept(reviewables);
        showSearchResults.run();
    }

    /**
     * Handles the profile button click.
     * It opens the ProfileView.
     */
    public void handleProfileClick() {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (showUserProfile != null && currentUser != null) {
            showUserProfile.accept(currentUser);
        }
    }

    /**
     * Handles the clear search button click.
     * It shows the default content.
     */
    public void handleClearSearch(TextField searchField) {
        searchField.clear();
    }

    /**
     * Clears the search field and invokes the clear button action.
     *
     * @param searchField The TextField to clear.
     */
    public void clearButton(TextField searchField) {
        searchField.clear();
        clearButton.run();
    }
}
