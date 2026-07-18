package com.university.coursemanagement.dto.response;

import com.university.coursemanagement.entity.enums.EnrollmentStatus;

import java.time.LocalDateTime;

public record EnrollmentResponse(
        Long id,
        Long studentId,
        String studentName,
        Long courseId,
        String courseTitle,
        EnrollmentStatus status,
        Integer progressPercent,
        LocalDateTime enrolledAt,
        LocalDateTime completedAt
) {
}
