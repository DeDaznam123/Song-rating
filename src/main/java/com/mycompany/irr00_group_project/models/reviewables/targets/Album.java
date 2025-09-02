package com.mycompany.irr00_group_project.models.reviewables.targets;

import com.mycompany.irr00_group_project.models.reviewables.Reviewable;
import com.mycompany.irr00_group_project.models.reviewables.ReviewableType;

/**
 * Represents an album in the application.
 */
public class Album extends Reviewable {
    private Artist artist;

    /**
     * Constructor for the Album class.
     *
     * @param id     The unique identifier for the album.
     * @param title  The title of the album.
     * @param image  The path to the image file for the album.
     * @param artist The artist of the album.
     */
    public Album(String id, String title, String image, Artist artist) {
        super(id, title, image);
        this.artist = artist;
    }

    // Accessors.

    @Override
    public ReviewableType getType() {
        return ReviewableType.ALBUM;
    }

    /**
     * Returns the artist of the album.
     *
     * @return The artist of the album.
     */
    public Artist getArtist() {
        return artist;
    }

    // Mutators.

    /**
     * Sets the artist of the album.
     *
     * @param artist The new artist for the album.
     */
    public void setArtist(Artist artist) {
        this.artist = artist;
    }
}