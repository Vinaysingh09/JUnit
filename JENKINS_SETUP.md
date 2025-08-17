# Jenkins Setup for Spring Boot User API Testing

This document provides step-by-step instructions for setting up Jenkins to run your JUnit and Mockito test cases automatically.

## Prerequisites

1. **Jenkins Server** (version 2.387.3 or higher)
2. **Java 21** installed on Jenkins server
3. **Maven 3.9.5** installed on Jenkins server
4. **Git** installed on Jenkins server

## Required Jenkins Plugins

Install the following plugins in Jenkins:

1. **Git plugin** - For source code management
2. **Maven Integration plugin** - For Maven build support
3. **JUnit plugin** - For test result reporting
4. **JaCoCo plugin** - For code coverage reporting
5. **HTML Publisher plugin** - For HTML report publishing
6. **Email Extension plugin** - For email notifications
7. **Pipeline plugin** - For pipeline support
8. **Timestamper plugin** - For build timestamps
9. **AnsiColor plugin** - For colored console output

## Jenkins Configuration

### 1. Global Tool Configuration

Go to **Manage Jenkins** → **Global Tool Configuration** and configure:

#### JDK Configuration
- **Name**: `JDK-21`
- **JAVA_HOME**: `/usr/lib/jvm/java-21-openjdk` (or your Java 21 path)

#### Maven Configuration
- **Name**: `Maven-3.9.5`
- **MAVEN_HOME**: `/usr/share/maven` (or your Maven path)

### 2. Create Jenkins Job

#### Option A: Using Jenkinsfile (Recommended)

1. Create a new **Pipeline** job
2. Configure SCM:
   - **Repository URL**: Your Git repository URL
   - **Branch**: `*/main` (or your main branch)
3. Pipeline script from SCM:
   - **Script Path**: `Jenkinsfile`

#### Option B: Using Freestyle Job

1. Create a new **Freestyle project**
2. Import the configuration from `jenkins-config.xml`
3. Update the Git repository URL

## Test Execution in Jenkins

### Pipeline Stages

The Jenkins pipeline includes the following stages:

1. **Checkout** - Clone the repository
2. **Build** - Compile the code
3. **Unit Tests** - Run JUnit tests with Mockito
4. **Integration Tests** - Run integration tests
5. **Code Quality Check** - Verify code coverage
6. **Build JAR** - Create deployable artifact

### Test Types Executed

#### Unit Tests
- **Location**: `src/test/java/com/example/demo/controller/UserControllerTest.java`
- **Location**: `src/test/java/com/example/demo/service/UserServiceTest.java`
- **Framework**: JUnit 5 + Mockito
- **Purpose**: Test individual components in isolation

#### Integration Tests
- **Location**: `src/test/java/com/example/demo/integration/UserIntegrationTest.java`
- **Framework**: JUnit 5 + Spring Boot Test
- **Purpose**: Test complete application stack

## Reports Generated

### 1. JUnit Test Reports
- **Location**: `target/surefire-reports/` (Unit tests)
- **Location**: `target/failsafe-reports/` (Integration tests)
- **Format**: XML files
- **Jenkins Plugin**: JUnit

### 2. Code Coverage Reports
- **Location**: `target/site/jacoco/`
- **Format**: HTML report
- **Jenkins Plugin**: JaCoCo
- **Coverage Thresholds**:
  - Line Coverage: 80% minimum
  - Branch Coverage: 70% minimum

### 3. Build Artifacts
- **Location**: `target/*.jar`
- **Purpose**: Deployable application

## Quality Gates

The pipeline includes quality gates:

1. **Test Success**: All tests must pass
2. **Code Coverage**: Minimum 80% line coverage
3. **Build Success**: Application must compile successfully

## Email Notifications

Configure email notifications for:
- **Build Failures**: Immediate notification to development team
- **Test Failures**: Detailed test failure information
- **Coverage Threshold Violations**: When coverage drops below thresholds

## Troubleshooting

### Common Issues

1. **Java Version Mismatch**
   ```
   Error: Java version not found
   Solution: Ensure Java 21 is installed and configured in Jenkins
   ```

2. **Maven Not Found**
   ```
   Error: Maven command not found
   Solution: Install Maven and configure in Global Tool Configuration
   ```

3. **Test Failures**
   ```
   Error: Tests failing in Jenkins but passing locally
   Solution: Check environment variables and database configuration
   ```

4. **Coverage Threshold Violations**
   ```
   Error: Coverage below 80%
   Solution: Add more test cases or adjust coverage thresholds
   ```

### Debug Commands

Add these to your pipeline for debugging:

```groovy
stage('Debug') {
    steps {
        sh 'java -version'
        sh 'mvn -version'
        sh 'echo $JAVA_HOME'
        sh 'echo $MAVEN_HOME'
        sh 'pwd'
        sh 'ls -la'
    }
}
```

## Best Practices

1. **Parallel Execution**: Run unit tests and integration tests in parallel
2. **Test Categories**: Use JUnit 5 tags to categorize tests
3. **Database Setup**: Use H2 in-memory database for tests
4. **Mocking Strategy**: Mock external dependencies, not internal components
5. **Coverage Goals**: Aim for 80%+ line coverage and 70%+ branch coverage

## Monitoring and Metrics

### Key Metrics to Monitor

1. **Build Success Rate**: Should be > 95%
2. **Test Execution Time**: Should be < 10 minutes
3. **Code Coverage Trend**: Should be stable or increasing
4. **Test Failure Rate**: Should be < 5%

### Dashboard Setup

Create a Jenkins dashboard with:
- Build status widgets
- Test result trends
- Coverage reports
- Build duration charts

## Security Considerations

1. **Credentials**: Store sensitive data in Jenkins credentials
2. **Repository Access**: Use SSH keys or tokens for Git access
3. **Artifact Security**: Secure artifact storage and access
4. **Log Security**: Avoid logging sensitive information

## Performance Optimization

1. **Parallel Testing**: Run tests in parallel where possible
2. **Test Isolation**: Ensure tests don't interfere with each other
3. **Resource Limits**: Set appropriate memory limits for test execution
4. **Caching**: Cache Maven dependencies between builds

## Conclusion

This setup provides a robust CI/CD pipeline that automatically runs your JUnit and Mockito tests, generates comprehensive reports, and ensures code quality through coverage thresholds. The pipeline will help catch issues early and maintain high code quality standards.
