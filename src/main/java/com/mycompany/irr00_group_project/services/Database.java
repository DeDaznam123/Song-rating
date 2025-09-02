package com.mycompany.irr00_group_project.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import com.mycompany.irr00_group_project.controllers.managers.SessionManager;
import com.mycompany.irr00_group_project.models.User;
import com.mycompany.irr00_group_project.models.reviewables.Review;
import com.mycompany.irr00_group_project.models.reviewables.Reviewable;
import com.mycompany.irr00_group_project.models.reviewables.ReviewableType;
import com.mycompany.irr00_group_project.models.reviewables.targets.Album;
import com.mycompany.irr00_group_project.models.reviewables.targets.Artist;
import com.mycompany.irr00_group_project.models.reviewables.targets.Song;

/**
 * The Database class represents a mock database containing various tables.
 * Each table is initialized with specific headers and file paths.
 * The files are expected to be in the "src/main/resources/db/" directory.
 */
public class Database {

    private static Database instance;

    private Table passwords;
    private Table users;
    private Table follows;
    private Table likes;
    private Table reviews;
    private Table songs;
    private Table artists;
    private Table albums;
    private Table tfidfVectors;

    /**
     * Private constructor to prevent direct instantiation.
     * Initializes the tables with their respective headers and file paths.
     * The files are expected to be in the "src/main/resources/db/" directory.
     * 
     * @throws Exception if there is an error initializing any of the tables.
     */
    private Database() {
        try {
            this.passwords = new Table(new String[]{"id", "user_id", "password", "salt"}, 
                "src/main/resources/db/passwords.csv");
            this.users = new Table(new String[]{"id", "username", "email", "last_login"}, 
                "src/main/resources/db/users.csv");
            this.follows = new Table(new String[]{"id", "follower_id", "followed_id"}, 
                "src/main/resources/db/follows.csv");
            this.likes = new Table(new String[]{"id", "user_id", "post_id"}, 
                "src/main/resources/db/likes.csv");
            this.reviews = new Table(new String[]{"id", "user_id", "content", "created_at", 
                "target_id", "rating", "reviewable_type"}, "src/main/resources/db/reviews.csv");
            this.songs = new Table(new String[]{"id", "title", "artist_id", "album_id", "image"}, 
                "src/main/resources/db/songs.csv");
            this.artists = new Table(new String[]{"id", "name", "image"}, 
                "src/main/resources/db/artists.csv");
            this.albums = new Table(new String[]{"id", "title", "artist_id", "image"}, 
                "src/main/resources/db/albums.csv");
            
            
        } catch (Exception e) {
            throw new RuntimeException("Error initializing database tables: " + e.getMessage(), e);
        }
    }

    /**
     * Constructor for the Database class used to test it.
     * @param path The path to the database directory where the CSV files are located.
     */
    public Database(String path) {
        try {
            this.passwords = new Table(new String[]{"id", "user_id", "password", "salt"}, 
                path + "/passwords.csv");
            this.users = new Table(new String[]{"id", "username", "email", "last_login"}, 
                path + "/users.csv");
            this.follows = new Table(new String[]{"id", "follower_id", "followed_id"}, 
                path + "/follows.csv");
            this.likes = new Table(new String[]{"id", "user_id", "post_id"}, 
                path + "/likes.csv");
            this.reviews = new Table(new String[]{"id", "user_id", "content", "created_at", 
                "target_id", "rating", "reviewable_type"}, path + "/reviews.csv");
            this.songs = new Table(new String[]{"id", "title", "artist_id", "album_id", "image"}, 
                path + "/songs.csv");
            this.artists = new Table(new String[]{"id", "name", "image"}, 
                path + "/artists.csv");
            this.albums = new Table(new String[]{"id", "title", "artist_id", "image"}, 
                path + "/albums.csv");
        } catch (Exception e) {
            throw new RuntimeException("Error initializing database tables: "
                + e.getMessage(), e);
        }
        instance = this;
    }

    ////////////////////////////////// USERS ///////////////////////////////
    /**
     * Converts a line from the users table into a User object.
     * 
     * @param line A map representing a line from the users table.
     * @return A User object created from the provided line.
     */
    private User turnLineToUser(Map<String, String> line) {
        return new User(
            line.get("id"),
            line.get("username"), 
            line.get("email")
        );
    }

    /**
     * Returns the path to the database directory.
     * 
     * @param username The username of the user to authenticate.
     * @param password The password of the user to authenticate.
     * @return The path to the database directory.
     * @throws IOException thrown if there is an error accessing the database directory.
     * @throws IllegalArgumentException thrown if the database directory does not exist.
     */
    public User authenticate(String username, String password) 
        throws IllegalArgumentException, IOException {
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Username and password must not be null or empty.");
        }
        List<Map<String, String>> user = users.getLines("username", username);
        if (user == null || user.isEmpty()) {
            return null;
        }
        List<Map<String, String>> p = passwords.getLines("user_id", user.get(0).get("id"));
        byte[] salt = Base64.getDecoder().decode(p.get(0).get("salt"));
        String passwordHash = HashPasword.hashPassword(password, salt);
        if (p == null || p.isEmpty() || !p.get(0).get("password").equals(passwordHash)) {
            return null; 
        }
        users.changeLine(user.get(0).get("id"), 
            new String[] {
                user.get(0).get("username"), 
                user.get(0).get("email"),
                java.time.LocalDateTime.now().toString()
            });
        return turnLineToUser(user.get(0));
    }

    /**
     * Returns a list of users that the specified user follows.
     * 
     * @param id The ID of the user whose followed users are to be retrieved.
     * @return An ArrayList of User objects representing the followed users.
     * @throws IllegalArgumentException if the id is null or empty.
     * @throws IOException if there is an error reading the follows or users table.
     */
    public ArrayList<User> getFollowedUsers(String id) 
        throws IllegalArgumentException, IOException {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("User ID must not be null or empty.");
        }
        ArrayList<User> followedUsers = new ArrayList<>();
        List<Map<String, String>> follows = this.follows.getLines("follower_id", id);

        for (Map<String, String> follow : follows) {
            String followedId = follow.get("followed_id");
            List<Map<String, String>> user = this.users.getLines("id", followedId);
            if (user != null && !user.isEmpty()) {
                followedUsers.add(turnLineToUser(user.get(0)));
            }
        }
        return followedUsers;
    }

    /**
     * Adds a new user to the database.
     * 
     * @param user The User object containing the user's details.
     * @param password The hashed password for the new user.
     * @param salt The salt used for hashing the password.
     * @throws IllegalArgumentException if the username already exists.
     */
    public void addUser(User user, String password, String salt) 
        throws IllegalArgumentException, IOException {
        if (user == null || password == null || password.isEmpty()) {
            throw new IllegalArgumentException("User and password must not be null or empty.");
        }
        if (salt == null || salt.isEmpty()) {
            throw new IllegalArgumentException("Salt must not be null or empty.");
        }

        // Add the user to the users table
        String userId = users.getNextId() + "";
        users.addLine(
            new String[] {
                user.getUsername(), 
                user.getEmail(),
                java.time.LocalDateTime.now().toString(), 
            }
        );

        // Add the password to the passwords table
        passwords.addLine(new String[]{userId, password, salt});
    }

    /**
     * Retrieves a User object by its ID.
     * @param userId The ID of the user to retrieve.
     * @return A User object if found, or null if no user with the given ID exists.
     * @throws IllegalArgumentException if the userId is null or empty.
     * @throws IOException if there is an error reading the users table.
     */
    public User getUserById(String userId) 
        throws IllegalArgumentException, IOException {
        if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("User ID must not be null or empty.");
        }
        List<Map<String, String>> userLines = this.users.getLines("id", userId);
        if (userLines.isEmpty()) {
            return null; // User not found
        }
        return turnLineToUser(userLines.get(0));
    }

    /**
     *  Searches for users by their username.
     * @param username The username of the user to search for.
     * @return A list of User objects that match the username.
     * @throws IllegalArgumentException if the username is null or empty.
     * @throws IOException if there is an error reading the users table.
     */
    public List<User> searchUsers(String username) 
        throws IllegalArgumentException, IOException {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username must not be null or empty.");
        }
        List<Map<String, String>> userLines = this.users.getLinesQuery("username", username);
        List<User> users = new ArrayList<>();
        for (Map<String, String> line : userLines) {
            users.add(turnLineToUser(line));
        }
        return users; 
    }

    ////////////////////////////////// SONGS, ARTISTS, ALBUMS ///////////////////////////////
     
    /**
     * Converts a line from the songs, albums, or artists table into a Reviewable object.
     * @param line A map representing a line from the songs, albums, or artists table.
     * @return A Reviewable object created from the provided line.
     */
    public Reviewable turnLineToReviewable(Map<String, String> line) {
        ReviewableType type;
        if (line.containsKey("album_id")) {
            type = ReviewableType.SONG;
        } else if (line.containsKey("artist_id")) {
            type = ReviewableType.ALBUM;
        } else {
            type = ReviewableType.ARTIST;
        }
        switch (type) {
            case SONG -> {
                try {
                    return turnLineToSong(line);
                } catch (IOException e) {
                    // Handle the exception, possibly log it or rethrow it.
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    // Handle the case where the line is invalid.
                    e.printStackTrace();
                }
            }
            case ARTIST -> {
                return turnLineToArtist(line);
            }
            case ALBUM -> {
                try {
                    return turnLineToAlbum(line);
                } catch (IOException e) {
                    // Handle the exception, possibly log it or rethrow it.
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    // Handle the case where the line is invalid.
                    e.printStackTrace();
                }
            }
            default -> throw new AssertionError();
        }
        return new Artist("problem", "", null);
    }

    /**
     * Converts a line from the songs table into a Song object.
     * 
     * @param line A map representing a line from the songs table.
     * @return A Song object created from the provided line.
     */
    private Artist turnLineToArtist(Map<String, String> line) {
        return new Artist(
            line.get("id"),
            line.get("name"),
            line.get("image")
        );
    }

    /**
     * Converts a line from the albums table into an Album object.
     * 
     * @param line A map representing a line from the albums table.
     * @return An Album object created from the provided line.
     * @throws IllegalArgumentException if the line is null or does not contain required fields.
     * @throws IOException if there is an error reading the artists table.
     */
    private Album turnLineToAlbum(Map<String, String> line) 
        throws IllegalArgumentException, IOException {
        List<Map<String, String>> artistLines = artists.getLines("id", line.get("artist_id"));
        if (artistLines.isEmpty()) {
            throw new IllegalArgumentException("Artist ID is invalid or does not exist.");
        }
        return new Album(
            line.get("id"),
            line.get("title"), 
            line.get("image"),
            turnLineToArtist(artistLines.get(0))
        );
    }

    /**
     * Converts a line from the songs table into a Song object.
     * 
     * @param line A map representing a line from the songs table.
     * @return A Song object created from the provided line.
     * @throws IllegalArgumentException if the line is null or does not contain required fields.
     * @throws IOException if there is an error reading the albums or artists table.
     */
    private Song turnLineToSong(Map<String, String> line) 
        throws IllegalArgumentException, IOException {
        // Get the artist for the song.
        List<Map<String, String>> artistLines = artists.getLines("id", line.get("artist_id"));
        if (artistLines.isEmpty()) {
            throw new IllegalArgumentException("Artist ID is invalid or does not exist.");
        }
        // If the album_id is not null or empty, retrieve the album.
        Album album = null;
        if (line.get("album_id") != null && !line.get("album_id").isEmpty()) {
            List<Map<String, String>> albumLines = albums.getLines("id", line.get("album_id"));
            if (albumLines.isEmpty()) {
                throw new IllegalArgumentException("Album ID is invalid or does not exist.");
            }
            album = turnLineToAlbum(albumLines.get(0));
        }
        return new Song(
            line.get("id"),
            line.get("title"), 
            line.get("image"), 
            turnLineToArtist(artistLines.get(0)), 
            album
        );
    }

    /**
     * CRetusns a list of songs from a specific artist.
     * 
     * @param artistId The artist ID for which songs are to be retrieved.
     * @return A list of Song objects associated with the specified artist.
     * @throws IllegalArgumentException if the artistId is null or empty.
     * @throws IOException if there is an error reading the songs table.
     * 
     */
    public List<Song> getSongsFromArtist(String artistId) 
        throws IllegalArgumentException, IOException {
        if (artistId == null || artistId.isEmpty()) {
            throw new IllegalArgumentException("Artist ID must not be null or empty.");
        }
        List<Map<String, String>> songLines = this.songs.getLines("artist_id", artistId);
        List<Song> songs = new ArrayList<>();
        for (Map<String, String> line : songLines) {
            songs.add(turnLineToSong(line));
        }
        return songs;
    }

    /**
     * Returns a list of albums from a specific artist.
     * 
     * @param artistId The artist ID for which albums are to be retrieved.
     * @return A list of Album objects associated with the specified artist.
     * @throws IllegalArgumentException if the artistId is null or empty.
     * @throws IOException if there is an error reading the albums table.
     */
    public List<Album> getAlbumsFromArtist(String artistId) 
        throws IllegalArgumentException, IOException {
        if (artistId == null || artistId.isEmpty()) {
            throw new IllegalArgumentException("Artist ID must not be null or empty.");
        }
        List<Map<String, String>> albumLines = this.albums.getLines("artist_id", artistId);
        List<Album> albums = new ArrayList<>();
        for (Map<String, String> line : albumLines) {
            albums.add(turnLineToAlbum(line));
        }
        return albums;
    }

    /**
     * Returns a list of Song objects from a specific album.
     * @param albumId The ID of the album for which songs are to be retrieved.
     * @return A list of all Song objects associated with the specified album.
     * @throws IllegalArgumentException if the albumId is null or empty.
     * @throws IOException if there is an error reading the songs table.
     */
    public List<Song> getSongsFromAlbum(String albumId) 
        throws IllegalArgumentException, IOException {
        if (albumId == null || albumId.isEmpty()) {
            throw new IllegalArgumentException("Artist ID must not be null or empty.");
        }
        List<Map<String, String>> songLines = this.songs.getLines("album_id", albumId);
        List<Song> songs = new ArrayList<>();
        for (Map<String, String> line : songLines) {
            songs.add(turnLineToSong(line));
        }
        return songs;
    }

    /**
     * Searches for reviewable items (songs, albums, artists) based on a query string.
     * @param query The search query string to match against titles and names.
     * @return A list of Reviewable objects that match the search criteria.
     */
    public List<Reviewable> searchReviewables(String query) {
        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>(); // Return an empty list if the query is null or empty.
        }
        List<Reviewable> results = new ArrayList<>();
        try {
            // Search songs
            List<Map<String, String>> songLines = this.songs.getLinesQuery("title", query);
            for (Map<String, String> line : songLines) {
                results.add(turnLineToSong(line));
            }
            // Search albums
            List<Map<String, String>> albumLines = this.albums.getLinesQuery("title", query);
            for (Map<String, String> line : albumLines) {
                results.add(turnLineToAlbum(line));
            }
            // Search artists
            List<Map<String, String>> artistLines = this.artists.getLinesQuery("name", query);
            for (Map<String, String> line : artistLines) {
                results.add(turnLineToArtist(line));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return results;       
    }

    ////////////////////////////////// REVIEWS ///////////////////////////////

    /**
     * Converts a line from the reviews table into a Comment object.
     * 
     * @param line A map representing a line from the reviews table.
     * @return A Comment object created from the provided line.
     * @throws IllegalArgumentException if the line is null or does not contain required fields.
     * @throws IOException if there is an error reading the users table.
     */
    public Review turnLineToReview(Map<String, String> line) 
        throws IllegalArgumentException, IOException {
        // Get the number of likes for the post.
        int likes = this.getReviewLikes(line.get("id")).size();
        // Get the user who made the comment.
        User user = turnLineToUser(users.getLines("id", line.get("user_id")).get(0));
        Reviewable reviewable = null; // This should be set based on the reviewable_type.
        if (line.get("reviewable_type").equals("SONG")) {
            reviewable = turnLineToSong(songs.getLines("id", line.get("target_id")).get(0));
        } else if (line.get("reviewable_type").equals("ALBUM")) {
            reviewable = turnLineToAlbum(albums.getLines("id", line.get("target_id")).get(0));
        } else if (line.get("reviewable_type").equals("ARTIST")) {
            reviewable = turnLineToArtist(artists.getLines("id", line.get("target_id")).get(0));
        }
        return new Review(
            line.get("id"),
            user,
            line.get("content"),
            likes,
            line.get("created_at"),
            Integer.parseInt(line.get("rating")),
            reviewable
        );
    }

    /**
     * Returns a list of users that follow the specified user.
     * 
     * @param reviewId The ID of the post .
     * @return count of likes for the post.
     * @throws IllegalArgumentException if the postId is null or empty.
     * @throws IOException if there is an error reading the likes table.
     */
    public List<Map<String, String>> getReviewLikes(String reviewId) 
        throws IllegalArgumentException, IOException {    
        if (reviewId == null || reviewId.isEmpty()) {
            throw new IllegalArgumentException("Review ID must not be null or empty.");
        }
        return this.likes.getLines("post_id", reviewId);
    }

    /**
     * Sorts the reviews by the number of likes, placing the current user's review at the top.
     * 
     * @param reviews The list of reviews to be sorted.
     * @return A sorted list of reviews with the current user's review at the top.
     */
    private List<Review> sortReviewsByLikes(List<Review> reviews) {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null) {
            reviews.sort((r1, r2) -> Integer.compare(r2.getLikes(), r1.getLikes()));
            return reviews;
        }
        reviews.sort((r1, r2) -> Integer.compare(r2.getLikes(), r1.getLikes()));
        reviews.sort((r1, r2) -> {
            boolean r1IsCurrentUser = r1.getUser().getId().equals(currentUser.getId());
            boolean r2IsCurrentUser = r2.getUser().getId().equals(currentUser.getId());
            if (r1IsCurrentUser && !r2IsCurrentUser) {
                return -1;
            }
            if (!r1IsCurrentUser && r2IsCurrentUser) { 
                return 1;
            }
            return 0;
        });
        return reviews;
    }

    /**
     * Returns a list of reviews for a specific reviewable target.
     * 
     * @param reviewable The reviewable target (e.g., Song) for which reviews are to be retrieved.
     * @return A list of Review objects associated with the specified target.
     * @throws IllegalArgumentException if the reviewable is null.
     * @throws IOException if there is an error reading the reviews table.
     */
    public List<Review> getReviews(Reviewable reviewable) 
        throws IllegalArgumentException, IOException {
        List<Map<String, String>> reviewLines = 
            this.reviews.getLines("target_id", reviewable.getId());
        List<Review> reviews = new ArrayList<>();
        for (Map<String, String> line : reviewLines) {
            if (line.get("reviewable_type") == null 
                || !line.get("reviewable_type").equals(reviewable.getType().toString())) {
                continue;
            }
            reviews.add(turnLineToReview(line));
        }

        return sortReviewsByLikes(reviews);
    }

    /**
     * Calculates the average rating for a reviewable target based on its reviews.
     * @param reviewable The reviewable target for which the average rating is to be calculated.
     * @throws IllegalArgumentException if the review is null or does not contain required fields.
     * @throws IOException if there is an error reading the reviews table.
     */
    public int getReviewRating(Reviewable reviewable) 
        throws IllegalArgumentException, IOException {
        if (reviewable == null) {
            throw new IllegalArgumentException("Reviewable must not be null.");
        }
        List<Review> reviews = getReviews(reviewable);
        if (reviews.isEmpty()) {
            return 0; 
        }
        int totalRating = 0;
        for (Review review : reviews) {
            totalRating += review.getRating();
        }
        return totalRating / reviews.size();
    }

    /**
     * Returns a list of reviews made by a specific user.
     * @param user The user whose reviews are to be retrieved.
     * @return A list of Review objects made by the specified user.
     * @throws IllegalArgumentException if the user is null or has an empty ID.
     * @throws IOException if there is an error reading the reviews table.
     */
    public List<Review> getReviewsByUser(User user) 
        throws IllegalArgumentException, IOException {
        if (user == null || user.getId() == null || user.getId().isEmpty()) {
            throw new IllegalArgumentException("User must not be null or have an empty ID.");
        }
        List<Map<String, String>> reviewLines = this.reviews.getLines("user_id", user.getId());
        List<Review> reviews = new ArrayList<>();
        for (Map<String, String> line : reviewLines) {
            reviews.add(turnLineToReview(line));
        }
        return sortReviewsByLikes(reviews);
    }

    ////////////////////////////////////// SINGLETON ///////////////////////////////

    /**
     * Returns the singleton instance of the Database.
     * 
     * @return The singleton Database instance.
     * @throws RuntimeException if there is an error initializing the database.
     */
    public static synchronized Database getInstance() {
        if (instance == null) {
            try {
                instance = new Database();
            } catch (Exception e) {
                throw new RuntimeException(
                    "Failed to initialize Database singleton: " + e.getMessage(), e);
            }
        }
        return instance;
    }

    ///////////////////////////////// ACCESSORS ///////////////////////////////
    /// 
    /**
     * Returns the table corresponding to the specified reviewable type.
     * @param reviewableType The type of reviewable (SONG, ARTIST, ALBUM).
     * @return The table corresponding to the specified reviewable type.
     * @throws IllegalArgumentException if the reviewableType is null.
     */
    public Table getReviewableTable(ReviewableType reviewableType) {
        if (reviewableType == null) {
            throw new IllegalArgumentException("Reviewable type must not be null.");
        }
        switch (reviewableType) {
            case SONG -> {
                return songs;
            }
            case ARTIST -> {
                return  artists;
            }
            case ALBUM -> {
                return albums;
            }
            default -> {
                throw new IllegalArgumentException("Invalid reviewable type: " + reviewableType);
            }
        }
    }

    public Table getPasswordsTable() {
        return passwords;
    }

    public Table getUsersTable() {
        return users;
    }

    public Table getFollowsTable() {
        return follows;
    }

    public Table getLikesTable() {
        return likes;
    }

    public Table getReviewsTable() {
        return reviews;
    }

    public Table getVectorsTable() {
        return tfidfVectors;
    }

}