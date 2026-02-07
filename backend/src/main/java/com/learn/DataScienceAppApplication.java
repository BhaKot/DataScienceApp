package com.learn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot Application Entry Point
 * 
 * Initializes the DataScienceApp backend service with REST APIs,
 * feature flags, and data pipeline processing capabilities.
 */
@SpringBootApplication
public class DataScienceAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataScienceAppApplication.class, args);
    }
}
