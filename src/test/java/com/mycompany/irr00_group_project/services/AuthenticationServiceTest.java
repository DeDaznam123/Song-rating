package com.mycompany.irr00_group_project.services;

import com.mycompany.irr00_group_project.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Tests for the AuthenticationService class, covering user authentication,
 * password validation, and email validation.
 */
public class AuthenticationServiceTest {

    private static final String TEST_DB_PATH = "src/test/resources/temporary-test-files/";
    private static final Path TEST_DB_DIR = Paths.get("src/test/resources/temporary-test-files");

    /**
     * Sets up the test environment by creating a temporary database directory
     * and populating it with test CSV files.
     * This method is called before each test to ensure a clean state.
     */
    @BeforeEach
    public void setUp() throws IOException {
        // Ensure clean test directory
        if (!Files.exists(TEST_DB_DIR)) {
            Files.createDirectories(TEST_DB_DIR);
        } else {
            cleanDirectory(TEST_DB_DIR);
        }

        // Initialize database after creating files
        new Database(TEST_DB_PATH);
    }

    /**
     * Cleans up the test environment by deleting the test database files.
     */
    @AfterEach
    public void tearDown() throws IOException {
        cleanDirectory(TEST_DB_DIR);
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

    @Test
    public void testValidatePassword() {
        assertTrue(AuthenticationService.validatePassword("Abcdef1!"));
        assertFalse(AuthenticationService.validatePassword("short1!"));
        assertFalse(AuthenticationService.validatePassword("NoSpecialChar1"));
        assertFalse(AuthenticationService.validatePassword("nouppercase1!"));
        assertFalse(AuthenticationService.validatePassword("NOLOWERCASE1!"));
        assertFalse(AuthenticationService.validatePassword("NoNumber!"));
        assertFalse(AuthenticationService.validatePassword(null));
    }

    @Test
    public void testValidateEmail() {
        assertTrue(AuthenticationService.validateEmail("test@example.com"));
        assertFalse(AuthenticationService.validateEmail("invalid-email"));
        assertFalse(AuthenticationService.validateEmail("test@.com"));
        assertFalse(AuthenticationService.validateEmail("test@com"));
        assertFalse(AuthenticationService.validateEmail(""));
        assertFalse(AuthenticationService.validateEmail(null));
    }

    @Test
    public void testSignUpAndLoginSuccess() throws IOException {
        User user = new User("0", "alice", "alice@example.com");
        String password = "Password1!";
        AuthenticationService.signUp(user, password);

        User loggedIn = AuthenticationService.login("alice", password);
        assertNotNull(loggedIn);
        assertEquals("alice", loggedIn.getUsername());
    }

    @Test
    public void testSignUpDuplicateUsernameThrows() throws IOException {
        User user1 = new User("0", "bob", "bob@example.com");
        User user2 = new User("0", "bob", "bob2@example.com");
        String password = "Password1!";
        AuthenticationService.signUp(user1, password);
        assertThrows(IllegalArgumentException.class,
            () -> AuthenticationService.signUp(user2, password));
    }

    @Test
    public void testSignUpDuplicateEmailThrows() throws IOException {
        User user1 = new User("0", "bob", "bob@example.com");
        User user2 = new User("0", "bobby", "bob@example.com");
        String password = "Password1!";
        AuthenticationService.signUp(user1, password);
        assertThrows(IllegalArgumentException.class,
            () -> AuthenticationService.signUp(user2, password));
    }

    @Test
    public void testSignUpInvalidEmailThrows() {
        User user = new User("0", "charlie", "invalid-email");
        String password = "Password1!";
        assertThrows(IllegalArgumentException.class,
            () -> AuthenticationService.signUp(user, password));
    }

    @Test
    public void testSignUpInvalidPasswordThrows() {
        User user = new User("0", "dave", "dave@example.com");
        String password = "short";
        assertThrows(IllegalArgumentException.class,
            () -> AuthenticationService.signUp(user, password));
    }

    @Test
    public void testLoginWrongPasswordReturnsNull() throws IOException {
        User user = new User("0", "eve", "eve@example.com");
        String password = "Password1!";
        AuthenticationService.signUp(user, password);

        User loggedIn = AuthenticationService.login("eve", "WrongPassword1!");
        assertNull(loggedIn);
    }

    @Test
    public void testLoginNonExistentUserReturnsNull() {
        User loggedIn = AuthenticationService.login("ghost", "Password1!");
        assertNull(loggedIn);
    }

    @Test
    public void testLoginNullOrEmptyCredentialsReturnsNull() {
        assertNull(AuthenticationService.login(null, "Password1!"));
        assertNull(AuthenticationService.login("alice", null));
        assertNull(AuthenticationService.login("", "Password1!"));
        assertNull(AuthenticationService.login("alice", ""));
    }
}
