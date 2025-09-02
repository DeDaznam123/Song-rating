package com.mycompany.irr00_group_project.views.reviewpage.comment_section;

import java.util.function.BiConsumer;

import com.mycompany.irr00_group_project.controllers.managers.SessionManager;
import com.mycompany.irr00_group_project.utils.ThemeUtils;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.image.ImageView;

/**
 * CommentInputView is a UI component for inputting comments in the comment section.
 * It includes a text area for entering comments, an avatar,
 * and buttons for submitting or canceling.
 */
public class CommentInputView extends VBox {
    private final TextArea commentField;
    private final Button commentButton;
    private final HBox buttonBar;
    private final UserRatingBox userRatingBox;

    /**
     * Constructs a CommentInputView with a callback for when a comment is submitted.
     *
     * @param onSubmit A consumer that accepts the comment text when submitted.
     */
    public CommentInputView(BiConsumer<String, Integer> onSubmit) {
        super(10);
        ThemeUtils.setPrimaryBackgroundRoundedInsets(this);

        HBox inputRow = new HBox(10);
        inputRow.setAlignment(Pos.TOP_LEFT);

        // Avatar
        ImageView avatar = new ImageView(SessionManager.getInstance().getCurrentUser().getAvatar());
        avatar.setFitWidth(40);
        avatar.setFitHeight(40);
        avatar.setStyle("-fx-background-radius: 50%;");

        // Comment field
        commentField = new TextArea();
        commentField.setPromptText("Add a comment...");
        commentField.setPrefRowCount(1);
        commentField.setWrapText(true);
        commentField.setPrefWidth(250);

        // Rating box (use only overall if you want)
        userRatingBox = new UserRatingBox();
        userRatingBox.setPrefWidth(150); // Adjust as needed

        // Add avatar and comment field side by side
        inputRow.getChildren().addAll(avatar, commentField);

        commentButton = new Button("Comment");
        commentButton.setOnAction(e -> {
            if (!commentField.getText().trim().isEmpty()) {
                int rating = userRatingBox.getOverall();
                onSubmit.accept(commentField.getText().trim(), rating);
                collapse();
            }
        });

        commentButton.setDisable(true);
        commentField.textProperty().addListener((obs, oldText, newText) -> {
            commentButton.setDisable(newText.trim().isEmpty());
        });

        buttonBar = new HBox(10, commentButton);
        buttonBar.setAlignment(Pos.CENTER_RIGHT);
        buttonBar.setVisible(true);

        // Add everything to the main VBox: inputRow (avatar+comment), then rating, then buttons
        getChildren().addAll(inputRow, userRatingBox, buttonBar);
    }

    /**
     * Collapses the comment input view, clearing the text area and hiding the button bar.
     */
    private void collapse() {
        commentField.clear();
        commentField.setPrefRowCount(1);
        userRatingBox.reset();
    }

    /**
     * Gets the user rating box for external access to rating values.
     *
     * @return The UserRatingBox component
     */
    public UserRatingBox getUserRatingBox() {
        return userRatingBox;
    }
}
