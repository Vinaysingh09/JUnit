# Jenkins Test Commands Quick Reference

## Basic Test Commands

### Run All Tests
```bash
mvn clean test verify
```

### Run Only Unit Tests
```bash
mvn test
```

### Run Only Integration Tests
```bash
mvn verify -DskipUnitTests=true
```

### Run Tests with Coverage
```bash
mvn clean test jacoco:report
```

### Run Tests and Check Coverage Thresholds
```bash
mvn clean test jacoco:report jacoco:check
```

## Jenkins Pipeline Commands

### Complete Pipeline Execution
```bash
# In Jenkins pipeline
sh 'mvn clean compile test verify jacoco:report jacoco:check'
```

### Parallel Test Execution
```groovy
parallel(
    "Unit Tests": {
        sh 'mvn test'
    },
    "Integration Tests": {
        sh 'mvn verify -DskipUnitTests=true'
    }
)
```

### Test with Specific Profile
```bash
mvn test -Dspring.profiles.active=test
```

## Test Result Analysis

### View Test Results
```bash
# Check test results
find target/surefire-reports -name "*.xml" -exec echo "Unit Test: {}" \;
find target/failsafe-reports -name "*.xml" -exec echo "Integration Test: {}" \;
```

### Count Test Results
```bash
# Count total tests
find target/surefire-reports -name "*.xml" -exec grep -o 'tests="[0-9]*"' {} \; | sed 's/tests="//g' | sed 's/"//g' | awk '{sum+=$1} END {print "Total Unit Tests: " sum}'
```

### Check Coverage
```bash
# View coverage report
cat target/site/jacoco/index.html | grep -o 'Total[^<]*' | head -1
```

## Debug Commands

### Environment Check
```bash
echo "Java Version: $(java -version 2>&1 | head -1)"
echo "Maven Version: $(mvn -version | head -1)"
echo "JAVA_HOME: $JAVA_HOME"
echo "MAVEN_HOME: $MAVEN_HOME"
echo "Java Version Check: $(java -version 2>&1 | grep -o 'version "[^"]*"' | cut -d'"' -f2)"
```

### Test Debug
```bash
# Run tests with debug output
mvn test -X

# Run specific test class
mvn test -Dtest=UserControllerTest

# Run specific test method
mvn test -Dtest=UserControllerTest#createUser_Success
```

## Jenkins Post-Actions

### Archive Test Results
```groovy
post {
    always {
        junit '**/target/surefire-reports/*.xml'
        junit '**/target/failsafe-reports/*.xml'
        publishHTML([
            allowMissing: false,
            alwaysLinkToLastBuild: true,
            keepAll: true,
            reportDir: 'target/site/jacoco',
            reportFiles: 'index.html',
            reportName: 'JaCoCo Coverage Report'
        ])
    }
}
```

### Email Notifications
```groovy
post {
    failure {
        emailext (
            subject: "Build Failed: ${env.JOB_NAME} [${env.BUILD_NUMBER}]",
            body: "Build failed. Check console output at ${env.BUILD_URL}",
            recipientProviders: [[$class: 'DevelopersRecipientProvider']]
        )
    }
}
```

## Performance Optimization

### Parallel Test Execution
```groovy
stage('Parallel Tests') {
    parallel(
        "Unit Tests": {
            sh 'mvn test -Dparallel=true'
        },
        "Integration Tests": {
            sh 'mvn verify -DskipUnitTests=true -Dparallel=true'
        }
    )
}
```

### Test Categories
```bash
# Run only fast tests
mvn test -Dgroups="fast"

# Run only slow tests
mvn test -Dgroups="slow"

# Exclude integration tests
mvn test -DexcludedGroups="integration"
```

## Quality Gates

### Coverage Threshold Check
```bash
# Check if coverage meets thresholds
mvn jacoco:check -Djacoco.lineCoverage=0.80 -Djacoco.branchCoverage=0.70
```

### Test Failure Threshold
```bash
# Fail build if more than 5% of tests fail
mvn test -DtestFailureIgnore=false -DfailIfNoTests=false
```

## Monitoring Commands

### Build Statistics
```bash
# Get build duration
echo "Build Duration: $BUILD_DURATION seconds"

# Get test execution time
find target/surefire-reports -name "*.xml" -exec grep -o 'time="[0-9.]*"' {} \; | sed 's/time="//g' | sed 's/"//g' | awk '{sum+=$1} END {print "Total Test Time: " sum " seconds"}'
```

### Resource Usage
```bash
# Monitor memory usage
free -h

# Monitor disk usage
df -h

# Monitor CPU usage
top -bn1 | grep "Cpu(s)"
```

## Troubleshooting Commands

### Test Environment Issues
```bash
# Check database connectivity
mvn test -Dspring.datasource.url=jdbc:h2:mem:testdb

# Check application properties
cat src/test/resources/application-test.properties

# Check classpath
mvn dependency:tree
```

### Memory Issues
```bash
# Increase memory for tests
mvn test -DargLine="-Xmx2048m -XX:MaxPermSize=512m"

# Check memory usage during tests
mvn test -DargLine="-XX:+PrintGC -XX:+PrintGCTimeStamps"
```

## Best Practices Summary

1. **Always use clean builds** in Jenkins: `mvn clean test`
2. **Set appropriate timeouts** for long-running tests
3. **Use parallel execution** for faster builds
4. **Archive test results** for historical analysis
5. **Set up quality gates** for coverage and test success
6. **Monitor resource usage** to prevent build failures
7. **Use specific test profiles** for different environments
8. **Implement proper error handling** in pipelines
