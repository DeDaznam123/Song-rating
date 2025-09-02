package com.mycompany.irr00_group_project.controllers;

import com.mycompany.irr00_group_project.models.reviewables.targets.Artist;
import com.mycompany.irr00_group_project.models.reviewables.targets.Album;
import com.mycompany.irr00_group_project.models.reviewables.targets.Song;
import com.mycompany.irr00_group_project.models.reviewables.ReviewableType;
import com.mycompany.irr00_group_project.services.Database;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * DetailsControllerTest tests the functionality of the DetailsController class.
 * It verifies that the controller correctly fetches and sets the artist, album,
 * and songs for a given reviewable item (song, album, or artist).
 */
public class DetailsControllerTest {
    private static final String TEST_DB_PATH = "src/test/resources/temporary-test-files/";
    private static final Path TEST_DB_DIR = Paths.get("src/test/resources/temporary-test-files");
    private Database db;

    /**
     * Sets up the test database before each test.
     * Creates a temporary directory for the test database if it does not exist,
     * or cleans it if it does.
     */
    @BeforeEach
    public void setUp() throws IOException {
        // Ensure clean test directory
        if (!Files.exists(TEST_DB_DIR)) {
            Files.createDirectories(TEST_DB_DIR);
        } else {
            cleanDirectory(TEST_DB_DIR);
        }

        db = new Database(TEST_DB_PATH);
    }

    /**
     * Cleans up the test database after each test.
     * Deletes all files in the temporary test directory.
     */
    @AfterEach
    public void tearDown() throws IOException {
        deleteDirectoryRecursively(TEST_DB_DIR);
    }

    @Test
    public void testSongConstructorSetsArtistAndAlbum() throws IOException {
        // Add artist
        db.getReviewableTable(ReviewableType.ARTIST).addLine(new String[]{"Artist1", "img1"});
        Artist artist = (Artist) db.turnLineToReviewable(
                db.getReviewableTable(ReviewableType.ARTIST).getLines("name", "Artist1").get(0));
        // Add album
        db.getReviewableTable(ReviewableType.ALBUM).addLine(
                new String[]{"Album1", artist.getId(), "imgA"});
        Album album = (Album) db.turnLineToReviewable(
                db.getReviewableTable(ReviewableType.ALBUM).getLines("title", "Album1").get(0));
        // Add song
        db.getReviewableTable(ReviewableType.SONG).addLine(
                new String[]{"Song1", artist.getId(), album.getId(), "imgS"});
        Song song = (Song) db.turnLineToReviewable(
                db.getReviewableTable(ReviewableType.SONG).getLines("title", "Song1").get(0));

        DetailsController controller = new DetailsController(song);
        assertEquals(artist.getId(), controller.getArtist().getId());
        assertEquals(1, controller.getAlbums().size());
        assertEquals(album.getId(), controller.getAlbums().get(0).getId());
        assertNull(controller.getSongs());
    }

    @Test
    public void testAlbumConstructorFetchesSongsAndArtist() throws IOException {
        // Add artist
        db.getReviewableTable(ReviewableType.ARTIST).addLine(new String[]{"Artist1", "img1"});
        Artist artist = (Artist) db.turnLineToReviewable(
                db.getReviewableTable(ReviewableType.ARTIST).getLines("name", "Artist1").get(0));
        // Add album
        db.getReviewableTable(ReviewableType.ALBUM).addLine(
                new String[]{"Album1", artist.getId(), "imgA"});
        Album album = (Album) db.turnLineToReviewable(
                db.getReviewableTable(ReviewableType.ALBUM).getLines("title", "Album1").get(0));
        // Add songs
        db.getReviewableTable(ReviewableType.SONG).addLine(
                new String[]{"Song1", artist.getId(), album.getId(), "imgS"});
        db.getReviewableTable(ReviewableType.SONG).addLine(
                new String[]{"Song2", artist.getId(), album.getId(), "imgS2"});

        DetailsController controller = new DetailsController(album);
        List<Song> songs = controller.getSongs();
        assertEquals(2, songs.size());
        assertEquals(artist.getId(), controller.getArtist().getId());
        assertNull(controller.getAlbums());
    }

    @Test
    public void testArtistConstructorFetchesSongsAndAlbums() throws IOException {
        // Add artist
        db.getReviewableTable(ReviewableType.ARTIST).addLine(new String[]{"Artist1", "img1"});
        Artist artist = (Artist) db.turnLineToReviewable(
                db.getReviewableTable(ReviewableType.ARTIST).getLines("name", "Artist1").get(0));
        // Add albums
        db.getReviewableTable(ReviewableType.ALBUM).addLine(
                new String[]{"Album1", artist.getId(), "imgA"});
        db.getReviewableTable(ReviewableType.ALBUM).addLine(
                new String[]{"Album2", artist.getId(), "imgB"});
        Album album1 = (Album) db.turnLineToReviewable(
                db.getReviewableTable(ReviewableType.ALBUM).getLines("title", "Album1").get(0));
        Album album2 = (Album) db.turnLineToReviewable(
                db.getReviewableTable(ReviewableType.ALBUM).getLines("title", "Album2").get(0));
        // Add songs
        db.getReviewableTable(ReviewableType.SONG).addLine(
                new String[]{"Song1", artist.getId(), album1.getId(), "imgS"});
        db.getReviewableTable(ReviewableType.SONG).addLine(
                new String[]{"Song2", artist.getId(), album2.getId(), "imgS2"});

        DetailsController controller = new DetailsController(artist);
        List<Song> songs = controller.getSongs();
        List<Album> albums = controller.getAlbums();
        assertEquals(2, songs.size());
        assertEquals(2, albums.size());
        assertNull(controller.getArtist());
    }

    private void cleanDirectory(Path dir) throws IOException {
        if (!Files.exists(dir)) {
            return;
        }
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (Path entry : stream) {
                Files.deleteIfExists(entry);
            }
        }
    }

    /**
     * Deletes a directory and all its contents recursively.
     *
     * @param dir The directory to delete.
     * @throws IOException If an I/O error occurs.
     */
    private static void deleteDirectoryRecursively(Path dir) throws IOException {
        if (Files.exists(dir)) {
            Files.walk(dir)
                .sorted(Comparator.reverseOrder())
                .forEach(path -> {
                    try {
                        Files.deleteIfExists(path);
                    } catch (IOException e) {
                        System.err.println("Failed to delete: " + path);
                    }
                });
        }
    }
}
