package com.mycompany.irr00_group_project.views.cards;

import com.mycompany.irr00_group_project.models.reviewables.Reviewable;
import com.mycompany.irr00_group_project.models.reviewables.ReviewableType;
import com.mycompany.irr00_group_project.models.reviewables.targets.Song;
import com.mycompany.irr00_group_project.models.reviewables.targets.Album;
import com.mycompany.irr00_group_project.utils.ThemeUtils;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

/**
 * Represents the top result card in the search results.
 */
public class TopResultCard extends HBox {

    /**
     * Constructor for the TopResultCard class.
     * @param reviewable The reviewable item to display.
     */
    public TopResultCard(Reviewable reviewable) {
        this.setSpacing(20);
        this.setPadding(new Insets(10));
        this.setAlignment(Pos.CENTER_LEFT);
        ThemeUtils.setSecondaryBackgroundRounded(this);

        ImageView imageView = new ImageView();
        imageView.setFitWidth(100);
        imageView.setFitHeight(100);
        imageView.setPreserveRatio(true);
        imageView.setImage(reviewable.getImage());

        VBox infoBox = new VBox(5);
        
        Label title = new Label(reviewable.getName());
        title.setFont(Font.font("System", 20));  

        Label subtitle = new Label(getSubtitle(reviewable));
        if (reviewable.getType() == ReviewableType.ARTIST) {
            subtitle.setFont(Font.font("System", 16));
        } else {
            subtitle.setFont(Font.font("System", 14));
        }
        subtitle.setStyle("-fx-text-fill: gray;");

        infoBox.getChildren().addAll(title, subtitle);

        getChildren().addAll(imageView, infoBox);
    }

    /**
     * Gets the appropriate subtitle for the reviewable based on its type.
     * @param reviewable The reviewable item
     * @return The subtitle string
     */
    private String getSubtitle(Reviewable reviewable) {
        if (reviewable.getType() == ReviewableType.SONG) {
            return "Song\n" + ((Song) reviewable).getArtist().getName();
        } else if (reviewable.getType() == ReviewableType.ALBUM) {
            return "Album\n" + ((Album) reviewable).getArtist().getName();
        } else if (reviewable.getType() == ReviewableType.ARTIST) {
            return "Artist";
        }
        return "";
    }
}
