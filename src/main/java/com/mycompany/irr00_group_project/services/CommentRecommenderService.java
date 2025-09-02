package com.mycompany.irr00_group_project.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.mycompany.irr00_group_project.models.reviewables.Review;
import com.mycompany.irr00_group_project.models.reviewables.Reviewable;
import com.mycompany.irr00_group_project.models.reviewables.ReviewableType;

/**
 * Service for recommending comments based on user reviews.
 */
public class CommentRecommenderService {

    private static Map<String, Double> idfMap = new HashMap<>();
    private static Set<String> vocabulary = new LinkedHashSet<>();
    private static List<Reviewable> allReviewables = new ArrayList<>();

    private static Database dbInstance = Database.getInstance();

    private static boolean cachesAreReady = false;
    private static String currentUserId;

    private CommentRecommenderService() { }

    public static void setDbInstance(Database db) {
        dbInstance = db;
    }

    public static void setCurrentUserId(String id) {
        currentUserId = id;
    }

    public static void setAllReviewables() throws IOException {
        allReviewables = getAllReviewables();
    }

    /**
     * Invalidates all caches.
     */
    public static synchronized void invalidateCaches() {
        cachesAreReady = false;
        if (idfMap != null) {
            idfMap.clear();
        }
        if (vocabulary != null) {
            vocabulary.clear();
        }
    }

    /**
     * Initializes collections if they are null.
     */
    private static void initializeCollectionsIfNull() {
        if (idfMap == null) {
            idfMap = new HashMap<>();
        }
        if (vocabulary == null) {
            vocabulary = new LinkedHashSet<>();
        }
    }

    /**
     * Prepares the caches for TF-IDF calculations.
     * @throws IOException if there is an error accessing the database.
     */
    private static synchronized void prepareCaches() throws IOException {
        if (cachesAreReady || allReviewables.isEmpty()) {
            return;
        }

        initializeCollectionsIfNull();

        Map<String, Integer> docFreq = new HashMap<>();
        int totalDocs = allReviewables.size();

        for (Reviewable item : allReviewables) {
            Set<String> uniqueWords = new HashSet<>();
            for (Review review : dbInstance.getReviews(item)) {
                String[] words = review.getText().toLowerCase().split("\\W+");
                for (String word : words) {
                    if (!word.isEmpty()) {
                        
                        vocabulary.add(word);
                        uniqueWords.add(word);
                    }
                }
            }
            for (String word : uniqueWords) {
                docFreq.merge(word, 1, Integer::sum);
            }
        }

        for (Map.Entry<String, Integer> entry : docFreq.entrySet()) {
            idfMap.put(entry.getKey(), Math.log((double) totalDocs / entry.getValue()));
        }

        cachesAreReady = true;
    }

    /**
     * Computes the term frequency (TF) map for a given reviewable item.
     * @param item The reviewable item to compute TF for.
     * @return A map of terms and their corresponding TF values.
     */
    public static Map<String, Double> computeTfMap(Reviewable item) {
        try {
            List<Review> reviews = dbInstance.getReviews(item);
            Map<String, Integer> count = new HashMap<>();
            int totalTerms = 0;

            for (Review c : reviews) {
                String[] words = c.getText().toLowerCase().split("\\W+");
                for (String word : words) {
                    if (!word.isEmpty()) {
                        count.merge(word, 1, Integer::sum);
                        totalTerms++;
                    }
                }
            }

            Map<String, Double> tfMap = new HashMap<>();
            if (totalTerms > 0) {
                for (Map.Entry<String, Integer> entry : count.entrySet()) {
                    tfMap.put(entry.getKey(), (double) entry.getValue() / totalTerms);
                }
            }
            return tfMap;

        } catch (IOException e) {
            throw new RuntimeException("Error fetching reviews for item: " + item.getId(), e);
        }
    }

    /**
     * Computes the TF-IDF vector for a given reviewable item.
     * @param item The reviewable item to compute the TF-IDF vector for.
     * @return A double array representing the TF-IDF vector.
     * @throws IOException if there is an error accessing the database.
     */
    public static double[] computeTfIdfVector(Reviewable item) throws IOException {
        prepareCaches();
        Map<String, Double> tfMap = item.getTfMap();
        double[] vector = new double[vocabulary.size()];

        int i = 0;
        for (String word : vocabulary) {
            vector[i++] = tfMap.getOrDefault(word, 0.0) * idfMap.getOrDefault(word, 0.0);
        }
        return vector;
    }

    /**
     * Fetches all reviewable items from the database.
     * @return A list of all reviewable items.
     * @throws IOException if there is an error accessing the database.
     */
    private static List<Reviewable> getAllReviewables() throws IOException {
        List<Reviewable> reviewables = new ArrayList<>();
        for (ReviewableType type : ReviewableType.values()) {
            try {
                reviewables.addAll(getAllItemsInTable(dbInstance.getReviewableTable(type)));
            } catch (IOException e) {
                System.err.println("Error fetching reviewables of type " 
                    + type + ": " + e.getMessage());
            }
        }
        return reviewables;
    }

    /**
     * Fetches all items in a given table and converts them to Reviewable objects.
     * @param table The table to fetch items from.
     * @return A list of Reviewable items.
     * @throws IOException if there is an error accessing the database.
     */
    private static List<Reviewable> getAllItemsInTable(Table table) throws IOException {
        return table.getAllLines().stream()
            .map(dbInstance::turnLineToReviewable)
            .collect(Collectors.toList());
    }

    /**
     * Gets the liked items for the current user based on their reviews.
     * @param allItemsMap A map of all reviewable items keyed by "id::type".
     * @return A list of liked Reviewable items.
     * @throws IOException if there is an error accessing the database.
     */
    private static List<Reviewable> getLikedItems(Map<String, Reviewable> allItemsMap)
        throws IOException {

        List<Reviewable> likedItems = new ArrayList<>();
        List<Map<String, String>> myReviews = 
            dbInstance.getReviewsTable().getLines("user_id", currentUserId);

        for (Map<String, String> reviewData : myReviews) {
            try {
                if (Integer.parseInt(reviewData.get("rating")) >= 3) {
                    String targetId = reviewData.get("target_id");
                    String typeStr = reviewData.get("reviewable_type");

                    if (targetId == null || typeStr == null
                        || targetId.isEmpty() || typeStr.isEmpty()) {

                        System.err.println("Skipping review due to missing data: " + reviewData);
                        continue;
                    }

                    ReviewableType type = ReviewableType.valueOf(typeStr.toUpperCase());
                    String key = targetId + "::" + type;
                    Reviewable item = allItemsMap.get(key);
                    if (item != null) {
                        likedItems.add(item);
                    } else {
                        System.err.println("Missing item in map for key: " + key);
                    }
                }
            } catch (Exception e) {
                System.err.println("Skipping review due to error: " + e.getMessage());
            }
        }

        return likedItems;
    }

    /**
     * Gets recommendations based on the user's liked items.
     * @return A list of recommended Reviewable items.
     * @throws IOException if there is an error accessing the database.
     */
    public static List<Reviewable> getRecommendations() throws IOException {
        prepareCaches();

        Map<String, Reviewable> allItemsMap = allReviewables.stream()
                .collect(Collectors.toMap(
                        item -> item.getId() + "::" + item.getType(),
                        item -> item));

        List<Reviewable> likedItems = getLikedItems(allItemsMap);
        if (likedItems.isEmpty()) {
            return new ArrayList<>();
        }

        Set<String> likedKeys = likedItems.stream()
                .map(item -> item.getId() + "::" + item.getType())
                .collect(Collectors.toSet());

        computeTfIdfForAll(allReviewables);

        double[] avgVector = new double[vocabulary.size()];
        for (Reviewable liked : likedItems) {
            double[] vec = liked.getTfIdfVector();
            for (int i = 0; i < vec.length; i++) {
                avgVector[i] += vec[i];
            }
        }
        for (int i = 0; i < avgVector.length; i++) {
            avgVector[i] /= likedItems.size();
        }

        List<Map.Entry<Reviewable, Double>> scored = new ArrayList<>();
        for (Reviewable item : allReviewables) {
            if (likedKeys.contains(item.getId() + "::" + item.getType())) {
                continue;
            }
            double[] vec = item.getTfIdfVector();
            Double angle = computeAngleBetweenVectors(avgVector, vec);
            if (!angle.isNaN()) {
                scored.add(Map.entry(item, angle));
            }
        }

        scored.sort(Map.Entry.comparingByValue());

        // Clean up memory.
        idfMap = null;
        vocabulary = null;
        dbInstance = null;

        return scored.stream().limit(10).map(Map.Entry::getKey).collect(Collectors.toList());
    }

    /**
     * Computes the TF-IDF for all reviewable items in batches.
     * @param items The list of reviewable items to compute TF-IDF for.
     * @throws IOException if there is an error accessing the database.
     */
    private static void computeTfIdfForAll(List<Reviewable> items) throws IOException {
        int batchSize = 6;
        int total = items.size();
        int fullBatches = total / batchSize;

        for (int i = 0; i < fullBatches; i++) {
            CustomThread.execute(
                items.subList(i * batchSize, i * batchSize + batchSize).toArray(new Reviewable[0])
            );
        }

        if (fullBatches * batchSize < total) {
            CustomThread.execute(
                items.subList(fullBatches * batchSize, total).toArray(new Reviewable[0])
            );
        }
    }

    /**
     * Computes the angle between two vectors using the dot product and magnitudes.
     * @param a The first vector.
     * @param b The second vector.
     * @return The angle in degrees between the two vectors, or NaN if either vector is zero.
     */
    private static Double computeAngleBetweenVectors(double[] a, double[] b) {
        double dot = 0.0;
        double magA = 0.0;
        double magB = 0.0;
        for (int i = 0; i < a.length; i++) {
            dot += a[i] * b[i];
            magA += a[i] * a[i];
            magB += b[i] * b[i];
        }

        if (magA == 0.0 || magB == 0.0) {
            return Double.NaN;
        }

        double cosTheta = dot / (Math.sqrt(magA) * Math.sqrt(magB));
        cosTheta = Math.max(-1.0, Math.min(1.0, cosTheta));
        return Math.toDegrees(Math.acos(cosTheta));
    }
}