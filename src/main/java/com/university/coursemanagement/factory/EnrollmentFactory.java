package com.university.coursemanagement.factory;

import com.university.coursemanagement.entity.Course;
import com.university.coursemanagement.entity.Enrollment;
import com.university.coursemanagement.entity.Student;
import com.university.coursemanagement.entity.enums.EnrollmentStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class EnrollmentFactory {
    public Enrollment createActiveEnrollment(Student student, Course course) {
        return Enrollment.builder()
                .student(student)
                .course(course)
                .enrolledAt(LocalDateTime.now())
                .status(EnrollmentStatus.ACTIVE)
                .progressPercent(0)
                .build();
    }
}
