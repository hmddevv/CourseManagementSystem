package com.university.coursemanagement.dto.mapper;

import com.university.coursemanagement.dto.response.CourseResponse;
import com.university.coursemanagement.entity.Course;
import org.springframework.stereotype.Component;

/**
 * Mapper cho Course. So bai hoc, so ghi danh dang hoat dong va diem danh gia deu
 * duoc truyen tu Service (tinh bang truy van gop) de tranh N+1 va lazy-loading
 * ngoai transaction.
 */
@Component
public class CourseMapper {

    public CourseResponse toResponse(Course course, int lessonCount, long activeEnrollments,
                                     double averageRating, long reviewCount) {
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
                averageRating,
                reviewCount,
                course.getCreatedAt(),
                course.getUpdatedAt()
        );
    }
}
