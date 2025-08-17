package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * User Controller Class
 * 
 * This class handles HTTP requests for User operations. It implements REST API endpoints
 * and provides proper HTTP status codes and error handling.
 * 
 * Key Concepts:
 * - @RestController: Marks this class as a REST controller (combines @Controller + @ResponseBody)
 * - @RequestMapping: Defines the base URL path for all endpoints in this controller
 * - @GetMapping, @PostMapping, etc.: Define HTTP methods and specific paths
 * - @PathVariable: Extracts values from URL path
 * - @RequestBody: Extracts JSON from request body
 * - @Valid: Triggers validation on the request body
 * - ResponseEntity: Wraps response with HTTP status code and headers
 * 
 * REST API Endpoints:
 * - GET /api/users - Get all users
 * - GET /api/users/{id} - Get user by ID
 * - POST /api/users - Create new user
 * - PUT /api/users/{id} - Update existing user
 * - DELETE /api/users/{id} - Delete user
 * - GET /api/users/search?firstName=... - Search users by first name
 * - GET /api/users/search?lastName=... - Search users by last name
 * - GET /api/users/search?firstName=...&lastName=... - Search users by full name
 * - GET /api/users/domain/{domain} - Get users by email domain
 * - GET /api/users/count - Get total user count
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    /**
     * UserService dependency injection
     * @Autowired tells Spring to inject the UserService bean
     */
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Create a new user
     * 
     * HTTP Method: POST
     * URL: /api/users
     * 
     * @param user The user object to create (from request body)
     * @return ResponseEntity with created user and HTTP 201 status
     */
    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        try {
            User createdUser = userService.createUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (IllegalArgumentException e) {
            // Return 400 Bad Request for validation errors
            throw new RuntimeException("Validation error: " + e.getMessage());
        } catch (RuntimeException e) {
            // Return 409 Conflict for duplicate email
            if (e.getMessage().contains("already exists")) {
                throw new RuntimeException("User with this email already exists");
            }
            throw e;
        }
    }

    /**
     * Get all users
     * 
     * HTTP Method: GET
     * URL: /api/users
     * 
     * @return ResponseEntity with list of all users and HTTP 200 status
     */
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Get user by ID
     * 
     * HTTP Method: GET
     * URL: /api/users/{id}
     * 
     * @param id The user ID from URL path
     * @return ResponseEntity with user if found (HTTP 200) or not found (HTTP 404)
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Update an existing user
     * 
     * HTTP Method: PUT
     * URL: /api/users/{id}
     * 
     * @param id The user ID from URL path
     * @param userDetails The updated user details from request body
     * @return ResponseEntity with updated user (HTTP 200) or not found (HTTP 404)
     */
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @Valid @RequestBody User userDetails) {
        try {
            User updatedUser = userService.updateUser(id, userDetails);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            if (e.getMessage().contains("already exists")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            throw e;
        }
    }

    /**
     * Delete a user by ID
     * 
     * HTTP Method: DELETE
     * URL: /api/users/{id}
     * 
     * @param id The user ID from URL path
     * @return ResponseEntity with HTTP 204 (No Content) if deleted, 404 if not found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        boolean deleted = userService.deleteUser(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Search users by first name
     * 
     * HTTP Method: GET
     * URL: /api/users/search?firstName=John
     * 
     * @param firstName The first name to search for (query parameter)
     * @return ResponseEntity with list of matching users
     */
    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsersByFirstName(@RequestParam(required = false) String firstName,
                                                           @RequestParam(required = false) String lastName) {
        try {
            if (firstName != null && lastName != null) {
                // Search by full name
                List<User> users = userService.searchUsersByFullName(firstName, lastName);
                return ResponseEntity.ok(users);
            } else if (firstName != null) {
                // Search by first name only
                List<User> users = userService.searchUsersByFirstName(firstName);
                return ResponseEntity.ok(users);
            } else if (lastName != null) {
                // Search by last name only
                List<User> users = userService.searchUsersByLastName(lastName);
                return ResponseEntity.ok(users);
            } else {
                // No search parameters provided, return all users
                List<User> users = userService.getAllUsers();
                return ResponseEntity.ok(users);
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get users by email domain
     * 
     * HTTP Method: GET
     * URL: /api/users/domain/{domain}
     * 
     * @param domain The email domain to search for
     * @return ResponseEntity with list of users from the specified domain
     */
    @GetMapping("/domain/{domain}")
    public ResponseEntity<List<User>> getUsersByEmailDomain(@PathVariable String domain) {
        try {
            List<User> users = userService.getUsersByEmailDomain(domain);
            return ResponseEntity.ok(users);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get total user count
     * 
     * HTTP Method: GET
     * URL: /api/users/count
     * 
     * @return ResponseEntity with total user count
     */
    @GetMapping("/count")
    public ResponseEntity<Long> getUserCount() {
        long count = userService.getTotalUserCount();
        return ResponseEntity.ok(count);
    }

    /**
     * Get users created after a specific date
     * 
     * HTTP Method: GET
     * URL: /api/users/created-after?date=2023-01-01T00:00:00
     * 
     * @param date The date to compare against (ISO format)
     * @return ResponseEntity with list of users created after the date
     */
    @GetMapping("/created-after")
    public ResponseEntity<List<User>> getUsersCreatedAfter(@RequestParam LocalDateTime date) {
        try {
            List<User> users = userService.getUsersCreatedAfter(date);
            return ResponseEntity.ok(users);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Check if user exists by email
     * 
     * HTTP Method: GET
     * URL: /api/users/exists?email=user@example.com
     * 
     * @param email The email to check
     * @return ResponseEntity with boolean indicating if user exists
     */
    @GetMapping("/exists")
    public ResponseEntity<Boolean> userExistsByEmail(@RequestParam String email) {
        try {
            boolean exists = userService.userExistsByEmail(email);
            return ResponseEntity.ok(exists);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get user by email
     * 
     * HTTP Method: GET
     * URL: /api/users/email/{email}
     * 
     * @param email The email address to search for
     * @return ResponseEntity with user if found (HTTP 200) or not found (HTTP 404)
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        try {
            Optional<User> user = userService.getUserByEmail(email);
            return user.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Health check endpoint
     * 
     * HTTP Method: GET
     * URL: /api/users/health
     * 
     * @return ResponseEntity with health status
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("User Service is running! Current time: " + LocalDateTime.now());
    }
}
