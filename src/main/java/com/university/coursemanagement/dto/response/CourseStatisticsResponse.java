package com.university.coursemanagement.dto.response;

import java.util.List;

/** Tong hop thong ke cho dashboard (tinh nang nang cao). */
public record CourseStatisticsResponse(
        long totalCourses,
        long publishedCourses,
        long draftCourses,
        long totalStudents,
        long totalActiveEnrollments,
        List<PopularCourse> popularCourses
) {
    /** Khoa hoc pho bien nhat theo so ghi danh dang hoat dong. */
    public record PopularCourse(
            Long courseId,
            String title,
            long activeEnrollments
    ) {
    }
}
