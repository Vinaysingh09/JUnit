package com.example.demo.repository;

import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * User Repository Interface
 * 
 * This interface extends JpaRepository to provide basic CRUD operations for the User entity.
 * Spring Data JPA automatically implements this interface at runtime.
 * 
 * Key Concepts:
 * - @Repository: Marks this interface as a Spring Data repository
 * - JpaRepository<User, Long>: Provides basic CRUD operations where:
 *   - User is the entity type
 *   - Long is the type of the primary key (ID)
 * - Custom query methods: Spring Data JPA can create queries from method names
 * - @Query: Allows you to write custom JPQL or native SQL queries
 * 
 * Available methods from JpaRepository:
 * - save(entity): Save or update an entity
 * - findById(id): Find entity by ID
 * - findAll(): Find all entities
 * - delete(entity): Delete an entity
 * - count(): Count total entities
 * - existsById(id): Check if entity exists by ID
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find user by email address
     * 
     * Spring Data JPA automatically creates a query based on the method name:
     * "findBy" + "Email" = "SELECT u FROM User u WHERE u.email = ?1"
     * 
     * @param email The email address to search for
     * @return Optional<User> - contains the user if found, empty if not found
     */
    Optional<User> findByEmail(String email);

    /**
     * Find users by first name (case-insensitive)
     * 
     * Method name: "findByFirstNameIgnoreCase"
     * Generated query: "SELECT u FROM User u WHERE LOWER(u.firstName) = LOWER(?1)"
     * 
     * @param firstName The first name to search for
     * @return List of users with the given first name
     */
    List<User> findByFirstNameIgnoreCase(String firstName);

    /**
     * Find users by last name (case-insensitive)
     * 
     * @param lastName The last name to search for
     * @return List of users with the given last name
     */
    List<User> findByLastNameIgnoreCase(String lastName);

    /**
     * Find users by first name and last name (case-insensitive)
     * 
     * Method name: "findByFirstNameIgnoreCaseAndLastNameIgnoreCase"
     * Generated query: "SELECT u FROM User u WHERE LOWER(u.firstName) = LOWER(?1) AND LOWER(u.lastName) = LOWER(?2)"
     * 
     * @param firstName The first name to search for
     * @param lastName The last name to search for
     * @return List of users matching both first and last name
     */
    List<User> findByFirstNameIgnoreCaseAndLastNameIgnoreCase(String firstName, String lastName);

    /**
     * Check if a user exists with the given email
     * 
     * Method name: "existsByEmail"
     * Generated query: "SELECT COUNT(u) > 0 FROM User u WHERE u.email = ?1"
     * 
     * @param email The email to check
     * @return true if a user with this email exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Find users by phone number
     * 
     * @param phone The phone number to search for
     * @return List of users with the given phone number
     */
    List<User> findByPhone(String phone);

    /**
     * Custom query to find users created after a specific date
     * 
     * @Query annotation allows you to write custom JPQL queries
     * JPQL (Java Persistence Query Language) is similar to SQL but works with entities
     * 
     * @param date The date to compare against
     * @return List of users created after the specified date
     */
    @Query("SELECT u FROM User u WHERE u.createdAt >= :date")
    List<User> findUsersCreatedAfter(@Param("date") java.time.LocalDateTime date);

    /**
     * Custom query to find users by email domain
     * 
     * This query uses LIKE operator to find users whose email ends with a specific domain
     * 
     * @param domain The email domain (e.g., "gmail.com")
     * @return List of users with emails from the specified domain
     */
    @Query("SELECT u FROM User u WHERE u.email LIKE %:domain")
    List<User> findUsersByEmailDomain(@Param("domain") String domain);

    /**
     * Custom query to count users by email domain
     * 
     * @param domain The email domain to count
     * @return Number of users with emails from the specified domain
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.email LIKE %:domain")
    long countUsersByEmailDomain(@Param("domain") String domain);

    /**
     * Find users with phone numbers (not null or empty)
     * 
     * Method name: "findByPhoneIsNotNullAndPhoneNot"
     * Generated query: "SELECT u FROM User u WHERE u.phone IS NOT NULL AND u.phone != ''"
     * 
     * @return List of users who have phone numbers
     */
    List<User> findByPhoneIsNotNullAndPhoneNot(String emptyString);
}
