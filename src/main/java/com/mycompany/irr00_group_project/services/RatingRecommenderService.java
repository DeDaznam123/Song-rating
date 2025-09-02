package com.mycompany.irr00_group_project.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mycompany.irr00_group_project.models.reviewables.Reviewable;
import com.mycompany.irr00_group_project.models.reviewables.ReviewableType;

/**
 *  This class provides a service for recommending songs based on user ratings.
 */
public class RatingRecommenderService {

    static final int RATING_THRESHOLD = 5;
    static int numRatings;

    /**
     * The rating matrix where each row represents
     * a user and each column represents a reviewable item.
     * The value at [i][j] is the rating given by user i to reviewable item j.
     */
    private float[][] ratingMatrix;
    private List<String> users;

    private Database database;
    private Table reviewTable;
    private Table songTable;

    private int numSongs;
    private int numAlbums;
    private int numArtists;

    /**
     * Constructor for the RatingRecommenderService.
     * Initializes the rating matrix and retrieves user and song data from the database.
     *
     * @throws IllegalArgumentException if there is an issue with the database connection.
     * @throws IOException if there is an error reading from the database.
     */
    public RatingRecommenderService() throws IllegalArgumentException, IOException {
        numRatings = 0;
        database = Database.getInstance();
        reviewTable = database.getReviewsTable();
        songTable = database.getReviewableTable(ReviewableType.SONG);
        users = database.getUsersTable().getEverythingInHeader("id");

        constructRatingMatrix();
        // printRatingMatrix();
    }

    /**
     * Gets song recommendations for a user based on their ratings.
     *
     * @param userID the ID of the user for whom to get recommendations.
     * @param numRecommendations the number of recommendations to return.
     * @return an array of Reviewable objects representing the recommended songs.
     * @throws IOException if there is an error reading from the database.
     */
    public List<Reviewable> getRecommendations(String userID, int numRecommendations)
        throws IOException {
        constructRatingMatrix();
        int numUsers = users.size();
        int numReviewables = ratingMatrix[0].length;
        float[] similarityScores = userSimilarity(userID);
        int targetUser = Integer.parseInt(userID);

        // Clamp the number of requested recommendations to the max number of songs
        numRecommendations = Math.min(numRecommendations, numReviewables);

        float[] recommendations = calculateRecommendations(
            numUsers, numReviewables, similarityScores, targetUser);

        List<Reviewable> recommendedObjects = buildRecommendedObjectsList(
            recommendations, numRecommendations);

        return recommendedObjects;
    }

    /**
     * Helper method to calculate recommendation scores for unrated items.
     * @param numUsers the total number of users.
     * @param numReviewables the total number of reviewable items.
     * @param similarityScores the array of similarity scores for each user.
     * @param targetUser the index of the target user for whom recommendations are being calculated.
     * @return an array of recommendation scores for each reviewable item.
     */
    private float[] calculateRecommendations(int numUsers,
        int numReviewables, float[] similarityScores, int targetUser) {
        float[] recommendations = new float[numReviewables];
        for (int j = 0; j < numReviewables; j++) {
            if (ratingMatrix[targetUser][j] == 0) {
                double total = 0;
                double simSum = 0;
                for (int i = 0; i < numUsers; i++) {
                    if (i != targetUser && ratingMatrix[i][j] > 0) {
                        total += similarityScores[i] * ratingMatrix[i][j];
                        simSum += Math.abs(similarityScores[i]);
                    }
                }
                recommendations[j] = (float) (simSum > 0 ? total / simSum : 0);
            }
        }
        return recommendations;
    }

    /**
     * Helper method to build the list of recommended Reviewable objects.
     * @param recommendations the array of recommendation scores.
     * @param numRecommendations the number of recommendations to return.
     * @return a list of Reviewable objects representing the recommended items.
     * @throws IOException if there is an error reading from the database.
     */
    private List<Reviewable> buildRecommendedObjectsList(
        float[] recommendations, int numRecommendations) throws IOException {
        List<float[]> indexedList = new ArrayList<>();
        for (int i = 0; i < recommendations.length; i++) {
            indexedList.add(new float[]{i + 1, recommendations[i]});
        }
        indexedList.sort((a, b) -> Float.compare(b[1], a[1]));

        List<Reviewable> recommendedObjects = new ArrayList<>();
        int limit = Math.min(numRecommendations, indexedList.size());
        for (int i = 0; i < limit; i++) {
            ReviewableType type = ReviewableType.SONG;
            int currIndex = (int) indexedList.get(i)[0];
            int offset = 0;
            if (currIndex > numSongs) {
                if (currIndex > numSongs + numAlbums) {
                    type = ReviewableType.ARTIST;
                    offset = numSongs + numAlbums;
                } else {
                    type = ReviewableType.ALBUM;
                    offset = numSongs;
                }
            }
            recommendedObjects.add(database.turnLineToReviewable(
                database.getReviewableTable(type).getLines(
                    "id",
                    Integer.toString((int) currIndex - offset)).get(0)
            ));
        }
        return recommendedObjects;
    }

    /**
     *  Sorts an array of floats in ascending order using insertion sort.
     *  This method is used to sort the similarity scores of users.
     * @param initial the array of floats to be sorted.
     * @return the sorted array of floats.
     */
    public float[] sortArray(float[] initial) {
        for (int i = 1; i < initial.length; i++) {
            float curr = initial[i];
            int j = i - 1;
            while (j > 0 && initial[j] > curr) {
                initial[j + 1] = initial[j];
                j = j - 1;
            }
            initial[j + 1] = curr;
        }

        return initial;
    }

    /**
     * Calculates the similarity scores between a user and all other users.
     * The similarity is calculated using the cosine similarity formula.
     *
     * @param userID the ID of the user for whom to calculate similarity scores.
     * @return an array of similarity scores for each user.
     */
    private float[] userSimilarity(String userID) {
        float[] res = new float[users.size()];
        float[] a = ratingMatrix[Integer.parseInt(userID)];
        for (int i = 0; i < users.size(); i++) {
            if (userID != users.get(i)) {
                float dot = 0;
                float normA = 0;
                float normB = 0;
                float[] b = ratingMatrix[Integer.parseInt(users.get(i))];
                for (int j = 0; j < a.length; j++) {
                    if (a[j] != 0 && b[j] != 0) {
                        dot += a[j] * b[j];
                        normA += a[j] * a[j];
                        normB += b[j] * b[j];
                    }

                }
                try {
                    // Compute cosine similarity
                    double similarity = 0;
                    if (normA != 0 && normB != 0) {
                        similarity = dot / (Math.sqrt(normA) * Math.sqrt(normB));
                    }

                    // Check if the current user is followed by the given userID
                    boolean isFollowed = database.getFollowedUsers(userID)
                        .contains(database.getUserById(users.get(i)));

                    // Apply boost if followed
                    float boost = isFollowed ? 1.25f : 1.0f;

                    // Store the result
                    res[i] = (float) similarity * boost;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return res;
    }

    /**
     * Constructs the rating matrix from the database.
     * This method retrieves all ratings from the database and populates the rating matrix.
     *
     * @throws IOException if there is an error reading from the database.
     */
    private void constructRatingMatrix() throws IOException {
        numSongs = songTable.getEverythingInHeader("id").size();
        numAlbums = database.getReviewableTable(ReviewableType.ALBUM)
            .getEverythingInHeader("id").size();
        numArtists = database.getReviewableTable(ReviewableType.ARTIST)
            .getEverythingInHeader("id").size();

        int numReviewables = numSongs + numAlbums + numArtists;

        ratingMatrix = new float[users.size()][numReviewables];

        List<Map<String, String>> ratingLines = reviewTable.getAllLines();

        for (int i = 0; i < ratingLines.size() - 1; i++) {

            int userId = Integer.parseInt(ratingLines.get(i).get("user_id"));
            int targetId = Integer.parseInt(ratingLines.get(i).get("target_id")) - 1;
            int rating = Integer.parseInt(ratingLines.get(i).get("rating"));
            int offset = 0;

            if (ratingLines.get(i).get("reviewable_type").equals("ALBUM")) {
                offset = numSongs;
            }
            if (ratingLines.get(i).get("reviewable_type").equals("ARTIST")) {
                offset = numSongs + numAlbums;
            }

            if (ratingMatrix[userId][targetId + offset] == 0) {
                ratingMatrix[userId][targetId + offset] = rating;
            }
        }
    }

    /**
     * Prints the rating matrix to the console.
     * This method is used for debugging purposes
     * to visualize the current state of the rating matrix.
     */
    public void printRatingMatrix() {
        String output = "";
        for (int i = 0; i < ratingMatrix.length; i++) {
            for (int j = 0; j < ratingMatrix[i].length; j++) {
                output += ratingMatrix[i][j] + " ";
            }
            output += "\n";
        }
        System.out.println(output);
    }
}
