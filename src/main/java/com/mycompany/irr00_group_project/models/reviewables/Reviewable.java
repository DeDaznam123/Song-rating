package com.mycompany.irr00_group_project.models.reviewables;

import java.io.IOException;
import java.util.Map;

import com.mycompany.irr00_group_project.services.CommentRecommenderService;
import com.mycompany.irr00_group_project.utils.ImageUtils;

import javafx.scene.image.Image;

/**
 * Reviewable interface represents an entity that can be reviewed, such as a song, album, or artist.
 * It provides methods to get the title, type, reviews, and to add a review.
 */
public abstract class Reviewable {

    private String id;
    private String name;
    private final Image image; // Path to image file.
    private Map<String, Double> tfMap;
    private double[] tfIdfVector;

    /**
     * Constructor.
     *
     * @param id    The unique identifier for the song.
     * @param name  The name/title of the song.
     * @param image The path to the image file for the song.
     */
    public Reviewable(String id, String name, String image) {
        this.id = id;
        this.name = name;
        this.image = ImageUtils.loadImage(image);
    }

    public abstract ReviewableType getType();

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public Image getImage() {
        return this.image;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Reviewable other = (Reviewable) obj;
        return this.getId().equals(other.getId()) && this.getType().equals(other.getType());
    }

    /**
     * Sets the unique identifier of the song.
     *
     * @param id The new unique identifier for the song.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Sets the name/title of the Reviewable.
     *
     * @param name The new name/title of the Reviewable.
     */
    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Double> getTfMap() {
        return tfMap;
    }

    public double[] getTfIdfVector() {
        return tfIdfVector;
    }

    /**
     * Updates the TF map for the Reviewable.
     * @throws IOException .
     */
    public void updateTfMap() throws IOException {
        tfMap = CommentRecommenderService.computeTfMap(this);
        updateTfIdfVector();
    }

    /**
     * Updates and stores the TF-IDF vector for the Reviewable.
     *
     * @throws IOException .
     */
    private void updateTfIdfVector() throws IOException {
        tfIdfVector = CommentRecommenderService.computeTfIdfVector(this);
    }
}
