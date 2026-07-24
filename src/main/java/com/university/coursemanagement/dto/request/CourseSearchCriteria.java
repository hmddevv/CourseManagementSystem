package com.university.coursemanagement.dto.request;

import com.university.coursemanagement.entity.enums.CourseLevel;
import com.university.coursemanagement.entity.enums.CourseStatus;

import java.math.BigDecimal;

public record CourseSearchCriteria(
        String keyword,
        Long categoryId,
        Long instructorId,
        CourseLevel level,
        CourseStatus status,
        BigDecimal minPrice,
        BigDecimal maxPrice
) {
}
