package com.university.coursemanagement.dto.mapper;

import com.university.coursemanagement.dto.request.LessonRequest;
import com.university.coursemanagement.dto.response.LessonResponse;
import com.university.coursemanagement.entity.Lesson;
import org.springframework.stereotype.Component;

@Component
public class LessonMapper {

    public Lesson toEntity(LessonRequest request) {
        return Lesson.builder()
                .title(request.title())
                .content(request.content())
                .orderIndex(request.orderIndex())
                .durationMinutes(request.durationMinutes())
                .build();
    }

    public void updateEntity(Lesson lesson, LessonRequest request) {
        lesson.setTitle(request.title());
        lesson.setContent(request.content());
        lesson.setOrderIndex(request.orderIndex());
        lesson.setDurationMinutes(request.durationMinutes());
    }

    public LessonResponse toResponse(Lesson lesson) {
        return new LessonResponse(
                lesson.getId(),
                lesson.getTitle(),
                lesson.getContent(),
                lesson.getOrderIndex(),
                lesson.getDurationMinutes(),
                lesson.getCourse().getId()
        );
    }
}
