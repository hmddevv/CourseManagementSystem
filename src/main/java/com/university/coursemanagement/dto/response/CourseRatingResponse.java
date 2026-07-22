package com.university.coursemanagement.dto.response;

/** Mot dong trong bang xep hang khoa hoc theo diem danh gia. */
public record CourseRatingResponse(
        Long courseId,
        String title,
        String categoryName,
        String instructorName,
        double averageRating,
        long reviewCount
) {
}
