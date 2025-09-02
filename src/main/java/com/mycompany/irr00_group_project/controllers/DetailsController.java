package com.mycompany.irr00_group_project.controllers;

import java.util.List;

import com.mycompany.irr00_group_project.models.reviewables.targets.Album;
import com.mycompany.irr00_group_project.models.reviewables.targets.Artist;
import com.mycompany.irr00_group_project.models.reviewables.targets.Song;
import com.mycompany.irr00_group_project.services.Database;

/**
 * DetailsController is responsible for managing the details of a song, album, or artist.
 * It retrieves the relevant information from the database and provides access to it.
 */
public class DetailsController {
    private List<Song> songs;
    private Artist artist;
    private List<Album> albums;

    /**
     * Constructor for DetailsController that initializes the controller
     * with a Song object. It retrieves the artist and album from the song.
     * @param song The Song object to initialize the controller with.
     * If the song is part of an album, it will also retrieve that album.
     */
    public DetailsController(Song song) {
        this.artist = song.getArtist();
        this.albums = new java.util.ArrayList<>();
        this.albums.add(song.getAlbum());
    }

    /**
     * Constructor for DetailsController that initializes the controller
     * with an Album object. It retrieves the songs from the album.
     * @param album The Album object to initialize the controller with.
     */
    public DetailsController(Album album) {
        try {
            this.songs = Database.getInstance().getSongsFromAlbum(album.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.artist = album.getArtist();
    }

    /**
     * Constructor for DetailsController that initializes the controller
     * with an Artist object. It retrieves the songs and albums from the artist.
     * @param artist The Artist object to initialize the controller with.
     */
    public DetailsController(Artist artist) {
        try {
            this.songs = Database.getInstance().getSongsFromArtist(artist.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            this.albums = Database.getInstance().getAlbumsFromArtist(artist.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Function to get a list of the albums.
     * @return a list of albums
     */
    public List<Album> getAlbums() {
        return albums;
    }

    /**
     * Function to get the Artist.
     * @return Reference to The Artist
     */
    public Artist getArtist() {
        return artist;
    }

    /**
     * Function to get a list of the Songs.
     * @return a list of the songs
     */
    public List<Song> getSongs() {
        return songs;
    }
}
