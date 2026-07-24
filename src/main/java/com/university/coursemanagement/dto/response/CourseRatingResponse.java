package com.university.coursemanagement.dto.response;

public record CourseRatingResponse(
        Long courseId,
        String title,
        String categoryName,
        String instructorName,
        double averageRating,
        long reviewCount
) {
}
