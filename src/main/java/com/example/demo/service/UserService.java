package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * User Service Class
 * 
 * This class contains the business logic for User operations. It acts as an intermediary
 * between the Controller and Repository layers, implementing the Service Layer pattern.
 * 
 * Key Concepts:
 * - @Service: Marks this class as a Spring service component
 * - Business Logic: Contains application-specific logic and rules
 * - Data Validation: Ensures data integrity before database operations
 * - Exception Handling: Provides meaningful error messages
 * - Transaction Management: Spring automatically manages transactions
 * 
 * Responsibilities:
 * - Validate input data
 * - Apply business rules
 * - Handle exceptions
 * - Coordinate between different repositories if needed
 * - Provide a clean API for controllers
 */
@Service
public class UserService {

    /**
     * UserRepository dependency injection
     * @Autowired tells Spring to inject the UserRepository bean
     * This is constructor injection, which is the recommended approach
     */
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Create a new user
     * 
     * This method validates the user data and saves it to the database.
     * It also sets the creation and update timestamps.
     * 
     * @param user The user object to create
     * @return The created user with generated ID and timestamps
     * @throws IllegalArgumentException if user data is invalid
     * @throws RuntimeException if email already exists
     */
    public User createUser(User user) {
        // Validate input
        validateUserData(user);
        
        // Check if email already exists
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("User with email " + user.getEmail() + " already exists");
        }
        
        // Set timestamps
        LocalDateTime now = LocalDateTime.now();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        
        // Save and return the user
        return userRepository.save(user);
    }

    /**
     * Get all users
     * 
     * @return List of all users in the database
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Get user by ID
     * 
     * @param id The user ID to find
     * @return Optional containing the user if found, empty if not found
     */
    public Optional<User> getUserById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("User ID must be a positive number");
        }
        return userRepository.findById(id);
    }

    /**
     * Get user by email
     * 
     * @param email The email address to search for
     * @return Optional containing the user if found, empty if not found
     */
    public Optional<User> getUserByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        return userRepository.findByEmail(email);
    }

    /**
     * Update an existing user
     * 
     * This method updates the user's information while preserving the creation timestamp.
     * It validates the data and checks for email conflicts.
     * 
     * @param id The ID of the user to update
     * @param userDetails The new user details
     * @return The updated user
     * @throws RuntimeException if user not found or email conflict
     */
    public User updateUser(Long id, User userDetails) {
        // Validate input
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("User ID must be a positive number");
        }
        validateUserData(userDetails);
        
        // Find the existing user
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        
        // Check for email conflict (if email is being changed)
        if (!existingUser.getEmail().equals(userDetails.getEmail()) &&
            userRepository.existsByEmail(userDetails.getEmail())) {
            throw new RuntimeException("User with email " + userDetails.getEmail() + " already exists");
        }
        
        // Update user fields
        existingUser.setFirstName(userDetails.getFirstName());
        existingUser.setLastName(userDetails.getLastName());
        existingUser.setEmail(userDetails.getEmail());
        existingUser.setPhone(userDetails.getPhone());
        existingUser.setUpdatedAt(LocalDateTime.now());
        
        // Save and return the updated user
        return userRepository.save(existingUser);
    }

    /**
     * Delete a user by ID
     * 
     * @param id The ID of the user to delete
     * @return true if user was deleted, false if user didn't exist
     */
    public boolean deleteUser(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("User ID must be a positive number");
        }
        
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * Search users by first name (case-insensitive)
     * 
     * @param firstName The first name to search for
     * @return List of users matching the first name
     */
    public List<User> searchUsersByFirstName(String firstName) {
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name cannot be null or empty");
        }
        return userRepository.findByFirstNameIgnoreCase(firstName.trim());
    }

    /**
     * Search users by last name (case-insensitive)
     * 
     * @param lastName The last name to search for
     * @return List of users matching the last name
     */
    public List<User> searchUsersByLastName(String lastName) {
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be null or empty");
        }
        return userRepository.findByLastNameIgnoreCase(lastName.trim());
    }

    /**
     * Search users by full name (case-insensitive)
     * 
     * @param firstName The first name to search for
     * @param lastName The last name to search for
     * @return List of users matching both first and last name
     */
    public List<User> searchUsersByFullName(String firstName, String lastName) {
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name cannot be null or empty");
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be null or empty");
        }
        return userRepository.findByFirstNameIgnoreCaseAndLastNameIgnoreCase(
            firstName.trim(), lastName.trim());
    }

    /**
     * Get users created after a specific date
     * 
     * @param date The date to compare against
     * @return List of users created after the specified date
     */
    public List<User> getUsersCreatedAfter(LocalDateTime date) {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        return userRepository.findUsersCreatedAfter(date);
    }

    /**
     * Get users by email domain
     * 
     * @param domain The email domain (e.g., "gmail.com")
     * @return List of users with emails from the specified domain
     */
    public List<User> getUsersByEmailDomain(String domain) {
        if (domain == null || domain.trim().isEmpty()) {
            throw new IllegalArgumentException("Domain cannot be null or empty");
        }
        return userRepository.findUsersByEmailDomain(domain.trim());
    }

    /**
     * Count users by email domain
     * 
     * @param domain The email domain to count
     * @return Number of users with emails from the specified domain
     */
    public long countUsersByEmailDomain(String domain) {
        if (domain == null || domain.trim().isEmpty()) {
            throw new IllegalArgumentException("Domain cannot be null or empty");
        }
        return userRepository.countUsersByEmailDomain(domain.trim());
    }

    /**
     * Get total number of users
     * 
     * @return Total count of users in the database
     */
    public long getTotalUserCount() {
        return userRepository.count();
    }

    /**
     * Check if a user exists by email
     * 
     * @param email The email to check
     * @return true if user exists, false otherwise
     */
    public boolean userExistsByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        return userRepository.existsByEmail(email.trim());
    }

    /**
     * Validate user data
     * 
     * This private method contains business logic for validating user data.
     * It checks for required fields and data format.
     * 
     * @param user The user object to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateUserData(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        
        if (user.getFirstName() == null || user.getFirstName().trim().isEmpty()) {
            throw new IllegalArgumentException("First name is required");
        }
        
        if (user.getLastName() == null || user.getLastName().trim().isEmpty()) {
            throw new IllegalArgumentException("Last name is required");
        }
        
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        
        // Basic email format validation
        if (!user.getEmail().contains("@") || !user.getEmail().contains(".")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        
        // Validate name lengths
        if (user.getFirstName().trim().length() < 2 || user.getFirstName().trim().length() > 50) {
            throw new IllegalArgumentException("First name must be between 2 and 50 characters");
        }
        
        if (user.getLastName().trim().length() < 2 || user.getLastName().trim().length() > 50) {
            throw new IllegalArgumentException("Last name must be between 2 and 50 characters");
        }
    }
}
