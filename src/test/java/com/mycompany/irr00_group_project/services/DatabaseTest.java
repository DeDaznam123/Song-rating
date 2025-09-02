package com.mycompany.irr00_group_project.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.mycompany.irr00_group_project.models.User;
import com.mycompany.irr00_group_project.models.reviewables.Review;
import com.mycompany.irr00_group_project.models.reviewables.Reviewable;
import com.mycompany.irr00_group_project.models.reviewables.ReviewableType;
import com.mycompany.irr00_group_project.models.reviewables.targets.Album;
import com.mycompany.irr00_group_project.models.reviewables.targets.Artist;
import com.mycompany.irr00_group_project.models.reviewables.targets.Song;

/**
 * Unit tests for the Database service.
 * This class tests various functionalities of the Database class, including user management,
 * reviewable retrieval, and error handling.
 */
public class DatabaseTest {
    private static final String TEST_DB_PATH = "src/test/resources/temporary-test-files/";
    private static final Path TEST_DB_DIR = Paths.get(TEST_DB_PATH);
    private Database db;    
    
    /**
     * Sets up the test environment before each test.
     * Initializes the database and creates necessary CSV files.
     *
     * @throws IOException if an error occurs while setting up the database.
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
     * Cleans up the test database directory after each test.
     */
    @AfterEach
    public void tearDown() {
        File dir = new File(TEST_DB_PATH);
        if (dir.exists()) {
            for (File f : dir.listFiles()) {
                f.delete();
            }
        }
    }

    @Test
    public void testAddUserAndGetUserById() throws IOException {
        User user = new User("0", "alice", "alice@example.com");
        db.addUser(user, "hashedpw", "salt");
        User fetched = db.getUserById("0");
        assertNotNull(fetched);
        assertEquals("alice", fetched.getUsername());
    }

    @Test
    public void testAddUserThrowsOnNullUser() {
        assertThrows(IllegalArgumentException.class, () -> db.addUser(null, "pw", "salt"));
    }

    @Test
    public void testAddUserThrowsOnNullPassword() {
        User user = new User("0", "alice", "alice@example.com");
        assertThrows(IllegalArgumentException.class, () -> db.addUser(user, null, "salt"));
    }

    @Test
    public void testAddUserThrowsOnEmptyPassword() {
        User user = new User("0", "alice", "alice@example.com");
        assertThrows(IllegalArgumentException.class, () -> db.addUser(user, "", "salt"));
    }

    @Test
    public void testAddUserThrowsOnNullSalt() {
        User user = new User("0", "alice", "alice@example.com");
        assertThrows(IllegalArgumentException.class, () -> db.addUser(user, "pw", null));
    }

    @Test
    public void testAddUserThrowsOnEmptySalt() {
        User user = new User("0", "alice", "alice@example.com");
        assertThrows(IllegalArgumentException.class, () -> db.addUser(user, "pw", ""));
    }

    @Test
    public void testGetUserByIdThrowsOnNull() {
        assertThrows(IllegalArgumentException.class, () -> db.getUserById(null));
    }

    @Test
    public void testGetUserByIdThrowsOnEmpty() {
        assertThrows(IllegalArgumentException.class, () -> db.getUserById(""));
    }

    @Test
    public void testGetUserByIdReturnsNullIfNotFound() throws IOException {
        assertNull(db.getUserById("999"));
    }

    @Test
    public void testSearchUsers() throws IOException {
        User user = new User("0", "alice", "alice@example.com");
        User user2 = new User("1", "alex", "alex@example.com");
        User user3 = new User("2", "petar", "petar@example.com");
        db.addUser(user, "pw", "salt");
        db.addUser(user2, "pw2", "salt2");
        db.addUser(user3, "pw3", "salt3");
        List<User> found = db.searchUsers("al");
        List<String> usernames = new ArrayList<>();
        for (User u : found) {
            usernames.add(u.getUsername());
        }
        assertFalse(found.size() != 2);
        assertFalse(usernames.contains("petar"));
        assertTrue(usernames.contains("alice"));
        assertTrue(usernames.contains("alex"));
    }

    @Test
    public void testSearchUsersThrowsOnNull() {
        assertThrows(IllegalArgumentException.class, () -> db.searchUsers(null));
    }

    @Test
    public void testSearchUsersThrowsOnEmpty() {
        assertThrows(IllegalArgumentException.class, () -> db.searchUsers(""));
    }

    @Test
    public void testGetFollowedUsers() throws IOException {
        User user1 = new User("0", "alice", "alice@example.com");
        User user2 = new User("1", "bob", "bob@example.com");
        User user3 = new User("2", "alex", "alex@example.com");
        db.addUser(user1, "pw1", "salt1");
        db.addUser(user2, "pw2", "salt2");
        db.addUser(user3, "pw3", "salt3");
        db.getFollowsTable().addLine(new String[]{"0", "1"});
        List<User> followed = db.getFollowedUsers("0");
        assertEquals(1, followed.size());
        assertEquals("bob", followed.get(0).getUsername());
    }

    @Test
    public void testGetFollowedUsersThrowsOnNull() {
        assertThrows(IllegalArgumentException.class, () -> db.getFollowedUsers(null));
    }

    @Test
    public void testGetFollowedUsersThrowsOnEmpty() {
        assertThrows(IllegalArgumentException.class, () -> db.getFollowedUsers(""));
    }

    @Test
    public void testAuthenticate() throws Exception {
        // Prepare user and password
        User user = new User("0", "alice", "alice@example.com");
        String password = "secret";
        byte[] salt = "somesalt".getBytes();
        String saltBase64 = Base64.getEncoder().encodeToString(salt);
        String hashed = HashPasword.hashPassword(password, salt);
        db.addUser(user, hashed, saltBase64);
        User authenticateduUser = db.authenticate(user.getUsername(), password);
        assertNotNull(user);
        assertEquals(authenticateduUser.getUsername(), user.getUsername());
    }

    @Test
    public void testAuthenticateWrongPassword() throws Exception {
        User user = new User("0", "alice", "alice@example.com");
        String password = "secret";
        byte[] salt = "somesalt".getBytes();
        String saltBase64 = Base64.getEncoder().encodeToString(salt);
        String hashed = HashPasword.hashPassword(password, salt);
        db.addUser(user, hashed, saltBase64);
        User authenticateduUser = db.authenticate(user.getUsername(), "wrongpassword");
        assertNull(authenticateduUser);
    }

    @Test
    public void testAuthenticateNonExistentUser() throws Exception {
        User user = db.authenticate("notfound", "pw");
        assertNull(user);
    }

    @Test
    public void testAuthenticateNullUsername() {
        assertThrows(IllegalArgumentException.class, () -> db.authenticate(null, "pw"));
    }

    @Test
    public void testAuthenticateNullPassword() {
        assertThrows(IllegalArgumentException.class, () -> db.authenticate("alice", null));
    }

    @Test
    public void testAuthenticateEmptyUsername() {
        assertThrows(IllegalArgumentException.class, () -> db.authenticate("", "pw"));
    }

    @Test
    public void testAuthenticateEmptyPassword() {
        assertThrows(IllegalArgumentException.class, () -> db.authenticate("alice", ""));
    }

    @Test
    public void testTurnLineToReviewableHandlesUnknownType() {
        Map<String, String> lineSong = new HashMap<>();
        try {
            db.getReviewableTable(ReviewableType.ARTIST)
                .addLine(new String[]{"Test Artist", "img"});
            db.getReviewableTable(ReviewableType.ALBUM)
                .addLine(new String[]{"Test Album", "0", "img"});
        } catch (IOException e) {
            fail("Failed to set up test data: " + e.getMessage());
        }
        lineSong.put("id", "0");
        lineSong.put("title", "test");
        lineSong.put("artist_id", "0");
        lineSong.put("album_id", "0");
        lineSong.put("image", "img");
        Reviewable reviewableSong = db.turnLineToReviewable(lineSong);
        assertNotNull(reviewableSong);
        assertTrue(reviewableSong instanceof Song);
        Map<String, String> lineAlbum = new HashMap<>();
        lineAlbum.put("id", "0");
        lineAlbum.put("title", "test");
        lineAlbum.put("artist_id", "0");
        lineAlbum.put("image", "img");
        Reviewable reviewableAlbum = db.turnLineToReviewable(lineAlbum);
        assertNotNull(reviewableAlbum);
        assertTrue(reviewableAlbum instanceof Album);
        Map<String, String> lineArtist = new HashMap<>();
        lineArtist.put("id", "0");
        lineArtist.put("name", "test");
        lineArtist.put("image", "img");
        Reviewable reviewableArtist = db.turnLineToReviewable(lineArtist);
        assertNotNull(reviewableArtist);
        assertTrue(reviewableArtist instanceof Artist);

    }

    @Test
    public void testGetSongsFromArtist() throws IOException {
        db.getReviewableTable(ReviewableType.ARTIST).addLine(new String[]{"Artist1", "img1"});
        Artist artist = (Artist) db.turnLineToReviewable(
            db.getReviewableTable(ReviewableType.ARTIST).getLines("name", "Artist1").get(0));
        db.getReviewableTable(ReviewableType.ARTIST).addLine(new String[]{"Artist2", "img2"});
        Artist otherArtist = (Artist) db.turnLineToReviewable(
            db.getReviewableTable(ReviewableType.ARTIST).getLines("name", "Artist2").get(0));

        db.getReviewableTable(ReviewableType.SONG)
            .addLine(new String[]{"SongA", artist.getId(), "", "imgA"});
        db.getReviewableTable(ReviewableType.SONG)
            .addLine(new String[]{"SongB", artist.getId(), "", "imgB"});
        db.getReviewableTable(ReviewableType.SONG)
            .addLine(new String[]{"OtherSong", otherArtist.getId(), "", "imgC"});

        List<Song> songs = db.getSongsFromArtist(artist.getId());
        List<String> songTitles = new ArrayList<>();
        for (Song song : songs) {
            songTitles.add(song.getName());
        }
        assertTrue(songTitles.contains("SongA"));
        assertTrue(songTitles.contains("SongB"));
        assertFalse(songTitles.contains("OtherSong"));
    }

    @Test
    public void testGetAlbumsFromArtist() throws IOException {
        db.getReviewableTable(ReviewableType.ARTIST).addLine(new String[]{"Artist1", "img1"});
        Artist artist = (Artist) db.turnLineToReviewable(
            db.getReviewableTable(ReviewableType.ARTIST).getLines("name", "Artist1").get(0));
        db.getReviewableTable(ReviewableType.ARTIST).addLine(new String[]{"Artist2", "img2"});
        Artist otherArtist = (Artist) db.turnLineToReviewable(
            db.getReviewableTable(ReviewableType.ARTIST).getLines("name", "Artist2").get(0));

        db.getReviewableTable(ReviewableType.ALBUM)
            .addLine(new String[]{"AlbumA", artist.getId(), "imgA"});
        db.getReviewableTable(ReviewableType.ALBUM)
            .addLine(new String[]{"AlbumB", artist.getId(), "imgB"});
        db.getReviewableTable(ReviewableType.ALBUM)
            .addLine(new String[]{"OtherAlbum", otherArtist.getId(), "imgC"});

        List<Album> albums = db.getAlbumsFromArtist(artist.getId());
        List<String> albumTitles = new ArrayList<>();
        for (Album album : albums) {
            albumTitles.add(album.getName());
        }
        assertTrue(albumTitles.contains("AlbumA"));
        assertTrue(albumTitles.contains("AlbumB"));
        assertFalse(albumTitles.contains("OtherAlbum"));
    }

    @Test
    public void testGetSongsFromAlbum() throws IOException {
        db.getReviewableTable(ReviewableType.ARTIST).addLine(new String[]{"Artist1", "img1"});
        Artist artist = (Artist) db.turnLineToReviewable(
            db.getReviewableTable(ReviewableType.ARTIST).getLines("name", "Artist1").get(0));
        db.getReviewableTable(ReviewableType.ALBUM)
            .addLine(new String[]{"Album1", artist.getId(), "img1"});
        Album album = (Album) db.turnLineToReviewable(
            db.getReviewableTable(ReviewableType.ALBUM).getLines("title", "Album1").get(0));
        db.getReviewableTable(ReviewableType.ALBUM)
            .addLine(new String[]{"Album2", artist.getId(), "img2"});
        Album otherAlbum = (Album) db.turnLineToReviewable(
            db.getReviewableTable(ReviewableType.ALBUM).getLines("title", "Album2").get(0));

        db.getReviewableTable(ReviewableType.SONG)
            .addLine(new String[]{"SongA", artist.getId(), album.getId(), "imgA"});
        db.getReviewableTable(ReviewableType.SONG)
            .addLine(new String[]{"SongB", artist.getId(), album.getId(), "imgB"});
        db.getReviewableTable(ReviewableType.SONG).addLine(
                new String[]{"OtherSong", artist.getId(), otherAlbum.getId(), "imgC"});

        List<Song> songs = db.getSongsFromAlbum(album.getId());
        List<String> songTitles = new ArrayList<>();
        for (Song song : songs) {
            songTitles.add(song.getName());
        }
        assertTrue(songTitles.contains("SongA"));
        assertTrue(songTitles.contains("SongB"));
        assertFalse(songTitles.contains("OtherSong"));
    }

    @Test
    public void testSearchReviewables() throws IOException {
        db.getReviewableTable(ReviewableType.ARTIST).addLine(new String[]{"The artist", "img"});
        Artist artist = (Artist) db.turnLineToReviewable(
            db.getReviewableTable(ReviewableType.ARTIST).getLines("name", "The artist").get(0));
        db.getReviewableTable(ReviewableType.ALBUM)
            .addLine(new String[]{"Album", artist.getId(), "img"});
        Album album = (Album) db.turnLineToReviewable(
            db.getReviewableTable(ReviewableType.ALBUM).getLines("title", "Album").get(0));
        db.getReviewableTable(ReviewableType.SONG)
            .addLine(new String[]{"The song", artist.getId(), album.getId(), "img"});

        List<Reviewable> results = db.searchReviewables("the");
        List<String> names = new ArrayList<>();
        for (Reviewable r : results) {
            names.add(r.getName());
        }

        assertFalse(results.isEmpty());
        assertFalse(results.size() != 2);
        assertTrue(names.contains("The artist"));
        assertTrue(names.contains("The song"));
        assertFalse(names.contains("Album"));
    }

    @Test
    public void testGetReviewLikes() throws IOException {
        User user = new User("0", "alice", "alice@example.com");
        db.addUser(user, "p", "s");

        db.getReviewsTable().addLine(
            new String[]{user.getId(), "Great!", "2025-01-01T00:00:00", "target", "5", "SONG"});
        String reviewId1 = db.getReviewsTable().getLineId(
                new String[]{user.getId(), "Great!", "2025-01-01T00:00:00", "target", "5", "SONG"});

        db.getReviewsTable().addLine(new String[]
            {user.getId(), "Great!!!", "2025-01-01T00:00:00", "target", "5", "SONG"});
        String reviewId2 = db.getReviewsTable().getLineId(
            new String[]{user.getId(), "Great!!!", "2025-01-01T00:00:00", "target", "5", "SONG"});

        db.getLikesTable().addLine(new String[]{user.getId(), reviewId1});

        List<Map<String, String>> likes1 = db.getReviewLikes(reviewId1);
        List<Map<String, String>> likes2 = db.getReviewLikes(reviewId2);
        assertEquals(1, likes1.size());
        assertEquals(0, likes2.size());
        assertEquals(user.getId(), likes1.get(0).get("user_id"));
    }

    @Test
    public void testGetReviewRating() throws IOException {
        User user = new User("0", "alice", "alice@example.com");
        db.addUser(user, "p", "s");

        db.getReviewableTable(ReviewableType.ARTIST).addLine(new String[]{"Artist", "img2"});
        Artist artist = (Artist) db.turnLineToReviewable(
            db.getReviewableTable(ReviewableType.ARTIST).getLines("name", "Artist").get(0));

        db.getReviewsTable().addLine(new String[]
            {user.getId(), "Nice!", "2025-01-01T00:00:00", artist.getId(), "4", "ARTIST"});
        db.getReviewsTable().addLine(new String[]
            {user.getId(), "Awesome!", "2025-01-01T00:00:00", artist.getId(), "2", "ARTIST"});

        int avg = db.getReviewRating(artist);
        assertEquals(3, avg);
    }

    @Test
    public void testGetReviewsByUser() throws IOException {
        User user1 = new User("0", "alice", "alice@example.com");
        db.addUser(user1, "p", "s");
        User user2 = new User("1", "bob", "bob@example.com");
        db.addUser(user2, "p", "s");
        db.getReviewableTable(ReviewableType.ARTIST).addLine(new String[]{"Artist", "img"});
        Artist artist = (Artist) db.turnLineToReviewable(
            db.getReviewableTable(ReviewableType.ARTIST).getLines("name", "Artist").get(0));
        db.getReviewsTable().addLine(new String[]
            {user1.getId(), "Nice!", "2025-01-01T00:00:00", artist.getId(), "5", "ARTIST"});
        db.getReviewsTable().addLine(new String[]
            {user2.getId(), "Awesome!", "2025-01-01T00:00:00", artist.getId(), "4", "ARTIST"});

        List<Review> reviews = db.getReviewsByUser(user1);
        List<String> contents = new ArrayList<>();
        for (Review review : reviews) {
            contents.add(review.getText());
        }
        assertEquals(1, reviews.size());
        assertTrue(contents.contains("Nice!"));
        assertFalse(contents.contains("Awesome!"));
    }

    @Test
    public void testGetSongsFromArtistThrowsOnNull() {
        assertThrows(IllegalArgumentException.class, () -> db.getSongsFromArtist(null));
    }

    @Test
    public void testGetSongsFromArtistThrowsOnEmpty() {
        assertThrows(IllegalArgumentException.class, () -> db.getSongsFromArtist(""));
    }

    @Test
    public void testGetAlbumsFromArtistThrowsOnNull() {
        assertThrows(IllegalArgumentException.class, () -> db.getAlbumsFromArtist(null));
    }

    @Test
    public void testGetAlbumsFromArtistThrowsOnEmpty() {
        assertThrows(IllegalArgumentException.class, () -> db.getAlbumsFromArtist(""));
    }

    @Test
    public void testGetSongsFromAlbumThrowsOnNull() {
        assertThrows(IllegalArgumentException.class, () -> db.getSongsFromAlbum(null));
    }

    @Test
    public void testGetSongsFromAlbumThrowsOnEmpty() {
        assertThrows(IllegalArgumentException.class, () -> db.getSongsFromAlbum(""));
    }

    @Test
    public void testSearchReviewablesEmptyQuery() {
        List<Reviewable> results = db.searchReviewables("");
        assertTrue(results.isEmpty());
    }

    @Test
    public void testGetReviewLikesThrowsOnNull() {
        assertThrows(IllegalArgumentException.class, () -> db.getReviewLikes(null));
    }

    @Test
    public void testGetReviewLikesThrowsOnEmpty() {
        assertThrows(IllegalArgumentException.class, () -> db.getReviewLikes(""));
    }

    @Test
    public void testGetReviewRatingReturnsZeroIfNoReviews() throws IOException {
        Reviewable dummy = new Artist("0", "Test", "img");
        assertEquals(0, db.getReviewRating(dummy));
    }

    @Test
    public void testGetReviewRatingThrowsOnNull() {
        assertThrows(IllegalArgumentException.class, () -> db.getReviewRating(null));
    }

    @Test
    public void testGetReviewsByUserThrowsOnNull() {
        assertThrows(IllegalArgumentException.class, () -> db.getReviewsByUser(null));
    }

    @Test
    public void testGetReviewsByUserThrowsOnEmptyId() {
        User user = new User("", "alice", "alice@example.com");
        assertThrows(IllegalArgumentException.class, () -> db.getReviewsByUser(user));
    }

    @Test
    public void testGetReviewableTable() {
        assertEquals(db.getReviewableTable(ReviewableType.SONG),
            db.getReviewableTable(ReviewableType.SONG));
        assertEquals(db.getReviewableTable(ReviewableType.ARTIST),
            db.getReviewableTable(ReviewableType.ARTIST));
        assertEquals(db.getReviewableTable(ReviewableType.ALBUM),
            db.getReviewableTable(ReviewableType.ALBUM));
        assertThrows(IllegalArgumentException.class, () -> db.getReviewableTable(null));
    }    
    
    @Test
    public void testAccessors() {
        assertEquals(db.getPasswordsTable(), db.getPasswordsTable());
        assertEquals(db.getUsersTable(), db.getUsersTable());
        assertEquals(db.getFollowsTable(), db.getFollowsTable());
        assertEquals(db.getLikesTable(), db.getLikesTable());
        assertEquals(db.getReviewsTable(), db.getReviewsTable());
        assertEquals(db.getVectorsTable(), db.getVectorsTable());
    }

    /**
     * Cleans the specified directory by deleting all files and subdirectories.
     *
     * @param dir the directory to clean
     * @throws IOException if an I/O error occurs
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
}
