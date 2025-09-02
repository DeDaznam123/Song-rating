package com.mycompany.irr00_group_project.views.homepage;

import com.mycompany.irr00_group_project.controllers.managers.CentralContentManager;
import com.mycompany.irr00_group_project.models.reviewables.ReviewableType;
import com.mycompany.irr00_group_project.models.reviewables.targets.Album;
import com.mycompany.irr00_group_project.models.reviewables.targets.Artist;
import com.mycompany.irr00_group_project.models.reviewables.targets.Song;
import com.mycompany.irr00_group_project.views.search.SearchResults;

import javafx.scene.Node;

/**
 * Represents the central view of the application.
 */
public class CentralView extends Node {

    private RecommendationsView recommendationsBox;
    private SearchResults resultsView;

    /**
     * Constructor for the CentralView class.
     */
    public CentralView() {       
        this.recommendationsBox = new RecommendationsView();
        recommendationsBox.setOnRecommendationSelected(reviewable -> {
            if (ReviewableType.SONG.equals(reviewable.getType())) {
                CentralContentManager.getInstance().showSongDetailView((Song) reviewable);
            } else if (ReviewableType.ALBUM.equals(reviewable.getType())) {
                CentralContentManager.getInstance().showAlbumDetailView((Album) reviewable);
            } else if (ReviewableType.ARTIST.equals(reviewable.getType())) {
                CentralContentManager.getInstance().showArtistDetailView((Artist) reviewable);
            }
        });
        this.resultsView = new SearchResults();

    }

    /**
     * Returns the recommendations box.
     *
     * @return The recommendations box.
     */
    public RecommendationsView getRecommendationsBox() {
        return recommendationsBox;
    }

    /**
     * Returns the search results view.
     *
     * @return The search results view.
     */
    public SearchResults getSearchResults() {
        return resultsView;
    }

}
