package com.university.coursemanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point of the Course Management System (He thong quan ly khoa hoc).
 *
 * <p>Architecture: Layered (Controller -> Service -> Repository) on top of
 * Spring Boot with JPA/Hibernate. See README.md for the full data flow.</p>
 */
@SpringBootApplication
public class CourseManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(CourseManagementApplication.class, args);
    }
}
