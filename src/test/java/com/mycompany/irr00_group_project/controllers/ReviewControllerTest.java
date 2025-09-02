package com.mycompany.irr00_group_project.controllers;

import com.mycompany.irr00_group_project.models.User;
import com.mycompany.irr00_group_project.models.reviewables.Review;
import com.mycompany.irr00_group_project.models.reviewables.targets.Artist;
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
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ReviewControllerTest tests the functionality of the ReviewController class.
 * It verifies that reviews can be added, edited, and liked, and that notifications
 * are sent correctly.
 */
public class ReviewControllerTest {

    private static final String TEST_DB_PATH = "src/test/resources/temporary-test-files/";
    private static final Path TEST_DB_DIR = Paths.get("src/test/resources/temporary-test-files");
    private Database db;
    private User currentUser;
    private Artist artist;

    /**
     * Sets up the test environment by initializing the database,
     * creating a test artist, and a test user.
     * This method is called before each test.
     * @throws IOException if there is an error initializing the database.
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
        db.getReviewableTable(ReviewableType.ARTIST).addLine(new String[]{"TestArtist", "img"});
        artist = (Artist) db.turnLineToReviewable(
            db.getReviewableTable(ReviewableType.ARTIST).getLines("name", "TestArtist").get(0));

        currentUser = new User("0", "alice", "alice@example.com");
        db.addUser(currentUser, "p", "s");
    }

    /**
     * Cleans up the test environment by deleting any files created during the tests.
     * This method is called after each test.
     */
    @AfterEach
    public void tearDown() throws IOException {
        deleteDirectoryRecursively(TEST_DB_DIR);
    }

    @Test
    public void testAddReviewAddsReviewAndNotifies() throws Exception {
        List<Review> changedReviews = new ArrayList<>();
        AtomicReference<String> notificationMsg = new AtomicReference<>("");

        ReviewController controller = new ReviewController(
            artist,
            reviews -> changedReviews.addAll(reviews),
            notificationMsg::set,
            currentUser
        );

        controller.addReview("Great artist!", 5);
        List<Review> reviews = controller.getReviews();
        assertFalse(reviews.isEmpty());
        assertEquals("Great artist!", reviews.get(0).getText());
        assertEquals(5, reviews.get(0).getRating());
        assertTrue(changedReviews.size() > 0);
        assertEquals("Review posted successfully.", notificationMsg.get());
    }

    @Test
    public void testAddReviewNoUserDoesNothing() throws Exception {
        AtomicBoolean notified = new AtomicBoolean(false);

        ReviewController controller = new ReviewController(
            artist,
            reviews -> {},
            msg -> notified.set(true),
            null
        );

        controller.addReview("Should not be added", 4);
        assertFalse(notified.get());
        assertTrue(controller.getReviews().isEmpty());
    }

    @Test
    public void testEditReviewUpdatesReviewAndNotifies() throws Exception {
        List<Review> changedReviews = new ArrayList<>();
        AtomicReference<String> notificationMsg = new AtomicReference<>("");

        ReviewController controller = new ReviewController(
            artist,
            reviews -> changedReviews.addAll(reviews),
            notificationMsg::set,
            currentUser
        );

        controller.addReview("Original review", 3);
        Review original = controller.getReviews().get(0);

        controller.editReview(original, "Edited review", 4);

        List<Review> reviews = controller.getReviews();
        assertEquals("Edited review", reviews.get(0).getText());
        assertEquals(4, reviews.get(0).getRating());
        assertTrue(changedReviews.size() > 0);
        assertEquals("Review posted successfully.", notificationMsg.get());
    }

    @Test
    public void testEditReviewNotFoundNotifies() throws Exception {
        AtomicReference<String> notificationMsg = new AtomicReference<>("");

        ReviewController controller = new ReviewController(
            artist,
            reviews -> {},
            notificationMsg::set,
            currentUser
        );

        // Create a review that is not in the DB
        Review fakeReview = new Review(currentUser, "Not in DB", artist, 2);

        controller.editReview(fakeReview, "Should fail", 1);

        assertEquals("Review not found in database.", notificationMsg.get());
    }

    @Test
    public void testHandleLikeAddsAndRemovesLike() throws Exception {
        ReviewController controller = new ReviewController(
            artist,
            reviews -> {},
            msg -> {},
            currentUser
        );

        controller.addReview("Like test", 5);
        Review review = db.turnLineToReview(db.getReviewsTable()
            .getLines("content", "Like test").get(0));
        System.out.println("Review ID: " + review.getId());
        // Like the review
        controller.handleLike(review);
        List<Map<String, String>> likesAfterLike = db.getLikesTable()
            .getLines("post_id", review.getId());
        assertFalse(likesAfterLike.isEmpty());
        boolean userLiked = likesAfterLike.stream()
            .anyMatch(line -> line.get("user_id").equals(currentUser.getId()));
        assertTrue(userLiked);

        // Like again (should unlike)
        controller.handleLike(review);
        List<Map<String, String>> likesAfterUnlike = db.getLikesTable()
            .getLines("post_id", review.getId());
        boolean userStillLikes = likesAfterUnlike.stream()
            .anyMatch(line -> line.get("user_id").equals(currentUser.getId()));
        assertFalse(userStillLikes);
    }

    /**
     * Cleans the specified directory by deleting all files within it.
     * If the directory does not exist, it does nothing.
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
