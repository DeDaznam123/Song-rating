package com.mycompany.irr00_group_project.views.search;

import java.util.List;
import java.util.function.Consumer;

import com.mycompany.irr00_group_project.models.reviewables.Reviewable;
import com.mycompany.irr00_group_project.utils.ThemeUtils;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

/**
 * ResultsView displays a list of reviewable results in a scrollable view.
 * It updates the displayed reviewables based on the search query.
 */
public class SearchResults extends VBox {

    private SearchResultList resultListVBox;

    /**
     * Constructor for the ResultsView class.
     * Initializes the layout and components.
     */
    public SearchResults() {
        Label searchResultsLabel = new Label("Search Results");
        ThemeUtils.setTitleFont(searchResultsLabel);

        resultListVBox = new SearchResultList();

        ScrollPane scrollPane = new ScrollPane(resultListVBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        this.setSpacing(10);
        this.setPadding(new Insets(20));
        this.getChildren().addAll(searchResultsLabel, scrollPane);
        this.setVisible(false);
        this.setMaxWidth(Double.MAX_VALUE);
        ThemeUtils.setPrimaryBackgroundRoundedInsets(this);
    }

    /**
     * Retrieves the reviewable list view.
     * @return The reviewable list view.
     */
    public Consumer<List<Reviewable>> updateResults() {
        return resultListVBox::updateResults;
    }

    /**
     * Sets the listener for reviewable selection events.
     *
     * @param listener The listener to be notified when a reviewable is selected.
     */
    public void setOnReviewableSelected(Consumer<Reviewable> listener) {
        resultListVBox.setOnReviewableSelected(listener);
    }
}