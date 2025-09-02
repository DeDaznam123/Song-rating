package com.mycompany.irr00_group_project.models.reviewables.targets;

import com.mycompany.irr00_group_project.models.reviewables.Reviewable;
import com.mycompany.irr00_group_project.models.reviewables.ReviewableType;

/**
 * Represents a song in the application.
 */
public class Song extends Reviewable {
    private Artist artist;
    private Album album;

    /**
     * Constructor for the Song class.
     *
     * @param id     The unique identifier for the song.
     * @param title  The title of the song.
     * @param image  The path to the image file for the song.
     * @param artist The artist of the song.
     */
    public Song(String id, String title, String image, Artist artist, Album album) {
        super(id, title, image);
        this.artist = artist;
        this.album = album;
    }

    // Accessors.
    
    @Override
    public ReviewableType getType() {
        return ReviewableType.SONG;
    }

    /**
     * Returns the artist of the song.
     *
     * @return The artist of the song.
     */
    public Artist getArtist() {
        return artist;
    }

    /**
     * Returns the album of the song.
     *
     * @return The album of the song.
     */
    public Album getAlbum() {
        return album;
    }

    // Mutators.

    /**
     * Sets the artist of the song.
     *
     * @param artist The new artist for the song.
     */
    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    /**
     * Sets the album of the song.
     *
     * @param album The new album for the song.
     */
    public void setAlbum(Album album) {
        this.album = album;
    }
}