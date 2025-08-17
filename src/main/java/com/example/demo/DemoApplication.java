package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot Application Class
 * 
 * This is the entry point of our Spring Boot application.
 * The @SpringBootApplication annotation is a convenience annotation that adds all of the following:
 * - @Configuration: Tags the class as a source of bean definitions for the application context
 * - @EnableAutoConfiguration: Tells Spring Boot to start adding beans based on classpath settings
 * - @ComponentScan: Tells Spring to look for other components, configurations, and services in the com/example/demo package
 */
@SpringBootApplication
public class DemoApplication {

    /**
     * Main method - the entry point of the application
     * 
     * @param args Command line arguments passed to the application
     */
    public static void main(String[] args) {
        // SpringApplication.run() bootstraps the application, starting Spring context
        // and the embedded web server (Tomcat by default)
        SpringApplication.run(DemoApplication.class, args);
        
        System.out.println("🚀 Spring Boot CRUD Application Started Successfully!");
        System.out.println("📊 H2 Database Console: http://localhost:8080/h2-console");
        System.out.println("🔗 API Base URL: http://localhost:8080/api/users");
    }
}
