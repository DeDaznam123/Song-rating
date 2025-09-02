package com.mycompany.irr00_group_project;

import com.mycompany.irr00_group_project.services.Database;
import com.mycompany.irr00_group_project.models.User;
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
 * UserTest tests the functionality of the User class, specifically the follow and
 * unfollow methods.
 * It verifies that users can follow and unfollow each other correctly, and that
 * appropriate exceptions are thrown when trying to follow or unfollow users
 * incorrectly.
 */
public class UserTest {

    private static final String TEST_DB_PATH = "src/test/resources/temporary-test-files/";
    private static final Path TEST_DB_DIR = Paths.get("src/test/resources/temporary-test-files");
    private Database db;
    private User user1;
    private User user2;

    /**
     * Sets up the test environment by initializing the database and adding
     * test users.
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
        user1 = new User("0", "alice", "alice@example.com");
        db.addUser(user1, "h", "s");
        user2 = new User("0", "bob", "bob@example.com");
        db.addUser(user2, "h", "s");
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
    public void testFollowAndUnfollow() {
        // Initially, user1 does not follow user2
        List<User> followed = user1.getFollowedUsers();
        assertTrue(followed.isEmpty());

        // user1 follows user2
        user1.follow(user2.getId());
        followed = user1.getFollowedUsers();
        assertEquals(1, followed.size());
        assertEquals(user2.getId(), followed.get(0).getId());

        // user1 unfollows user2
        user1.unfollow(user2.getId());
        followed = user1.getFollowedUsers();
        assertTrue(followed.isEmpty());
    }

    /**
     * Deletes all files in the specified directory.
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
     * This method is used to clean up the test directory after all tests are run.
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
