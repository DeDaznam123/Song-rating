package com.mycompany.irr00_group_project.services;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.mycompany.irr00_group_project.models.reviewables.Reviewable;

/**
 * Thread manager for the expensive Recommendation by comments TF-IDF algorithm.
 */
public class CustomThread extends Thread {
    /**
     * Executes recommendation by comments on a batch.
     * @param batch takes in a batch of reviewable objects
     */
    public static void execute(Reviewable[] batch) {
        ExecutorService executor = Executors.newFixedThreadPool(batch.length);
        for (Reviewable item : batch) {
            executor.submit(() -> {
                try {
                    item.updateTfMap();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
        executor.shutdown();
        try {
            // Wait up to 1 minute for all tasks to finish
            if (!executor.awaitTermination(1, TimeUnit.MINUTES)) {
                System.out.println("Timeout reached before all tasks completed.");
            } 
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt(); // Preserve the interrupt status
        }
    }
}