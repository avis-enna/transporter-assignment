package com.transporter.assignment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot application class for the Transporter Assignment System.
 * 
 * This application optimizes the assignment of transporters to lanes with the following objectives:
 * 1. Cost Minimization: Minimize the total cost of assigning transporters to lanes
 * 2. Maximize Transporter Usage: Utilize the maximum number of transporters up to a user-specified limit
 * 3. Full Lane Coverage: Ensure every lane is assigned to at least one transporter
 */
@SpringBootApplication
public class TransporterAssignmentApplication {

    public static void main(String[] args) {
        SpringApplication.run(TransporterAssignmentApplication.class, args);
    }
}
