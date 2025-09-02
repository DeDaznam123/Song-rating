package com.mycompany.irr00_group_project.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import com.mycompany.irr00_group_project.models.User;
import com.mycompany.irr00_group_project.models.reviewables.Reviewable;
import com.mycompany.irr00_group_project.services.Database;

/**
 * SearchController handles the search functionality for reviewable items in the
 * application.
 * It interacts with the database to fetch search results based on user queries.
 */
public class SearchController {

    private List<Reviewable> searchResultReviewables;
    private List<User> searchResultUsers;
    private static SearchController instance;

    /**
     * Constructor for the SearchController class.
     * Initializes the access token and search result list.
     */
    public SearchController() {
        this.searchResultReviewables = new ArrayList<>();
        this.searchResultUsers = new ArrayList<>();
    }

    /**
     * Updates the search results based on the provided query.
     *
     * @param query The search query.
     */
    public void updateSearchResultsReviewables(String query) {
        if (query == null || query.trim().isEmpty()) {
            this.searchResultReviewables.clear();
            return;
        }
        this.searchResultReviewables = Database.getInstance()
            .searchReviewables(query.toLowerCase().trim());
    }

    /**
     * Returns the current search results for reviewable items.
     * 
     * @param query The search query.
     * @throws IOException              thrown if there is an error accessing the
     *                                  database.
     * @throws IllegalArgumentException thrown if the query is null or empty.
     */
    public void updateSearchResultsUsers(String query)
            throws IllegalArgumentException, IOException {
        if (query == null || query.trim().isEmpty()) {
            this.searchResultUsers.clear();
            return;
        }
        this.searchResultUsers = Database.getInstance().searchUsers(query.toLowerCase().trim());
    }

    /**
     * Returns the current search results.
     *
     * @return A list of reviewable items matching the search query.
     */
    public List<Reviewable> getSearchResultsReviewables() {
        return searchResultReviewables;
    }

    /**
     * Returns the current search results for users.
     *
     * @return A list of users matching the search query.
     */
    public List<User> getSearchResultsUsers() {
        return searchResultUsers;
    }

    /**
     * Returns the singleton instance of SearchController.
     *
     * @return The singleton instance of SearchController.
     */
    public static SearchController getInstance() {
        if (instance == null) {
            instance = new SearchController();
        }
        return instance;
    }

}
