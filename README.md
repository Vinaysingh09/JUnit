# Spring Boot CRUD Application with Comprehensive Unit Testing

This project demonstrates a complete Spring Boot CRUD (Create, Read, Update, Delete) application with comprehensive unit testing from beginner to production level. It's designed to help you learn unit testing concepts step by step.

## 🎯 Project Overview

This is a User Management System that provides REST API endpoints for managing users. The project includes:

- **Complete CRUD Operations** for User entities
- **Comprehensive Unit Tests** with detailed explanations
- **Integration Tests** testing the full application stack
- **Best Practices** for Spring Boot development and testing
- **Production-Ready** code structure and patterns

## 🏗️ Project Structure

```
src/
├── main/
│   ├── java/com/example/demo/
│   │   ├── DemoApplication.java          # Main Spring Boot application
│   │   ├── controller/
│   │   │   └── UserController.java       # REST API endpoints
│   │   ├── model/
│   │   │   └── User.java                 # User entity with validation
│   │   ├── repository/
│   │   │   └── UserRepository.java       # Data access layer
│   │   └── service/
│   │       └── UserService.java          # Business logic layer
│   └── resources/
│       └── application.properties        # Application configuration
└── test/
    └── java/com/example/demo/
        ├── controller/
        │   └── UserControllerTest.java   # Controller unit tests
        ├── service/
        │   └── UserServiceTest.java      # Service unit tests
        └── integration/
            └── UserIntegrationTest.java  # Integration tests
```

## 🚀 Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- IDE (IntelliJ IDEA, Eclipse, or VS Code)

### Running the Application

1. **Clone the repository:**
   ```bash
   git clone <repository-url>
   cd JUnit
   ```

2. **Build the project:**
   ```bash
   mvn clean install
   ```

3. **Run the application:**
   ```bash
   mvn spring-boot:run
   ```

4. **Access the application:**
   - Application: http://localhost:8080
   - H2 Database Console: http://localhost:8080/h2-console
   - API Base URL: http://localhost:8080/api/users

### Running Tests

1. **Run all tests:**
   ```bash
   mvn test
   ```

2. **Run specific test categories:**
   ```bash
   # Run only unit tests
   mvn test -Dtest=*Test
   
   # Run only integration tests
   mvn test -Dtest=*IntegrationTest
   
   # Run specific test class
   mvn test -Dtest=UserServiceTest
   ```

3. **Run tests with coverage:**
   ```bash
   mvn test jacoco:report
   ```

## 📚 API Endpoints

### User Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/users` | Get all users |
| `GET` | `/api/users/{id}` | Get user by ID |
| `POST` | `/api/users` | Create new user |
| `PUT` | `/api/users/{id}` | Update existing user |
| `DELETE` | `/api/users/{id}` | Delete user |

### Search Operations

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/users/search?firstName=John` | Search by first name |
| `GET` | `/api/users/search?lastName=Doe` | Search by last name |
| `GET` | `/api/users/search?firstName=John&lastName=Doe` | Search by full name |
| `GET` | `/api/users/domain/{domain}` | Get users by email domain |
| `GET` | `/api/users/email/{email}` | Get user by email |

### Utility Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/users/count` | Get total user count |
| `GET` | `/api/users/exists?email=user@example.com` | Check if user exists |
| `GET` | `/api/users/health` | Health check |

## 🧪 Testing Concepts Explained

### 1. Unit Testing Fundamentals

**What is Unit Testing?**
Unit testing is a software testing method where individual units (functions, methods, classes) are tested in isolation to ensure they work correctly.

**Key Concepts:**
- **Arrange**: Set up test data and conditions
- **Act**: Execute the method being tested
- **Assert**: Verify the expected results

**Example from our project:**
```java
@Test
@DisplayName("Should create user successfully when valid data is provided")
void createUser_Success() {
    // Arrange (Given) - Set up the test scenario
    User newUser = new User("Alice", "Brown", "alice.brown@example.com");
    when(userRepository.existsByEmail("alice.brown@example.com")).thenReturn(false);
    
    // Act (When) - Execute the method being tested
    User createdUser = userService.createUser(newUser);
    
    // Assert (Then) - Verify the results
    assertNotNull(createdUser);
    assertEquals("Alice", createdUser.getFirstName());
}
```

### 2. Mocking with Mockito

**What is Mocking?**
Mocking is creating fake objects that simulate the behavior of real objects. This allows us to test components in isolation.

**Key Mockito Concepts:**
- `@Mock`: Creates a mock object
- `@InjectMocks`: Injects mocks into the class being tested
- `when().thenReturn()`: Defines mock behavior
- `verify()`: Ensures methods were called correctly

**Example:**
```java
@Mock
private UserRepository userRepository;

@InjectMocks
private UserService userService;

@Test
void testWithMock() {
    // Define mock behavior
    when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
    
    // Test the service
    Optional<User> result = userService.getUserById(1L);
    
    // Verify mock was called
    verify(userRepository).findById(1L);
}
```

### 3. Test Categories

#### Unit Tests (`UserServiceTest.java`)
- Test individual methods in isolation
- Use mocks for dependencies
- Fast execution
- Focus on business logic

#### Controller Tests (`UserControllerTest.java`)
- Test REST API endpoints
- Use MockMvc to simulate HTTP requests
- Verify HTTP status codes and responses
- Test request/response handling

#### Integration Tests (`UserIntegrationTest.java`)
- Test the entire application stack
- Use real database (H2 in-memory)
- Test complete workflows
- Verify data persistence

### 4. Testing Best Practices

#### Test Naming Convention
```java
@Test
@DisplayName("Should [expected behavior] when [condition]")
void methodName_Scenario_ExpectedResult() {
    // test implementation
}
```

#### Test Organization
```java
// Group related tests with comments
// ==================== CREATE USER TESTS ====================

@Test
void createUser_Success() { /* ... */ }

@Test
void createUser_InvalidData_ThrowsException() { /* ... */ }
```

#### Assertion Best Practices
```java
// Use specific assertions
assertEquals(expected, actual, "Custom message");
assertNotNull(result);
assertTrue(condition);
assertThrows(Exception.class, () -> methodCall());
```

## 🔧 Configuration

### Application Properties

The application uses H2 in-memory database for development and testing:

```properties
# H2 Database Configuration
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.username=sa
spring.datasource.password=password

# JPA Configuration
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
```

### Test Configuration

Tests use the same H2 database but with transaction rollback:

```java
@SpringBootTest
@Transactional  // Each test runs in a transaction that gets rolled back
@ActiveProfiles("test")
```

## 📖 Learning Path

### Beginner Level
1. **Start with Unit Tests**: Read `UserServiceTest.java` to understand basic unit testing
2. **Learn Mocking**: Study how `@Mock` and `@InjectMocks` work
3. **Understand Assertions**: See different types of assertions used
4. **Practice**: Try adding new test methods

### Intermediate Level
1. **Controller Testing**: Study `UserControllerTest.java` to learn API testing
2. **MockMvc**: Understand how to simulate HTTP requests
3. **Response Validation**: Learn to verify HTTP status codes and JSON responses
4. **Error Handling**: See how to test error scenarios

### Advanced Level
1. **Integration Testing**: Study `UserIntegrationTest.java`
2. **Database Testing**: Understand testing with real database operations
3. **Test Data Management**: Learn about `@BeforeEach` and test data setup
4. **Performance Testing**: Consider adding performance tests

### Production Level
1. **Test Coverage**: Aim for 80%+ code coverage
2. **Test Categories**: Organize tests by type and purpose
3. **CI/CD Integration**: Set up automated testing in pipelines
4. **Test Maintenance**: Keep tests updated with code changes

## 🛠️ Development Workflow

### Adding New Features
1. Write failing tests first (TDD approach)
2. Implement the feature
3. Ensure all tests pass
4. Refactor if needed

### Example: Adding a New Endpoint
```java
// 1. Add test first
@Test
void getUserByPhone_ValidPhone_ReturnsUser() {
    // test implementation
}

// 2. Add controller method
@GetMapping("/phone/{phone}")
public ResponseEntity<User> getUserByPhone(@PathVariable String phone) {
    // implementation
}

// 3. Add service method
public Optional<User> getUserByPhone(String phone) {
    // implementation
}

// 4. Add repository method
Optional<User> findByPhone(String phone);
```

## 🐛 Common Issues and Solutions

### Test Failures
- **NullPointerException**: Check if mocks are properly configured
- **Database Issues**: Ensure `@Transactional` is used for integration tests
- **Assertion Failures**: Verify expected vs actual values

### Performance Issues
- **Slow Tests**: Use `@DirtiesContext` sparingly
- **Memory Issues**: Clean up test data in `@AfterEach`

## 📈 Next Steps

### Advanced Testing Topics
1. **Test Containers**: For testing with real databases
2. **WireMock**: For testing external API calls
3. **Cucumber**: For behavior-driven development
4. **Performance Testing**: JMeter or Gatling integration

### Production Considerations
1. **Test Environments**: Separate test configurations
2. **Data Management**: Test data factories and builders
3. **Monitoring**: Test execution metrics
4. **Documentation**: Keep test documentation updated

## 🤝 Contributing

When contributing to this project:

1. Write tests for new features
2. Ensure all existing tests pass
3. Follow the established naming conventions
4. Add comments explaining complex test scenarios
5. Update this README if needed

## 📝 License

This project is for educational purposes. Feel free to use and modify as needed.

---

**Happy Testing! 🎉**

Remember: Good tests are like good documentation - they help you and others understand how your code should work.
