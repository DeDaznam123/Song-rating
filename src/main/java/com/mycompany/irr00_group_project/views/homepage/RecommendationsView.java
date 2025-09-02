package com.mycompany.irr00_group_project.views.homepage;

import java.util.List;
import java.util.function.Consumer;

import com.mycompany.irr00_group_project.controllers.managers.SessionManager;
import com.mycompany.irr00_group_project.models.reviewables.Reviewable;
import com.mycompany.irr00_group_project.utils.Colors;
import com.mycompany.irr00_group_project.utils.GuiHelper;
import com.mycompany.irr00_group_project.utils.ThemeUtils;

import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

/**
 * RecommendationsView shows recommendations in several sections with cards.
 */
public class RecommendationsView extends VBox {

    private Consumer<Reviewable> onRecommendationSelected;
    private Thread recommendationsThread; // Store the thread

    /**
     * Creates the recommendations layout.
     */
    public RecommendationsView() {
        Label header = GuiHelper.createTitleLabel("Recommendations");
        this.onRecommendationSelected = r -> {};
        Label loadingLabel = new Label("Loading recommendations...");
        ThemeUtils.setTitleFont(loadingLabel);
        loadingLabel.setTextFill(Colors.TEXT);
        this.getChildren().addAll(header, loadingLabel);
        this.setPadding(GuiHelper.createPaddedVBox(0, 20).getPadding());
        ThemeUtils.setPrimaryBackgroundRoundedInsets(this);

        // Load recommendations in a background thread
        Task<List<Reviewable>> loadTask = new Task<>() {
            @Override
            protected List<Reviewable> call() {
                try {
                    // This will block until threads are finished in CommentRecommendationService.
                    return SessionManager.getInstance().getRecommendations();
                } catch (Exception e) {
                    e.printStackTrace();
                    return List.of();
                }
            }
        };

        loadTask.setOnSucceeded(event -> {
            List<Reviewable> recommendations = loadTask.getValue();
            this.getChildren().remove(loadingLabel);

            // === Top Recommendations Grid ===
            TilePane topGrid = createTilePane();
            for (Reviewable rec : recommendations) {
                VBox card = createRecommendationCard(rec);
                topGrid.getChildren().add(card);
            }

            ScrollPane scrollPane = new ScrollPane(topGrid);
            scrollPane.setFitToWidth(true);
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

            this.getChildren().add(scrollPane);
        });

        // Store and start the thread
        recommendationsThread = new Thread(loadTask);
        recommendationsThread.start();
    }

    /**
     * Returns the thread used for loading recommendations.
     */
    public Thread getRecommendationsThread() {
        return recommendationsThread;
    }

    /**
     * Creates a TilePane for displaying cards.
     *
     * @return The TilePane.
     */
    private TilePane createTilePane() {
        TilePane tilePane = new TilePane();
        tilePane.setHgap(20);
        tilePane.setVgap(20);
        tilePane.setTileAlignment(javafx.geometry.Pos.TOP_LEFT);
        tilePane.setPrefTileWidth(140);
        tilePane.setPrefTileHeight(140); 
        tilePane.setPadding(GuiHelper.createPaddedVBox(0, 10).getPadding());
        return tilePane;
    }

    /**
     * Creates a recommendation card for a Reviewable item.
     *
     * @param reviewable The Reviewable item to create a card for.
     * @return The VBox containing the recommendation card.
     */
    private VBox createRecommendationCard(Reviewable reviewable) {
        VBox card = GuiHelper.createPaddedVBox(5, 10);
        card.setPrefSize(200, 100);
        card.setAlignment(javafx.geometry.Pos.CENTER);
        ThemeUtils.setSecondaryBackgroundRounded(card);

        ImageView imageView = new ImageView(reviewable.getImage());
        imageView.setFitWidth(100);
        imageView.setFitHeight(100);
        imageView.setSmooth(true);
        imageView.setPreserveRatio(true);
        ThemeUtils.applyRoundedClip(imageView, 20);

        Label label = new Label(reviewable.getName());
        ThemeUtils.setTextFont(label);

        card.setOnMouseClicked(event -> handleRecommendationSelected(reviewable));
        card.getChildren().addAll(imageView, label);

        return card;
    }

    /**
     * Sets the listener for recommendation selection events.
     *
     * @param listener The listener to be notified when a recommendation is selected.
     */
    public void setOnRecommendationSelected(Consumer<Reviewable> listener) {
        this.onRecommendationSelected = listener;
    }

    /**
     * Handles the selection of a recommendation.
     *
     * @param reviewable The selected Reviewable item.
     */
    private void handleRecommendationSelected(Reviewable reviewable) {
        if (onRecommendationSelected != null) {
            onRecommendationSelected.accept(reviewable);
        }
    }
}