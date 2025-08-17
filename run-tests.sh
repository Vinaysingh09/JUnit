#!/bin/bash

# Test Execution Script for Jenkins
# This script runs all tests and generates reports

set -e  # Exit on any error

echo "=========================================="
echo "Starting Test Execution"
echo "=========================================="

# Set environment variables
export JAVA_HOME=${JAVA_HOME:-/usr/lib/jvm/java-21-openjdk}
export MAVEN_HOME=${MAVEN_HOME:-/usr/share/maven}
export PATH=$JAVA_HOME/bin:$MAVEN_HOME/bin:$PATH

# Display versions
echo "Java Version:"
java -version
echo ""
echo "Maven Version:"
mvn -version
echo ""

# Clean and compile
echo "=========================================="
echo "Cleaning and Compiling..."
echo "=========================================="
mvn clean compile

# Run unit tests
echo "=========================================="
echo "Running Unit Tests..."
echo "=========================================="
mvn test -Dspring.profiles.active=test

# Run integration tests
echo "=========================================="
echo "Running Integration Tests..."
echo "=========================================="
mvn verify -DskipUnitTests=true -Dspring.profiles.active=test

# Generate coverage report
echo "=========================================="
echo "Generating Coverage Report..."
echo "=========================================="
mvn jacoco:report

# Check coverage thresholds
echo "=========================================="
echo "Checking Coverage Thresholds..."
echo "=========================================="
mvn jacoco:check

# Display test results summary
echo "=========================================="
echo "Test Results Summary"
echo "=========================================="

# Count test results
if [ -d "target/surefire-reports" ]; then
    echo "Unit Test Results:"
    find target/surefire-reports -name "*.xml" -exec grep -l "testsuite" {} \; | wc -l | xargs echo "  Test suites:"
    find target/surefire-reports -name "*.xml" -exec grep -o 'tests="[0-9]*"' {} \; | sed 's/tests="//g' | sed 's/"//g' | awk '{sum+=$1} END {print "  Total tests: " sum}'
fi

if [ -d "target/failsafe-reports" ]; then
    echo "Integration Test Results:"
    find target/failsafe-reports -name "*.xml" -exec grep -l "testsuite" {} \; | wc -l | xargs echo "  Test suites:"
    find target/failsafe-reports -name "*.xml" -exec grep -o 'tests="[0-9]*"' {} \; | sed 's/tests="//g' | sed 's/"//g' | awk '{sum+=$1} END {print "  Total tests: " sum}'
fi

echo "=========================================="
echo "Test Execution Completed Successfully!"
echo "=========================================="

# List generated reports
echo "Generated Reports:"
echo "  - JUnit Reports: target/surefire-reports/"
echo "  - Integration Test Reports: target/failsafe-reports/"
echo "  - Coverage Report: target/site/jacoco/index.html"
echo "  - Test Artifacts: target/test-classes/"
