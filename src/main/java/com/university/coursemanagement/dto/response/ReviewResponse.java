package com.university.coursemanagement.dto.response;

import java.time.LocalDateTime;

public record ReviewResponse(
        Long id,
        Long courseId,
        Long studentId,
        String studentName,
        Integer rating,
        String comment,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
