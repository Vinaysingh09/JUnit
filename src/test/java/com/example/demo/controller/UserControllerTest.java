package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * UserController Unit Tests
 * 
 * This class contains comprehensive unit tests for the UserController class.
 * We use MockMvc to simulate HTTP requests and test the REST API endpoints.
 * 
 * Key Testing Concepts:
 * - @ExtendWith(MockitoExtension.class): Enables Mockito integration with JUnit 5
 * - @Mock: Creates a mock object for UserService
 * - @InjectMocks: Injects the mock into UserController
 * - MockMvc: Simulates HTTP requests and responses
 * - ObjectMapper: Converts objects to/from JSON
 * - @BeforeEach: Runs before each test method to set up test data
 * - @DisplayName: Provides descriptive names for test methods
 * 
 * Test Categories:
 * 1. Create User Tests (POST /api/users)
 * 2. Read User Tests (GET /api/users)
 * 3. Update User Tests (PUT /api/users/{id})
 * 4. Delete User Tests (DELETE /api/users/{id})
 * 5. Search User Tests (GET /api/users/search)
 * 6. Validation Tests
 * 7. Error Handling Tests
 */
@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    /**
     * Mock UserService - this simulates the service layer
     * We control what this mock returns to test different scenarios
     */
    @Mock
    private UserService userService;

    /**
     * UserController instance with mocked dependencies injected
     * This is the class we're actually testing
     */
    @InjectMocks
    private UserController userController;

    /**
     * MockMvc instance for simulating HTTP requests
     * This allows us to test the REST API endpoints
     */
    private MockMvc mockMvc;

    /**
     * ObjectMapper for converting objects to/from JSON
     * Used in request/response body serialization
     */
    private ObjectMapper objectMapper;

    /**
     * Test data - sample users for testing
     */
    private User testUser1;
    private User testUser2;
    private User testUser3;

    /**
     * Setup method that runs before each test
     * Creates test data and initializes MockMvc
     */
    @BeforeEach
    void setUp() {
        // Initialize MockMvc with the controller and global exception handler
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new com.example.demo.exception.GlobalExceptionHandler())
                .build();
        
        // Initialize ObjectMapper with Java 8 date/time support
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        
        // Create test users with different scenarios
        testUser1 = new User("John", "Doe", "john.doe@example.com", "1234567890");
        testUser1.setId(1L);
        testUser1.setCreatedAt(LocalDateTime.now());
        testUser1.setUpdatedAt(LocalDateTime.now());

        testUser2 = new User("Jane", "Smith", "jane.smith@example.com", "0987654321");
        testUser2.setId(2L);
        testUser2.setCreatedAt(LocalDateTime.now());
        testUser2.setUpdatedAt(LocalDateTime.now());

        testUser3 = new User("Bob", "Johnson", "bob.johnson@gmail.com");
        testUser3.setId(3L);
        testUser3.setCreatedAt(LocalDateTime.now());
        testUser3.setUpdatedAt(LocalDateTime.now());
    }

    // ==================== CREATE USER TESTS ====================

    /**
     * Test successful user creation via POST request
     * 
     * This test verifies that:
     * 1. POST request to /api/users is handled correctly
     * 2. JSON request body is properly deserialized
     * 3. Service method is called with correct parameters
     * 4. HTTP 201 status is returned
     * 5. Response body contains the created user
     */
    @Test
    @DisplayName("Should create user successfully via POST request")
    void createUser_Success() throws Exception {
        // Arrange
        User newUser = new User("Alice", "Brown", "alice.brown@example.com");
        User createdUser = new User("Alice", "Brown", "alice.brown@example.com");
        createdUser.setId(4L);
        createdUser.setCreatedAt(LocalDateTime.now());
        createdUser.setUpdatedAt(LocalDateTime.now());
        
        when(userService.createUser(any(User.class))).thenReturn(createdUser);

        // Act & Assert
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(4))
                .andExpect(jsonPath("$.firstName").value("Alice"))
                .andExpect(jsonPath("$.lastName").value("Brown"))
                .andExpect(jsonPath("$.email").value("alice.brown@example.com"))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());

        // Verify service method was called
        verify(userService).createUser(any(User.class));
    }

    /**
     * Test user creation with invalid data
     * 
     * This test verifies that the controller properly handles
     * validation errors and returns appropriate HTTP status
     */
    @Test
    @DisplayName("Should return bad request when creating user with invalid data")
    void createUser_InvalidData_ReturnsBadRequest() throws Exception {
        // Arrange - User with missing required fields
        User invalidUser = new User("", "", "");

        // Act & Assert
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest());

        // Verify service method was not called
        verify(userService, never()).createUser(any(User.class));
    }

    /**
     * Test user creation with duplicate email
     * 
     * This test verifies that the controller properly handles
     * the case where a user with the same email already exists
     */
    @Test
    @DisplayName("Should handle duplicate email error during user creation")
    void createUser_DuplicateEmail_HandlesError() throws Exception {
        // Arrange
        User newUser = new User("Alice", "Brown", "john.doe@example.com");
        when(userService.createUser(any(User.class)))
                .thenThrow(new RuntimeException("User with email john.doe@example.com already exists"));

        // Act & Assert
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isConflict());

        // Verify service method was called
        verify(userService).createUser(any(User.class));
    }

    // ==================== READ USER TESTS ====================

    /**
     * Test getting all users via GET request
     * 
     * This test verifies that:
     * 1. GET request to /api/users returns all users
     * 2. HTTP 200 status is returned
     * 3. Response body contains the list of users
     */
    @Test
    @DisplayName("Should return all users via GET request")
    void getAllUsers_Success() throws Exception {
        // Arrange
        List<User> users = Arrays.asList(testUser1, testUser2, testUser3);
        when(userService.getAllUsers()).thenReturn(users);

        // Act & Assert
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].firstName").value("Jane"))
                .andExpect(jsonPath("$[2].id").value(3))
                .andExpect(jsonPath("$[2].firstName").value("Bob"));

        // Verify service method was called
        verify(userService).getAllUsers();
    }

    /**
     * Test getting user by ID - success case
     * 
     * This test verifies that:
     * 1. GET request to /api/users/{id} returns the correct user
     * 2. HTTP 200 status is returned when user exists
     * 3. Response body contains the user data
     */
    @Test
    @DisplayName("Should return user when valid ID is provided")
    void getUserById_ValidId_ReturnsUser() throws Exception {
        // Arrange
        when(userService.getUserById(1L)).thenReturn(Optional.of(testUser1));

        // Act & Assert
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));

        // Verify service method was called
        verify(userService).getUserById(1L);
    }

    /**
     * Test getting user by ID - not found case
     * 
     * This test verifies that:
     * 1. GET request to /api/users/{id} returns 404 when user doesn't exist
     * 2. HTTP 404 status is returned
     */
    @Test
    @DisplayName("Should return 404 when user ID does not exist")
    void getUserById_InvalidId_ReturnsNotFound() throws Exception {
        // Arrange
        when(userService.getUserById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/users/999"))
                .andExpect(status().isNotFound());

        // Verify service method was called
        verify(userService).getUserById(999L);
    }

    /**
     * Test getting user by email
     * 
     * This test verifies that the email endpoint works correctly
     */
    @Test
    @DisplayName("Should return user when valid email is provided")
    void getUserByEmail_ValidEmail_ReturnsUser() throws Exception {
        // Arrange
        when(userService.getUserByEmail("john.doe@example.com")).thenReturn(Optional.of(testUser1));

        // Act & Assert
        mockMvc.perform(get("/api/users/email/john.doe@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));

        // Verify service method was called
        verify(userService).getUserByEmail("john.doe@example.com");
    }

    // ==================== UPDATE USER TESTS ====================

    /**
     * Test successful user update via PUT request
     * 
     * This test verifies that:
     * 1. PUT request to /api/users/{id} updates the user correctly
     * 2. HTTP 200 status is returned
     * 3. Response body contains the updated user
     */
    @Test
    @DisplayName("Should update user successfully via PUT request")
    void updateUser_Success() throws Exception {
        // Arrange
        User updateData = new User("John", "Updated", "john.updated@example.com");
        User updatedUser = new User("John", "Updated", "john.updated@example.com");
        updatedUser.setId(1L);
        updatedUser.setCreatedAt(LocalDateTime.now());
        updatedUser.setUpdatedAt(LocalDateTime.now());
        
        when(userService.updateUser(eq(1L), any(User.class))).thenReturn(updatedUser);

        // Act & Assert
        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Updated"))
                .andExpect(jsonPath("$.email").value("john.updated@example.com"));

        // Verify service method was called
        verify(userService).updateUser(eq(1L), any(User.class));
    }

    /**
     * Test user update with non-existent ID
     * 
     * This test verifies that the controller returns 404
     * when attempting to update a user that doesn't exist
     */
    @Test
    @DisplayName("Should return 404 when updating non-existent user")
    void updateUser_NonExistentId_ReturnsNotFound() throws Exception {
        // Arrange
        User updateData = new User("John", "Updated", "john.updated@example.com");
        when(userService.updateUser(eq(999L), any(User.class)))
                .thenThrow(new RuntimeException("User not found with id: 999"));

        // Act & Assert
        mockMvc.perform(put("/api/users/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isNotFound());

        // Verify service method was called
        verify(userService).updateUser(eq(999L), any(User.class));
    }

    // ==================== DELETE USER TESTS ====================

    /**
     * Test successful user deletion via DELETE request
     * 
     * This test verifies that:
     * 1. DELETE request to /api/users/{id} deletes the user correctly
     * 2. HTTP 204 (No Content) status is returned when user exists
     */
    @Test
    @DisplayName("Should delete user successfully via DELETE request")
    void deleteUser_Success() throws Exception {
        // Arrange
        when(userService.deleteUser(1L)).thenReturn(true);

        // Act & Assert
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());

        // Verify service method was called
        verify(userService).deleteUser(1L);
    }

    /**
     * Test user deletion with non-existent ID
     * 
     * This test verifies that the controller returns 404
     * when attempting to delete a user that doesn't exist
     */
    @Test
    @DisplayName("Should return 404 when deleting non-existent user")
    void deleteUser_NonExistentId_ReturnsNotFound() throws Exception {
        // Arrange
        when(userService.deleteUser(999L)).thenReturn(false);

        // Act & Assert
        mockMvc.perform(delete("/api/users/999"))
                .andExpect(status().isNotFound());

        // Verify service method was called
        verify(userService).deleteUser(999L);
    }

    // ==================== SEARCH USER TESTS ====================

    /**
     * Test searching users by first name
     * 
     * This test verifies that the search endpoint works correctly
     * for searching by first name
     */
    @Test
    @DisplayName("Should return users matching first name")
    void searchUsersByFirstName_Success() throws Exception {
        // Arrange
        List<User> users = Arrays.asList(testUser1);
        when(userService.searchUsersByFirstName("John")).thenReturn(users);

        // Act & Assert
        mockMvc.perform(get("/api/users/search")
                .param("firstName", "John"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].firstName").value("John"));

        // Verify service method was called
        verify(userService).searchUsersByFirstName("John");
    }

    /**
     * Test searching users by last name
     * 
     * This test verifies that the search endpoint works correctly
     * for searching by last name
     */
    @Test
    @DisplayName("Should return users matching last name")
    void searchUsersByLastName_Success() throws Exception {
        // Arrange
        List<User> users = Arrays.asList(testUser1);
        when(userService.searchUsersByLastName("Doe")).thenReturn(users);

        // Act & Assert
        mockMvc.perform(get("/api/users/search")
                .param("lastName", "Doe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].lastName").value("Doe"));

        // Verify service method was called
        verify(userService).searchUsersByLastName("Doe");
    }

    /**
     * Test searching users by full name
     * 
     * This test verifies that the search endpoint works correctly
     * for searching by both first and last name
     */
    @Test
    @DisplayName("Should return users matching full name")
    void searchUsersByFullName_Success() throws Exception {
        // Arrange
        List<User> users = Arrays.asList(testUser1);
        when(userService.searchUsersByFullName("John", "Doe")).thenReturn(users);

        // Act & Assert
        mockMvc.perform(get("/api/users/search")
                .param("firstName", "John")
                .param("lastName", "Doe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[0].lastName").value("Doe"));

        // Verify service method was called
        verify(userService).searchUsersByFullName("John", "Doe");
    }

    // ==================== ADDITIONAL ENDPOINT TESTS ====================

    /**
     * Test getting users by email domain
     * 
     * This test verifies that the domain endpoint works correctly
     */
    @Test
    @DisplayName("Should return users with matching email domain")
    void getUsersByEmailDomain_Success() throws Exception {
        // Arrange
        List<User> users = Arrays.asList(testUser3);
        when(userService.getUsersByEmailDomain("gmail.com")).thenReturn(users);

        // Act & Assert
        mockMvc.perform(get("/api/users/domain/gmail.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].email").value("bob.johnson@gmail.com"));

        // Verify service method was called
        verify(userService).getUsersByEmailDomain("gmail.com");
    }

    /**
     * Test getting total user count
     * 
     * This test verifies that the count endpoint works correctly
     */
    @Test
    @DisplayName("Should return correct total user count")
    void getUserCount_Success() throws Exception {
        // Arrange
        when(userService.getTotalUserCount()).thenReturn(3L);

        // Act & Assert
        mockMvc.perform(get("/api/users/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("3"));

        // Verify service method was called
        verify(userService).getTotalUserCount();
    }

    /**
     * Test checking if user exists by email
     * 
     * This test verifies that the exists endpoint works correctly
     */
    @Test
    @DisplayName("Should return true when user with email exists")
    void userExistsByEmail_Exists_ReturnsTrue() throws Exception {
        // Arrange
        when(userService.userExistsByEmail("john.doe@example.com")).thenReturn(true);

        // Act & Assert
        mockMvc.perform(get("/api/users/exists")
                .param("email", "john.doe@example.com"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        // Verify service method was called
        verify(userService).userExistsByEmail("john.doe@example.com");
    }

    /**
     * Test health check endpoint
     * 
     * This test verifies that the health endpoint works correctly
     */
    @Test
    @DisplayName("Should return health status")
    void health_ReturnsStatus() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/users/health"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("User Service is running!")));
    }

    // ==================== ERROR HANDLING TESTS ====================

    /**
     * Test handling of service exceptions
     * 
     * This test verifies that the controller properly handles
     * exceptions thrown by the service layer
     */
    @Test
    @DisplayName("Should handle service exceptions gracefully")
    void handleServiceExceptions() throws Exception {
        // Arrange
        when(userService.getAllUsers()).thenThrow(new RuntimeException("Database connection failed"));

        // Act & Assert
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isInternalServerError());

        // Verify service method was called
        verify(userService).getAllUsers();
    }

    /**
     * Test handling of validation errors in search
     * 
     * This test verifies that the controller properly handles
     * validation errors in search parameters
     */
    @Test
    @DisplayName("Should handle validation errors in search parameters")
    void searchUsers_InvalidParameters_ReturnsBadRequest() throws Exception {
        // Arrange
        when(userService.searchUsersByFirstName(""))
                .thenThrow(new IllegalArgumentException("First name cannot be null or empty"));

        // Act & Assert
        mockMvc.perform(get("/api/users/search")
                .param("firstName", ""))
                .andExpect(status().isBadRequest());

        // Verify service method was called
        verify(userService).searchUsersByFirstName("");
    }
}
