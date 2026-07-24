package com.university.coursemanagement.service;

import com.university.coursemanagement.dto.request.LessonRequest;
import com.university.coursemanagement.dto.response.LessonResponse;

import java.util.List;

public interface LessonService {
    LessonResponse addLesson(Long courseId, LessonRequest request);

    LessonResponse updateLesson(Long lessonId, LessonRequest request);

    List<LessonResponse> getLessonsByCourse(Long courseId);

    void deleteLesson(Long lessonId);
}
