package com.university.coursemanagement.dto.response;

public record LessonResponse(
        Long id,
        String title,
        String content,
        Integer orderIndex,
        Integer durationMinutes,
        Long courseId
) {
}
