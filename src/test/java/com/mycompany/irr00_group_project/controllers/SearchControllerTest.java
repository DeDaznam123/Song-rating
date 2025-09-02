package com.mycompany.irr00_group_project.controllers;

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

import static org.junit.jupiter.api.Assertions.*;

/**
 * SearchControllerTest tests the functionality of the SearchController class.
 * It verifies that the search methods return expected results for users and reviewables.
 */
public class SearchControllerTest {

    private static final String TEST_DB_PATH = "src/test/resources/temporary-test-files/";
    private static final Path TEST_DB_DIR = Paths.get("src/test/resources/temporary-test-files");
    private Database db;
    private SearchController controller;

    /**
     * Sets up the test environment by initializing the database and adding test data.
     * This method is called before each test.
     *
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
        controller = new SearchController();

        // Add users
        db.getUsersTable().addLine(
            new String[]{"alice", "alice@example.com", "2024-01-01T00:00:00"});
        db.getUsersTable().addLine(
            new String[]{"bob", "bob@example.com", "2024-01-01T00:00:00"});

        // Add reviewables (e.g., artists)
        db.getReviewableTable(ReviewableType.ARTIST)
            .addLine(new String[]{"The Beatles", "beatles.png"});
        db.getReviewableTable(ReviewableType.ARTIST).addLine(new String[]{"Queen", "queen.png"});
    }

    /**
     * Cleans up the test environment by deleting the test database files.
     * This method is called after each test.
     */
    @AfterEach
    public void tearDown() throws IOException {
        deleteDirectoryRecursively(TEST_DB_DIR);
    }

    @Test
    public void testSearchUsersReturnsMatchingUsers() throws IOException {
        controller.updateSearchResultsUsers("alice");
        assertEquals(1, controller.getSearchResultsUsers().size());
        assertEquals("alice", controller.getSearchResultsUsers().get(0).getUsername());

        controller.updateSearchResultsUsers("bob");
        assertEquals(1, controller.getSearchResultsUsers().size());
        assertEquals("bob", controller.getSearchResultsUsers().get(0).getUsername());
    }

    @Test
    public void testSearchUsersReturnsEmptyForNoMatch() throws IOException {
        controller.updateSearchResultsUsers("charlie");
        assertTrue(controller.getSearchResultsUsers().isEmpty());
    }

    @Test
    public void testUpdateSearchResultsUsersClearsOnEmptyQuery() throws IOException {
        controller.updateSearchResultsUsers("alice");
        assertFalse(controller.getSearchResultsUsers().isEmpty());

        controller.updateSearchResultsUsers("");
        assertTrue(controller.getSearchResultsUsers().isEmpty());
    }

    @Test
    public void testSearchReviewablesReturnsMatchingReviewables() {
        controller.updateSearchResultsReviewables("beatles");
        assertEquals(1, controller.getSearchResultsReviewables().size());
        assertTrue(controller.getSearchResultsReviewables()
            .get(0).getName().toLowerCase().contains("beatles"));

        controller.updateSearchResultsReviewables("queen");
        assertEquals(1, controller.getSearchResultsReviewables().size());
        assertTrue(controller.getSearchResultsReviewables()
            .get(0).getName().toLowerCase().contains("queen"));
    }

    @Test
    public void testUpdateSearchResultsReviewablesClearsOnEmptyQuery() {
        controller.updateSearchResultsReviewables("queen");
        assertFalse(controller.getSearchResultsReviewables().isEmpty());

        controller.updateSearchResultsReviewables("");
        assertTrue(controller.getSearchResultsReviewables().isEmpty());
    }

    /**
     * Cleans the specified directory by deleting all files within it.
     *
     * @param dir The directory to clean.
     * @throws IOException if there is an error accessing the directory.
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
