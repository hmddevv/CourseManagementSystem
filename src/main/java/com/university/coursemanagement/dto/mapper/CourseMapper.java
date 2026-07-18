package com.university.coursemanagement.dto.mapper;

import com.university.coursemanagement.dto.response.CourseResponse;
import com.university.coursemanagement.entity.Course;
import org.springframework.stereotype.Component;

/**
 * Mapper cho Course. So bai hoc va so ghi danh dang hoat dong duoc truyen tu
 * Service (tinh bang count query) de tranh N+1 va lazy-loading ngoai transaction.
 */
@Component
public class CourseMapper {

    public CourseResponse toResponse(Course course, int lessonCount, long activeEnrollments) {
        long available = Math.max(0, course.getCapacity() - activeEnrollments);
        return new CourseResponse(
                course.getId(),
                course.getTitle(),
                course.getDescription(),
                course.getLevel(),
                course.getStatus(),
                course.getPrice(),
                course.getCapacity(),
                course.getDurationHours(),
                course.getCategory().getId(),
                course.getCategory().getName(),
                course.getInstructor().getId(),
                course.getInstructor().getFullName(),
                lessonCount,
                activeEnrollments,
                available,
                course.getCreatedAt(),
                course.getUpdatedAt()
        );
    }
}
