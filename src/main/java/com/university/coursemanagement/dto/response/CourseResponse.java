package com.university.coursemanagement.dto.response;

import com.university.coursemanagement.entity.enums.CourseLevel;
import com.university.coursemanagement.entity.enums.CourseStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CourseResponse(
        Long id,
        String title,
        String description,
        CourseLevel level,
        CourseStatus status,
        BigDecimal price,
        Integer capacity,
        Integer durationHours,
        Long categoryId,
        String categoryName,
        Long instructorId,
        String instructorName,
        int lessonCount,
        long activeEnrollments,
        long availableSlots,
        double averageRating,
        long reviewCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
