package com.university.coursemanagement.dto.response;

import com.university.coursemanagement.entity.enums.CourseLevel;
import com.university.coursemanagement.entity.enums.CourseStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Du lieu tra ve cho khoa hoc, da lam giau them thong tin dan xuat
 * (ten danh muc, ten giang vien, so bai hoc, so cho con trong).
 */
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
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
