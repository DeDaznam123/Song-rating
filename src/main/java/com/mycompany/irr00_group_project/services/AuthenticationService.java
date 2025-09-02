package com.mycompany.irr00_group_project.services;

import java.io.IOException;
import java.util.Base64;

import com.mycompany.irr00_group_project.models.User;

/**
 * The AuthenticationService class is responsible for handling user
 * authentication
 * within the application.
 */
public class AuthenticationService {

    /**
     * Authenticates a user with the given username and password.
     *
     * @param username The username of the user.
     * @param password The password of the user.
     * @return User object if authentication is successful, false otherwise.
     */
    public static User login(String username, String password) {
        // Handle null or empty credentials up front:
        if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
            return null;
        }
        // use real DB logic:
        try {
            return Database.getInstance().authenticate(username, password);
        } catch (Exception e) {
            System.err.println("Error during authentication: " + e.getMessage());
            return null;
        }
    }

    /**
     * Validates the password according to specified criteria.
     *
     * @param password The password to validate.
     * @return true if the password is valid, false otherwise.
     *         This method checks that the password is at least 8 characters long,
     *         contains at least one uppercase letter, one lowercase letter, one
     *         digit,
     *         and one special character. The special characters considered are:
     *         !@#$%^&*()_+-=[]{};':"\\|,.<>/?.
     *         It also checks that the password is not null.
     *         The password must meet both conditions to be considered valid.
     */
    public static boolean validatePassword(String password) {
        if (password == null) {
            return false;
        }
        boolean condition1 = password.length() >= 8
                && password.matches(".*[A-Z].*");
        boolean condition2 = password.matches(".*[a-z].*")
                && password.matches(".*[0-9].*") && password.matches(
                        ".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*");
        return condition1 && condition2;
    }

    /**
     * Validates the email format.
     *
     * @param email The email to validate.
     * @return true if the email is valid, false otherwise.
     *         This method uses a simple regex to check the email format.
     *         It checks for the presence of an '@' symbol, a domain name,
     *         and a top-level domain (TLD) with at least two characters.
     */
    public static boolean validateEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        // Simple regex for email validation
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(emailRegex);
    }

    /**
     * Signs up a new user with the given details.
     *
     * @param user     The User object containing the user's details.
     * @param password The password for the new user.
     * @throws IllegalArgumentException if the username is already taken or if
     * the password does not meet the criteria.
     * @throws IOException if there is an error during the sign-up
     * process.
     */
    public static void signUp(User user, String password)
            throws IllegalArgumentException, IOException {

        // Check conditions for sign-up.
        if (Database.getInstance().getUsersTable().getLines("username", user.getUsername())
                .size() > 0) {
            throw new IllegalArgumentException("Username is already taken.");
        }
        if (!validateEmail(user.getEmail())) {
            throw new IllegalArgumentException("Invalid email format.");
        }
        if (Database.getInstance().getUsersTable().getLines("email", user.getEmail())
                .size() > 0) {
            throw new IllegalArgumentException("Exist a user with this email.");
        }
        if (!validatePassword(password)) {
            throw new IllegalArgumentException("Password does not meet the criteria.");
        }

        byte[] salt = HashPasword.generateSalt();
        String hash = HashPasword.hashPassword(password, salt);
        String saltString = Base64.getEncoder().encodeToString(salt);

        // Add the user to the database.
        Database.getInstance().addUser(user, hash, saltString);
    }
}
