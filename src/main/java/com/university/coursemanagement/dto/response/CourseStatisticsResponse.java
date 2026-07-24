package com.university.coursemanagement.dto.response;

import java.util.List;

public record CourseStatisticsResponse(
        long totalCourses,
        long publishedCourses,
        long draftCourses,
        long totalStudents,
        long totalActiveEnrollments,
        List<PopularCourse> popularCourses
) {
    public record PopularCourse(
            Long courseId,
            String title,
            long activeEnrollments
    ) {
    }
}
