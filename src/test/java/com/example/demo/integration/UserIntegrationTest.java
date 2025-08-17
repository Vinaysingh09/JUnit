package com.example.demo.integration;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * User Integration Tests
 * 
 * This class contains integration tests that test the entire application stack,
 * including the database layer. These tests use a real H2 in-memory database
 * and test the complete flow from controller to database.
 * 
 * Key Integration Testing Concepts:
 * - @SpringBootTest: Loads the entire Spring application context
 * - @AutoConfigureWebMvc: Configures MockMvc for web layer testing
 * - @ActiveProfiles: Specifies which profile to use (test profile)
 * - @Transactional: Each test runs in a transaction that gets rolled back
 * - Real Database: Uses actual H2 database operations
 * - Full Stack: Tests controller -> service -> repository -> database flow
 * 
 * Integration Test Categories:
 * 1. Full CRUD Operations
 * 2. Database Operations
 * 3. Business Logic Validation
 * 4. Error Scenarios
 * 5. Data Persistence
 */
@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class UserIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    /**
     * Setup method that runs before each test
     * Initializes MockMvc and ObjectMapper
     */
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        
        // Clear the database before each test
        userRepository.deleteAll();
    }

    // ==================== FULL STACK CRUD TESTS ====================

    /**
     * Test complete user creation flow
     * 
     * This integration test verifies the entire flow:
     * 1. HTTP POST request to controller
     * 2. Controller calls service
     * 3. Service validates and calls repository
     * 4. Repository saves to database
     * 5. Response is returned through all layers
     */
    @Test
    @DisplayName("Should create user through complete application stack")
    void createUser_FullStack_Success() throws Exception {
        // Arrange
        User newUser = new User("Alice", "Brown", "alice.brown@example.com", "1234567890");

        // Act & Assert - Test through HTTP endpoint
        String response = mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("Alice"))
                .andExpect(jsonPath("$.lastName").value("Brown"))
                .andExpect(jsonPath("$.email").value("alice.brown@example.com"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Verify user was actually saved in database
        User savedUser = objectMapper.readValue(response, User.class);
        Optional<User> foundUser = userRepository.findById(savedUser.getId());
        assertTrue(foundUser.isPresent());
        assertEquals("Alice", foundUser.get().getFirstName());
        assertEquals("alice.brown@example.com", foundUser.get().getEmail());
    }

    /**
     * Test complete user retrieval flow
     * 
     * This test verifies that users can be retrieved through the complete stack
     */
    @Test
    @DisplayName("Should retrieve user through complete application stack")
    void getUserById_FullStack_Success() throws Exception {
        // Arrange - Create user directly in database
        User user = new User("John", "Doe", "john.doe@example.com");
        User savedUser = userRepository.save(user);

        // Act & Assert - Test through HTTP endpoint
        mockMvc.perform(get("/api/users/" + savedUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedUser.getId()))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    /**
     * Test complete user update flow
     * 
     * This test verifies that users can be updated through the complete stack
     */
    @Test
    @DisplayName("Should update user through complete application stack")
    void updateUser_FullStack_Success() throws Exception {
        // Arrange - Create user directly in database
        User user = new User("John", "Doe", "john.doe@example.com");
        User savedUser = userRepository.save(user);

        // Prepare update data
        User updateData = new User("John", "Updated", "john.updated@example.com", "5555555555");

        // Act & Assert - Test through HTTP endpoint
        mockMvc.perform(put("/api/users/" + savedUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedUser.getId()))
                .andExpect(jsonPath("$.lastName").value("Updated"))
                .andExpect(jsonPath("$.email").value("john.updated@example.com"))
                .andExpect(jsonPath("$.phone").value("5555555555"));

        // Verify user was actually updated in database
        Optional<User> updatedUser = userRepository.findById(savedUser.getId());
        assertTrue(updatedUser.isPresent());
        assertEquals("Updated", updatedUser.get().getLastName());
        assertEquals("john.updated@example.com", updatedUser.get().getEmail());
    }

    /**
     * Test complete user deletion flow
     * 
     * This test verifies that users can be deleted through the complete stack
     */
    @Test
    @DisplayName("Should delete user through complete application stack")
    void deleteUser_FullStack_Success() throws Exception {
        // Arrange - Create user directly in database
        User user = new User("John", "Doe", "john.doe@example.com");
        User savedUser = userRepository.save(user);

        // Act & Assert - Test through HTTP endpoint
        mockMvc.perform(delete("/api/users/" + savedUser.getId()))
                .andExpect(status().isNoContent());

        // Verify user was actually deleted from database
        Optional<User> deletedUser = userRepository.findById(savedUser.getId());
        assertFalse(deletedUser.isPresent());
    }

    // ==================== BUSINESS LOGIC INTEGRATION TESTS ====================

    /**
     * Test email uniqueness constraint
     * 
     * This test verifies that the business rule preventing duplicate emails
     * works correctly through the entire application stack
     */
    @Test
    @DisplayName("Should prevent duplicate email creation through complete stack")
    void createUser_DuplicateEmail_FullStack_ThrowsException() throws Exception {
        // Arrange - Create first user
        User user1 = new User("John", "Doe", "john.doe@example.com");
        userRepository.save(user1);

        // Act & Assert - Try to create second user with same email
        User user2 = new User("Jane", "Smith", "john.doe@example.com");
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user2)))
                .andExpect(status().isConflict());

        // Verify only one user exists in database
        List<User> allUsers = userRepository.findAll();
        assertEquals(1, allUsers.size());
    }

    /**
     * Test data validation through complete stack
     * 
     * This test verifies that validation annotations work correctly
     * through the entire application stack
     */
    @Test
    @DisplayName("Should validate data through complete application stack")
    void createUser_InvalidData_FullStack_ReturnsBadRequest() throws Exception {
        // Arrange - User with invalid data (empty required fields)
        User invalidUser = new User("", "", "");

        // Act & Assert
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest());

        // Verify no user was created in database
        List<User> allUsers = userRepository.findAll();
        assertEquals(0, allUsers.size());
    }

    // ==================== DATABASE OPERATION TESTS ====================

    /**
     * Test repository operations directly
     * 
     * This test verifies that the repository layer works correctly
     * with the actual database
     */
    @Test
    @DisplayName("Should perform repository operations correctly")
    void repositoryOperations_Success() {
        // Test save operation
        User user = new User("Test", "User", "test@example.com");
        User savedUser = userRepository.save(user);
        assertNotNull(savedUser.getId());

        // Test findById operation
        Optional<User> foundUser = userRepository.findById(savedUser.getId());
        assertTrue(foundUser.isPresent());
        assertEquals("Test", foundUser.get().getFirstName());

        // Test findByEmail operation
        Optional<User> foundByEmail = userRepository.findByEmail("test@example.com");
        assertTrue(foundByEmail.isPresent());
        assertEquals(savedUser.getId(), foundByEmail.get().getId());

        // Test existsByEmail operation
        assertTrue(userRepository.existsByEmail("test@example.com"));
        assertFalse(userRepository.existsByEmail("nonexistent@example.com"));

        // Test findAll operation
        List<User> allUsers = userRepository.findAll();
        assertEquals(1, allUsers.size());

        // Test delete operation
        userRepository.deleteById(savedUser.getId());
        Optional<User> deletedUser = userRepository.findById(savedUser.getId());
        assertFalse(deletedUser.isPresent());
    }

    /**
     * Test custom query methods
     * 
     * This test verifies that custom repository methods work correctly
     */
    @Test
    @DisplayName("Should execute custom repository queries correctly")
    void customRepositoryQueries_Success() {
        // Arrange - Create test users
        User user1 = new User("John", "Doe", "john.doe@gmail.com");
        User user2 = new User("Jane", "Doe", "jane.doe@yahoo.com");
        User user3 = new User("Bob", "Smith", "bob.smith@gmail.com");
        
        user1.setCreatedAt(LocalDateTime.now());
        user2.setCreatedAt(LocalDateTime.now());
        user3.setCreatedAt(LocalDateTime.now());
        
        userRepository.saveAll(Arrays.asList(user1, user2, user3));

        // Test findByFirstNameIgnoreCase
        List<User> johnUsers = userRepository.findByFirstNameIgnoreCase("john");
        assertEquals(1, johnUsers.size());
        assertEquals("John", johnUsers.get(0).getFirstName());

        // Test findByLastNameIgnoreCase
        List<User> doeUsers = userRepository.findByLastNameIgnoreCase("doe");
        assertEquals(2, doeUsers.size());

        // Test findByFirstNameIgnoreCaseAndLastNameIgnoreCase
        List<User> johnDoeUsers = userRepository.findByFirstNameIgnoreCaseAndLastNameIgnoreCase("john", "doe");
        assertEquals(1, johnDoeUsers.size());

        // Test findUsersByEmailDomain
        List<User> gmailUsers = userRepository.findUsersByEmailDomain("gmail.com");
        assertEquals(2, gmailUsers.size());

        // Test countUsersByEmailDomain
        long gmailCount = userRepository.countUsersByEmailDomain("gmail.com");
        assertEquals(2, gmailCount);
    }

    // ==================== SERVICE LAYER INTEGRATION TESTS ====================

    /**
     * Test service layer with real repository
     * 
     * This test verifies that the service layer works correctly
     * with the actual repository and database
     */
    @Test
    @DisplayName("Should perform service operations with real repository")
    void serviceOperations_WithRealRepository_Success() {
        // Test create user
        User newUser = new User("Service", "Test", "service.test@example.com");
        User createdUser = userService.createUser(newUser);
        assertNotNull(createdUser.getId());
        assertNotNull(createdUser.getCreatedAt());
        assertNotNull(createdUser.getUpdatedAt());

        // Test get user by ID
        Optional<User> foundUser = userService.getUserById(createdUser.getId());
        assertTrue(foundUser.isPresent());
        assertEquals("Service", foundUser.get().getFirstName());

        // Test get user by email
        Optional<User> foundByEmail = userService.getUserByEmail("service.test@example.com");
        assertTrue(foundByEmail.isPresent());
        assertEquals(createdUser.getId(), foundByEmail.get().getId());

        // Test update user
        User updateData = new User("Service", "Updated", "service.updated@example.com");
        User updatedUser = userService.updateUser(createdUser.getId(), updateData);
        assertEquals("Updated", updatedUser.getLastName());
        assertEquals("service.updated@example.com", updatedUser.getEmail());

        // Test delete user
        boolean deleted = userService.deleteUser(createdUser.getId());
        assertTrue(deleted);
        
        Optional<User> deletedUser = userService.getUserById(createdUser.getId());
        assertFalse(deletedUser.isPresent());
    }

    /**
     * Test service validation with real repository
     * 
     * This test verifies that service layer validation works correctly
     */
    @Test
    @DisplayName("Should validate data in service layer with real repository")
    void serviceValidation_WithRealRepository_ThrowsException() {
        // Test null user
        assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser(null);
        });

        // Test user with invalid data
        User invalidUser = new User("", "Test", "invalid-email");
        assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser(invalidUser);
        });

        // Test duplicate email
        User user1 = new User("First", "User", "duplicate@example.com");
        userService.createUser(user1);

        User user2 = new User("Second", "User", "duplicate@example.com");
        assertThrows(RuntimeException.class, () -> {
            userService.createUser(user2);
        });
    }

    // ==================== ERROR SCENARIO TESTS ====================

    /**
     * Test error scenarios through complete stack
     * 
     * This test verifies that error handling works correctly
     * through the entire application stack
     */
    @Test
    @DisplayName("Should handle error scenarios through complete stack")
    void errorScenarios_FullStack_HandledCorrectly() throws Exception {
        // Test getting non-existent user
        mockMvc.perform(get("/api/users/999"))
                .andExpect(status().isNotFound());

        // Test updating non-existent user
        User updateData = new User("Non", "Existent", "non.existent@example.com");
        mockMvc.perform(put("/api/users/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isNotFound());

        // Test deleting non-existent user
        mockMvc.perform(delete("/api/users/999"))
                .andExpect(status().isNotFound());

        // Test invalid JSON
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("invalid json"))
                .andExpect(status().isBadRequest());
    }
}
