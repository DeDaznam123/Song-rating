package com.mycompany.irr00_group_project.services;

import com.mycompany.irr00_group_project.models.User;
import com.mycompany.irr00_group_project.models.reviewables.Reviewable;
import com.mycompany.irr00_group_project.models.reviewables.ReviewableType;
import com.mycompany.irr00_group_project.models.reviewables.targets.Artist;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Unit tests for the RatingRecommenderService class.
 * This class tests the functionality of the recommender service,
 * including recommendations based on user ratings and reviews.
 */
public class RatingRecommenderServiceTest {

    private RatingRecommenderService recommender;
    private Database db;
    private static final String TEST_DB_PATH = "src/test/resources/temporary-test-files";
    private static final Path TEST_DB_DIR = Paths.get(TEST_DB_PATH);
    private User user;
    private User user2;

    /**
     * Sets up the test environment before all tests are run.
     * @throws IOException if an error occurs while setting up the database.
     * Initializes the database, adds users, artists, reviewables, and reviews.
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
        System.out.println("Database initialized at: " + TEST_DB_PATH);
        //Add users
        user = new User("0", "Alice", "Alice@example.com");
        db.addUser(user, "1", "1");

        user2 = new User("0", "Bob", "Bob@example.com");
        db.addUser(user2, "1", "1");
        System.out.println("User1 ID: " + user.getId());
        //Add artists
        db.getReviewableTable(ReviewableType.ARTIST).addLine(new String[]{"Artist1", "img1"});
        Artist artist = (Artist) db.turnLineToReviewable(
            db.getReviewableTable(ReviewableType.ARTIST).getLines("name", "Artist1").get(0));
        System.out.println(artist.getId());
        db.getReviewableTable(ReviewableType.ARTIST).addLine(new String[]{"Artist2", "img2"});
        Artist otherArtist = (Artist) db.turnLineToReviewable(
            db.getReviewableTable(ReviewableType.ARTIST).getLines("name", "Artist2").get(0));

        //Add reviewables
        db.getReviewableTable(ReviewableType.SONG)
            .addLine(new String[]{"SongA", artist.getId(), "", "imgA"});
        db.getReviewableTable(ReviewableType.SONG)
            .addLine(new String[]{"SongB", artist.getId(), "", "imgB"});
        db.getReviewableTable(ReviewableType.SONG)
            .addLine(new String[]{"OtherSong", artist.getId(), "", "imgC"});
        db.getReviewableTable(ReviewableType.SONG)
            .addLine(new String[]{"OtherSong", otherArtist.getId(), "", "imgD"});
        db.getReviewableTable(ReviewableType.SONG)
            .addLine(new String[]{"OtherSong", otherArtist.getId(), "", "imgE"});
        db.getReviewableTable(ReviewableType.SONG)
            .addLine(new String[]{"OtherSong", otherArtist.getId(), "", "imgF"});

        //Add Reviews
        db.getReviewsTable().addLine(
            new String[]{user.getId(), "Great!", "2025-01-01T00:00:00", "1", "5", "SONG"});

        db.getReviewsTable().addLine(
            new String[]{user2.getId(), "Great!!!", "2025-01-01T00:00:00", "1", "5", "SONG"});

        db.getReviewsTable().addLine(
            new String[]{user2.getId(), "Great!!!", "2025-01-01T00:00:00", "2", "5", "SONG"});

        recommender = new RatingRecommenderService();
    }

    /**
     * Cleans up the test environment after all tests are run.
     * Deletes the temporary test database files.
     * This is necessary to ensure that each test run starts with a clean state.
     */
    @AfterAll
    public static void cleanUpTestDirectory() throws IOException {
        deleteDirectoryRecursively(TEST_DB_DIR);
    }

    @Test
    public void testRating() throws IOException {
        List<Reviewable> rec = recommender.getRecommendations("1", 1);
        assertEquals(rec.get(0).getId(), "1");
    }

    @Test
    public void testRatingMatrixIsNotNull() throws IOException {
        recommender.printRatingMatrix();
        assertNotNull(recommender);
    }

    @Test
    public void testGetRecommendationsReturnsCorrectLength() throws IOException {
        String testUserId = "0";
        int expectedCount = 3;
        List<Reviewable> recommendations = recommender
            .getRecommendations(testUserId, expectedCount);
        assertEquals(expectedCount, recommendations.size());
    }

    @Test
    public void testGetRecommendationsReturnsNonNull() throws IOException {
        String testUserId = "0";
        List<Reviewable> recommendations = recommender
            .getRecommendations(testUserId, 5);
        for (Reviewable reviewable : recommendations) {
            assertNotNull(reviewable);
        }
    }

    /**
     * Deletes all files in the specified directory.
     * This method is used to clean up the test directory after each test.
     *
     * @param dir The directory to clean.
     * @throws IOException If an I/O error occurs.
     */
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
