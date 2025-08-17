package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * User Entity Class
 * 
 * This class represents a User in our application. It's annotated with JPA annotations
 * to map it to a database table and validation annotations to ensure data integrity.
 * 
 * Key Concepts:
 * - @Entity: Marks this class as a JPA entity (database table)
 * - @Table: Specifies the table name in the database
 * - @Id: Marks a field as the primary key
 * - @GeneratedValue: Specifies how the primary key should be generated
 * - @Column: Specifies column properties
 * - @NotBlank: Ensures the field is not null, empty, or whitespace
 * - @Email: Validates that the field contains a valid email format
 * - @Size: Specifies the minimum and maximum length of the field
 */
@Entity
@Table(name = "users")
public class User {

    /**
     * Primary key for the user
     * @GeneratedValue(strategy = GenerationType.IDENTITY) means the database will
     * automatically generate unique IDs (auto-increment)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * User's first name
     * @NotBlank ensures the field is not null, empty, or just whitespace
     * @Size specifies minimum and maximum length
     */
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    @Column(name = "first_name", nullable = false)
    private String firstName;

    /**
     * User's last name
     */
    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    @Column(name = "last_name", nullable = false)
    private String lastName;

    /**
     * User's email address
     * @Email validates that the field contains a valid email format
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    /**
     * User's phone number
     */
    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    @Column(name = "phone")
    private String phone;

    /**
     * Timestamp when the user was created
     * This field is automatically set when a new user is created
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when the user was last updated
     * This field is automatically updated when the user is modified
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Default constructor (required by JPA)
    public User() {
    }

    // Constructor with required fields
    public User(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Constructor with all fields
    public User(String firstName, String lastName, String email, String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
        this.updatedAt = LocalDateTime.now();
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
        this.updatedAt = LocalDateTime.now();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        this.updatedAt = LocalDateTime.now();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Custom method to get the full name of the user
     * This is a business logic method that combines first and last name
     * @JsonIgnore prevents this method from being serialized to JSON
     */
    @JsonIgnore
    public String getFullName() {
        return firstName + " " + lastName;
    }

    /**
     * Override toString method for better debugging and logging
     */
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    /**
     * Override equals method to compare users based on their ID
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id != null && id.equals(user.id);
    }

    /**
     * Override hashCode method to be consistent with equals
     */
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
