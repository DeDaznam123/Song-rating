package com.mycompany.irr00_group_project.views.reviewpage.comment_section;

import java.util.function.Consumer;

import com.mycompany.irr00_group_project.controllers.managers.SessionManager;
import com.mycompany.irr00_group_project.services.Database;
import com.mycompany.irr00_group_project.models.reviewables.Review;
import com.mycompany.irr00_group_project.utils.ThemeUtils;
import com.mycompany.irr00_group_project.views.reviewpage.StarRatingView;
import com.mycompany.irr00_group_project.utils.TriConsumer;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * The CommentView class represents a view for displaying a comment in the comment section.
 * It includes the comment text, the username of the commenter, and a like button.
 * The like button updates the like count when pressed.
 */
public class CommentView extends VBox {
    private final Label likeLabel;
    private final Review comment;
    private final TriConsumer<Review, String, Integer> editReview; 

    /**
     * Constructs a CommentView with the specified comment and like button action.
     *
     * @param comment The comment to display.
     * @param onLikePressed A consumer that defines the action to take 
     *                      when the like button is pressed.
     */
    public CommentView(Review comment, Consumer<Review> onLikePressed, 
            TriConsumer<Review, String, Integer> editReview) {
                
        this.editReview = editReview;
        this.comment = comment;
        setSpacing(10);
        setPadding(new Insets(10));
        ThemeUtils.setSecondaryBackgroundRounded(this);
        setPrefWidth(600);

        Label usernameLabel = new Label("@" + comment.getUser().getUsername());
        usernameLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
    
        StarRatingView reviewBox = new StarRatingView(false);
        reviewBox.setRating(comment.getRating()); 
        HBox userAndRating = new HBox(10, usernameLabel, reviewBox);
        userAndRating.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        var currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser != null && currentUser.getId().equals(comment.getUser().getId())) {
            Button editButton = new Button("Edit");
            editButton.setOnAction(e -> {
                setEditView(usernameLabel);
            });
            userAndRating.getChildren().add(editButton);
        }

        Label commentText = new Label(comment.getText());
        commentText.setStyle("-fx-text-fill: white;");
        commentText.setWrapText(true);

        likeLabel = new Label("👍 " + comment.getLikes());
        
        // Check if current user has already liked this comment
        boolean userHasLiked = checkIfUserLikedComment(comment);
        updateLikeButtonStyle(userHasLiked);
        
        likeLabel.setOnMouseClicked(e -> {
            onLikePressed.accept(comment);
            updateLikes(comment.getLikes());
        });

        getChildren().addAll(userAndRating, commentText, likeLabel);
    }
    
    /**
     * Checks if the current user has already liked the given comment.
     * 
     * @param comment The comment to check.
     * @return true if the user has liked the comment, false otherwise.
     */
    private boolean checkIfUserLikedComment(Review comment) {
        try {
            var currentUser = SessionManager.getInstance().getCurrentUser();
            if (currentUser == null || comment.getId() == null) {
                return false;
            }
            return Database.getInstance().getLikesTable().getLineId(
                    new String[] {currentUser.getId(), comment.getId()}) != null;
        } catch (Exception e) {
            System.err.println("Error checking if user liked comment: " + e.getMessage());
            return false;
        }
    }

    /**
     * Sets the view to edit mode, allowing the user to modify the comment text and rating.
     * @param usernameLabel The label displaying the username of the commenter.
     */
    private void setEditView(Label usernameLabel) {
        TextArea editField = new TextArea(comment.getText());
        editField.setWrapText(true);
        editField.setPrefRowCount(2);

        StarRatingView editableRating = new StarRatingView(true);
        editableRating.setRating(comment.getRating());

        Button saveButton = new Button("Save");
        Button cancelButton = new Button("Cancel");
        HBox editButtons = new HBox(10, saveButton, cancelButton);

        // Remove old nodes and add editing nodes
        getChildren().clear();
        HBox editUserAndRating = new HBox(10, usernameLabel, editableRating);
        editUserAndRating.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        getChildren().addAll(editUserAndRating, editField, editButtons, likeLabel);

        saveButton.setOnAction(ev -> {
            String newText = editField.getText().trim();
            Integer newRating = editableRating.getRating();
            editReview.accept(comment, newText, newRating);
        });
        cancelButton.setOnAction(ev -> {
            getChildren().clear();
            Label commentText = new Label(comment.getText());
            commentText.setStyle("-fx-text-fill: white;");
            commentText.setWrapText(true);
            StarRatingView restoredRating = new StarRatingView(false);
            HBox userAndRating = new HBox(10, usernameLabel, restoredRating);
            userAndRating.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            restoredRating.setRating(comment.getRating());
            Button editButton = new Button("Edit");
            editButton.setOnAction(e -> setEditView(usernameLabel));
            userAndRating.getChildren().add(editButton);
            getChildren().addAll(userAndRating, commentText, likeLabel);
        });
           
    }
    
    /**
     * Updates the style of the like button based on whether the user has liked it.
     * 
     * @param userHasLiked true if the user has liked the comment, false otherwise.
     */
    private void updateLikeButtonStyle(boolean userHasLiked) {
        if (userHasLiked) {
            likeLabel.setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold; -fx-cursor: hand;");
        } else {
            likeLabel.setStyle("-fx-text-fill: gray; -fx-cursor: hand;");
        }
    }

    /**
     * Updates the like count display and visual feedback.
     * 
     * @param newCount The new like count to display.
     */
    public void updateLikes(int newCount) {
        likeLabel.setText("👍 " + newCount);
        // Re-check the like status to update the visual feedback
        boolean userHasLiked = checkIfUserLikedComment(this.comment);
        updateLikeButtonStyle(userHasLiked);
    }
}
