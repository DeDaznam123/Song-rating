package com.mycompany.irr00_group_project.controllers;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

import com.mycompany.irr00_group_project.controllers.managers.SessionManager;
import com.mycompany.irr00_group_project.models.User;

import javafx.scene.control.TextField;

/**
 * Controller for the top bar of the application.
 * It handles search functionality and user profile interactions.
 */
public class UserSearchController {

    private Runnable showDefault;

    // Callback for updating search results (used Consumer to accept an argument).
    private Consumer<List<User>> updateResults;

    /**
     * Constructor for the TopBarController class.
     * @param showDefault Callback to show default content.
     * @param updateResults Callback to update search results.
     */
    public UserSearchController(Runnable showDefault, Consumer<List<User>> updateResults) {
        this.showDefault = showDefault;
        this.updateResults = updateResults;
    }

    /**
     * Handles the search functionality.
     * If the query is blank, it shows the default content.
     * Otherwise, it updates the search results and shows them.
     * @param query The search query entered by the user.
     * @throws IOException Thrown if there is an error accessing the database.
     * @throws IllegalArgumentException Thrown if the query is null or empty.
     */
    public void handleSearch(String query) throws IllegalArgumentException, IOException {
        if (query.isBlank()) {
            showDefault.run();
            return;
        }
        SearchController.getInstance().updateSearchResultsUsers(query);
        List<User> users = SearchController.getInstance().getSearchResultsUsers();
        for (User user : users) {
            if (user.getId().equals(SessionManager.getInstance().getCurrentUser().getId())) {
                users.remove(user);
                for (User u : users) {
                    System.out.println("User: " + u.getUsername());
                }
                break;
            }
        }
        updateResults.accept(users);
    }

    /**
     * Handles the clear search button click.
     * It shows the default content.
     */
    public void handleClearSearch(TextField searchField) {
        searchField.clear();
        showDefault.run();
    }
}
