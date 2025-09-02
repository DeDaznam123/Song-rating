package com.mycompany.irr00_group_project.views.topbar;

import com.mycompany.irr00_group_project.controllers.TopBarController;
import com.mycompany.irr00_group_project.utils.ThemeUtils;

import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.util.Duration;

/**
 * The TopBarView class represents the top bar of the application.
 * It includes a search field, a clear button, and a profile button.
 */
public class TopBarView extends HBox {
    private final TextField searchField;
    private final PauseTransition debounceTimer;

    /**
     * Constructor for the TopBarView class.
     *
     * @param controller The controller associated with this view.
     */
    public TopBarView(TopBarController controller) {
        
        ThemeUtils.setPrimaryBackground(this);
        this.setSpacing(10);
        this.setAlignment(Pos.CENTER_LEFT);
        this.setPadding(new Insets(10));

        searchField = new TextField();
        searchField.setPromptText("Search...");
        searchField.setPrefWidth(400);
        searchField.setStyle("-fx-text-fill: #ffffff; -fx-prompt-text-fill:rgb(146, 146, 146);");
        ThemeUtils.setTertiaryBackgroundRounded(searchField);

        // Debounce timer to avoid running search too often.
        debounceTimer = new PauseTransition(Duration.millis(300));
        debounceTimer.setOnFinished(event -> controller.handleSearch(searchField.getText().trim()));

        searchField.setOnKeyReleased(e -> debounceTimer.playFromStart());
        searchField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal && searchField.getText().trim().isEmpty()) {
                controller.handleClearSearch(searchField);
            }
        });

        Button clearBtn = new Button("X");
        ThemeUtils.setTertiaryBackgroundRounded(clearBtn);
        ThemeUtils.makeSmallCircleButton(clearBtn);
        clearBtn.setOnAction(e -> {
            controller.clearButton(searchField);
            
        });

        Button profileBtn = new Button("P");
        ThemeUtils.makeCircleButton(profileBtn);
        profileBtn.setOnAction(e -> controller.handleProfileClick());

        getChildren().addAll(searchField, clearBtn, profileBtn);
        HBox.setHgrow(searchField, Priority.ALWAYS);
    }
}
