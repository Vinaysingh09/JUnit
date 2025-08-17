package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * UserService Unit Tests
 * 
 * This class contains comprehensive unit tests for the UserService class.
 * We use Mockito to mock the UserRepository dependency, allowing us to test
 * the service layer in isolation.
 * 
 * Key Testing Concepts:
 * - @ExtendWith(MockitoExtension.class): Enables Mockito integration with JUnit 5
 * - @Mock: Creates a mock object for UserRepository
 * - @InjectMocks: Injects the mock into UserService
 * - @BeforeEach: Runs before each test method to set up test data
 * - @DisplayName: Provides descriptive names for test methods
 * 
 * Test Categories:
 * 1. Create User Tests
 * 2. Read User Tests (Get operations)
 * 3. Update User Tests
 * 4. Delete User Tests
 * 5. Search User Tests
 * 6. Validation Tests
 * 7. Edge Case Tests
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    /**
     * Mock UserRepository - this simulates the database layer
     * We control what this mock returns to test different scenarios
     */
    @Mock
    private UserRepository userRepository;

    /**
     * UserService instance with mocked dependencies injected
     * This is the class we're actually testing
     */
    @InjectMocks
    private UserService userService;

    /**
     * Test data - sample users for testing
     */
    private User testUser1;
    private User testUser2;
    private User testUser3;

    /**
     * Setup method that runs before each test
     * Creates test data and resets mock behavior
     */
    @BeforeEach
    void setUp() {
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
     * Test successful user creation
     * 
     * This test verifies that:
     * 1. User data is validated
     * 2. Email uniqueness is checked
     * 3. Timestamps are set correctly
     * 4. User is saved to repository
     * 5. Correct user is returned
     */
    @Test
    @DisplayName("Should create user successfully when valid data is provided")
    void createUser_Success() {
        // Arrange (Given) - Set up the test scenario
        User newUser = new User("Alice", "Brown", "alice.brown@example.com");
        
        // Mock repository behavior
        when(userRepository.existsByEmail("alice.brown@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(4L);
            return user;
        });

        // Act (When) - Execute the method being tested
        User createdUser = userService.createUser(newUser);

        // Assert (Then) - Verify the results
        assertNotNull(createdUser);
        assertEquals(4L, createdUser.getId());
        assertEquals("Alice", createdUser.getFirstName());
        assertEquals("Brown", createdUser.getLastName());
        assertEquals("alice.brown@example.com", createdUser.getEmail());
        assertNotNull(createdUser.getCreatedAt());
        assertNotNull(createdUser.getUpdatedAt());

        // Verify that repository methods were called correctly
        verify(userRepository).existsByEmail("alice.brown@example.com");
        verify(userRepository).save(any(User.class));
    }

    /**
     * Test user creation with duplicate email
     * 
     * This test verifies that the service properly handles
     * the case where a user with the same email already exists
     */
    @Test
    @DisplayName("Should throw exception when creating user with duplicate email")
    void createUser_DuplicateEmail_ThrowsException() {
        // Arrange
        User newUser = new User("Alice", "Brown", "john.doe@example.com");
        when(userRepository.existsByEmail("john.doe@example.com")).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.createUser(newUser);
        });

        assertEquals("User with email john.doe@example.com already exists", exception.getMessage());
        verify(userRepository).existsByEmail("john.doe@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    /**
     * Test user creation with invalid data
     * 
     * This test verifies that validation works correctly
     * for various invalid input scenarios
     */
    @Test
    @DisplayName("Should throw exception when creating user with invalid data")
    void createUser_InvalidData_ThrowsException() {
        // Test cases for invalid data
        assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser(null);
        }, "Should throw exception for null user");

        assertThrows(IllegalArgumentException.class, () -> {
            User invalidUser = new User("", "Doe", "test@example.com");
            userService.createUser(invalidUser);
        }, "Should throw exception for empty first name");

        assertThrows(IllegalArgumentException.class, () -> {
            User invalidUser = new User("John", "", "test@example.com");
            userService.createUser(invalidUser);
        }, "Should throw exception for empty last name");

        assertThrows(IllegalArgumentException.class, () -> {
            User invalidUser = new User("John", "Doe", "");
            userService.createUser(invalidUser);
        }, "Should throw exception for empty email");

        assertThrows(IllegalArgumentException.class, () -> {
            User invalidUser = new User("John", "Doe", "invalid-email");
            userService.createUser(invalidUser);
        }, "Should throw exception for invalid email format");
    }

    // ==================== READ USER TESTS ====================

    /**
     * Test getting all users
     * 
     * This test verifies that the service correctly retrieves
     * all users from the repository
     */
    @Test
    @DisplayName("Should return all users successfully")
    void getAllUsers_Success() {
        // Arrange
        List<User> expectedUsers = Arrays.asList(testUser1, testUser2, testUser3);
        when(userRepository.findAll()).thenReturn(expectedUsers);

        // Act
        List<User> actualUsers = userService.getAllUsers();

        // Assert
        assertEquals(3, actualUsers.size());
        assertEquals(expectedUsers, actualUsers);
        verify(userRepository).findAll();
    }

    /**
     * Test getting user by ID - success case
     * 
     * This test verifies that the service correctly retrieves
     * a user when a valid ID is provided
     */
    @Test
    @DisplayName("Should return user when valid ID is provided")
    void getUserById_ValidId_ReturnsUser() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser1));

        // Act
        Optional<User> result = userService.getUserById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testUser1, result.get());
        verify(userRepository).findById(1L);
    }

    /**
     * Test getting user by ID - not found case
     * 
     * This test verifies that the service returns empty Optional
     * when a user with the given ID doesn't exist
     */
    @Test
    @DisplayName("Should return empty when user ID does not exist")
    void getUserById_InvalidId_ReturnsEmpty() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<User> result = userService.getUserById(999L);

        // Assert
        assertFalse(result.isPresent());
        verify(userRepository).findById(999L);
    }

    /**
     * Test getting user by ID with invalid input
     * 
     * This test verifies that the service properly validates
     * the input ID parameter
     */
    @Test
    @DisplayName("Should throw exception when ID is null or negative")
    void getUserById_InvalidInput_ThrowsException() {
        // Test null ID
        assertThrows(IllegalArgumentException.class, () -> {
            userService.getUserById(null);
        });

        // Test negative ID
        assertThrows(IllegalArgumentException.class, () -> {
            userService.getUserById(-1L);
        });

        // Test zero ID
        assertThrows(IllegalArgumentException.class, () -> {
            userService.getUserById(0L);
        });
    }

    /**
     * Test getting user by email
     * 
     * This test verifies that the service correctly retrieves
     * a user by email address
     */
    @Test
    @DisplayName("Should return user when valid email is provided")
    void getUserByEmail_ValidEmail_ReturnsUser() {
        // Arrange
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(testUser1));

        // Act
        Optional<User> result = userService.getUserByEmail("john.doe@example.com");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testUser1, result.get());
        verify(userRepository).findByEmail("john.doe@example.com");
    }

    // ==================== UPDATE USER TESTS ====================

    /**
     * Test successful user update
     * 
     * This test verifies that the service correctly updates
     * an existing user's information
     */
    @Test
    @DisplayName("Should update user successfully when valid data is provided")
    void updateUser_Success() {
        // Arrange
        User updateData = new User("John", "Updated", "john.updated@example.com", "5555555555");
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser1));
        when(userRepository.existsByEmail("john.updated@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser1);

        // Act
        User updatedUser = userService.updateUser(1L, updateData);

        // Assert
        assertNotNull(updatedUser);
        verify(userRepository).findById(1L);
        verify(userRepository).existsByEmail("john.updated@example.com");
        verify(userRepository).save(any(User.class));
    }

    /**
     * Test user update with non-existent ID
     * 
     * This test verifies that the service properly handles
     * attempts to update a user that doesn't exist
     */
    @Test
    @DisplayName("Should throw exception when updating non-existent user")
    void updateUser_NonExistentId_ThrowsException() {
        // Arrange
        User updateData = new User("John", "Updated", "john.updated@example.com");
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.updateUser(999L, updateData);
        });

        assertEquals("User not found with id: 999", exception.getMessage());
        verify(userRepository).findById(999L);
        verify(userRepository, never()).save(any(User.class));
    }

    /**
     * Test user update with duplicate email
     * 
     * This test verifies that the service prevents updating
     * a user with an email that already belongs to another user
     */
    @Test
    @DisplayName("Should throw exception when updating user with duplicate email")
    void updateUser_DuplicateEmail_ThrowsException() {
        // Arrange
        User updateData = new User("John", "Updated", "jane.smith@example.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser1));
        when(userRepository.existsByEmail("jane.smith@example.com")).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.updateUser(1L, updateData);
        });

        assertEquals("User with email jane.smith@example.com already exists", exception.getMessage());
    }

    // ==================== DELETE USER TESTS ====================

    /**
     * Test successful user deletion
     * 
     * This test verifies that the service correctly deletes
     * an existing user
     */
    @Test
    @DisplayName("Should delete user successfully when valid ID is provided")
    void deleteUser_Success() {
        // Arrange
        when(userRepository.existsById(1L)).thenReturn(true);

        // Act
        boolean result = userService.deleteUser(1L);

        // Assert
        assertTrue(result);
        verify(userRepository).existsById(1L);
        verify(userRepository).deleteById(1L);
    }

    /**
     * Test user deletion with non-existent ID
     * 
     * This test verifies that the service returns false
     * when attempting to delete a user that doesn't exist
     */
    @Test
    @DisplayName("Should return false when deleting non-existent user")
    void deleteUser_NonExistentId_ReturnsFalse() {
        // Arrange
        when(userRepository.existsById(999L)).thenReturn(false);

        // Act
        boolean result = userService.deleteUser(999L);

        // Assert
        assertFalse(result);
        verify(userRepository).existsById(999L);
        verify(userRepository, never()).deleteById(any());
    }

    // ==================== SEARCH USER TESTS ====================

    /**
     * Test searching users by first name
     * 
     * This test verifies that the service correctly searches
     * for users by their first name
     */
    @Test
    @DisplayName("Should return users matching first name")
    void searchUsersByFirstName_Success() {
        // Arrange
        List<User> expectedUsers = Arrays.asList(testUser1);
        when(userRepository.findByFirstNameIgnoreCase("John")).thenReturn(expectedUsers);

        // Act
        List<User> result = userService.searchUsersByFirstName("John");

        // Assert
        assertEquals(1, result.size());
        assertEquals(expectedUsers, result);
        verify(userRepository).findByFirstNameIgnoreCase("John");
    }

    /**
     * Test searching users by last name
     * 
     * This test verifies that the service correctly searches
     * for users by their last name
     */
    @Test
    @DisplayName("Should return users matching last name")
    void searchUsersByLastName_Success() {
        // Arrange
        List<User> expectedUsers = Arrays.asList(testUser1);
        when(userRepository.findByLastNameIgnoreCase("Doe")).thenReturn(expectedUsers);

        // Act
        List<User> result = userService.searchUsersByLastName("Doe");

        // Assert
        assertEquals(1, result.size());
        assertEquals(expectedUsers, result);
        verify(userRepository).findByLastNameIgnoreCase("Doe");
    }

    /**
     * Test searching users by full name
     * 
     * This test verifies that the service correctly searches
     * for users by both first and last name
     */
    @Test
    @DisplayName("Should return users matching full name")
    void searchUsersByFullName_Success() {
        // Arrange
        List<User> expectedUsers = Arrays.asList(testUser1);
        when(userRepository.findByFirstNameIgnoreCaseAndLastNameIgnoreCase("John", "Doe"))
                .thenReturn(expectedUsers);

        // Act
        List<User> result = userService.searchUsersByFullName("John", "Doe");

        // Assert
        assertEquals(1, result.size());
        assertEquals(expectedUsers, result);
        verify(userRepository).findByFirstNameIgnoreCaseAndLastNameIgnoreCase("John", "Doe");
    }

    // ==================== VALIDATION TESTS ====================

    /**
     * Test email existence check
     * 
     * This test verifies that the service correctly checks
     * whether a user with a given email exists
     */
    @Test
    @DisplayName("Should return true when user with email exists")
    void userExistsByEmail_Exists_ReturnsTrue() {
        // Arrange
        when(userRepository.existsByEmail("john.doe@example.com")).thenReturn(true);

        // Act
        boolean result = userService.userExistsByEmail("john.doe@example.com");

        // Assert
        assertTrue(result);
        verify(userRepository).existsByEmail("john.doe@example.com");
    }

    /**
     * Test email existence check for non-existent email
     * 
     * This test verifies that the service returns false
     * when no user with the given email exists
     */
    @Test
    @DisplayName("Should return false when user with email does not exist")
    void userExistsByEmail_NotExists_ReturnsFalse() {
        // Arrange
        when(userRepository.existsByEmail("nonexistent@example.com")).thenReturn(false);

        // Act
        boolean result = userService.userExistsByEmail("nonexistent@example.com");

        // Assert
        assertFalse(result);
        verify(userRepository).existsByEmail("nonexistent@example.com");
    }

    /**
     * Test email existence check with invalid input
     * 
     * This test verifies that the service properly validates
     * the email parameter
     */
    @Test
    @DisplayName("Should throw exception when checking email existence with invalid input")
    void userExistsByEmail_InvalidInput_ThrowsException() {
        // Test null email
        assertThrows(IllegalArgumentException.class, () -> {
            userService.userExistsByEmail(null);
        });

        // Test empty email
        assertThrows(IllegalArgumentException.class, () -> {
            userService.userExistsByEmail("");
        });

        // Test whitespace email
        assertThrows(IllegalArgumentException.class, () -> {
            userService.userExistsByEmail("   ");
        });
    }

    // ==================== EDGE CASE TESTS ====================

    /**
     * Test getting total user count
     * 
     * This test verifies that the service correctly returns
     * the total number of users in the database
     */
    @Test
    @DisplayName("Should return correct total user count")
    void getTotalUserCount_Success() {
        // Arrange
        when(userRepository.count()).thenReturn(3L);

        // Act
        long result = userService.getTotalUserCount();

        // Assert
        assertEquals(3L, result);
        verify(userRepository).count();
    }

    /**
     * Test getting users by email domain
     * 
     * This test verifies that the service correctly filters
     * users by their email domain
     */
    @Test
    @DisplayName("Should return users with matching email domain")
    void getUsersByEmailDomain_Success() {
        // Arrange
        List<User> expectedUsers = Arrays.asList(testUser3);
        when(userRepository.findUsersByEmailDomain("gmail.com")).thenReturn(expectedUsers);

        // Act
        List<User> result = userService.getUsersByEmailDomain("gmail.com");

        // Assert
        assertEquals(1, result.size());
        assertEquals(expectedUsers, result);
        verify(userRepository).findUsersByEmailDomain("gmail.com");
    }

    /**
     * Test counting users by email domain
     * 
     * This test verifies that the service correctly counts
     * users with a specific email domain
     */
    @Test
    @DisplayName("Should return correct count for email domain")
    void countUsersByEmailDomain_Success() {
        // Arrange
        when(userRepository.countUsersByEmailDomain("gmail.com")).thenReturn(1L);

        // Act
        long result = userService.countUsersByEmailDomain("gmail.com");

        // Assert
        assertEquals(1L, result);
        verify(userRepository).countUsersByEmailDomain("gmail.com");
    }

    /**
     * Test getting users created after a specific date
     * 
     * This test verifies that the service correctly filters
     * users by their creation date
     */
    @Test
    @DisplayName("Should return users created after specified date")
    void getUsersCreatedAfter_Success() {
        // Arrange
        LocalDateTime date = LocalDateTime.now().minusDays(1);
        List<User> expectedUsers = Arrays.asList(testUser1, testUser2, testUser3);
        when(userRepository.findUsersCreatedAfter(date)).thenReturn(expectedUsers);

        // Act
        List<User> result = userService.getUsersCreatedAfter(date);

        // Assert
        assertEquals(3, result.size());
        assertEquals(expectedUsers, result);
        verify(userRepository).findUsersCreatedAfter(date);
    }
}
