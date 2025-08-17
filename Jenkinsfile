pipeline {
    agent any
    
    tools {
        maven 'Maven-3.9.5'
        jdk 'JDK-21'
    }
    
    environment {
        JAVA_HOME = tool('JDK-21')
        MAVEN_HOME = tool('Maven-3.9.5')
        PATH = "${JAVA_HOME}/bin:${MAVEN_HOME}/bin:${PATH}"
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        
        stage('Build') {
            steps {
                sh 'mvn clean compile'
            }
        }
        
        stage('Unit Tests') {
            steps {
                sh 'mvn test'
            }
            post {
                always {
                    // Publish JUnit test results
                    junit '**/target/surefire-reports/*.xml'
                    
                    // Publish JaCoCo coverage report
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
        }
        
        stage('Integration Tests') {
            steps {
                sh 'mvn verify -DskipUnitTests=true'
            }
            post {
                always {
                    // Publish integration test results
                    junit '**/target/failsafe-reports/*.xml'
                }
            }
        }
        
        stage('Code Quality Check') {
            steps {
                sh 'mvn jacoco:check'
            }
        }
        
        stage('Build JAR') {
            steps {
                sh 'mvn package -DskipTests'
            }
            post {
                success {
                    archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
                }
            }
        }
    }
    
    post {
        always {
            // Clean workspace
            cleanWs()
        }
        success {
            echo 'Pipeline completed successfully!'
        }
        failure {
            echo 'Pipeline failed!'
        }
    }
}
