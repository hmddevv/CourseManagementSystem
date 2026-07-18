package com.university.coursemanagement.dto.request;

import com.university.coursemanagement.entity.enums.CourseLevel;
import com.university.coursemanagement.entity.enums.CourseStatus;

import java.math.BigDecimal;

/**
 * Tieu chi loc khoa hoc (tat ca deu tuy chon). Duoc chuyen thanh cac
 * Specification ghep lai, ket hop voi Paging/Sorting o tang controller.
 */
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
