package com.university.coursemanagement.service.impl;

import com.university.coursemanagement.dto.mapper.LessonMapper;
import com.university.coursemanagement.dto.request.LessonRequest;
import com.university.coursemanagement.dto.response.LessonResponse;
import com.university.coursemanagement.entity.Course;
import com.university.coursemanagement.entity.Lesson;
import com.university.coursemanagement.exception.BusinessException;
import com.university.coursemanagement.exception.ResourceNotFoundException;
import com.university.coursemanagement.repository.CourseRepository;
import com.university.coursemanagement.repository.LessonRepository;
import com.university.coursemanagement.service.LessonService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class LessonServiceImpl implements LessonService {
    private final LessonRepository lessonRepository;
    private final CourseRepository courseRepository;
    private final LessonMapper lessonMapper;

    public LessonServiceImpl(LessonRepository lessonRepository,
                             CourseRepository courseRepository,
                             LessonMapper lessonMapper) {
        this.lessonRepository = lessonRepository;
        this.courseRepository = courseRepository;
        this.lessonMapper = lessonMapper;
    }

    @Override
    @Transactional
    public LessonResponse addLesson(Long courseId, LessonRequest request) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> ResourceNotFoundException.of("khóa học", courseId));
        if (lessonRepository.existsByCourseIdAndOrderIndex(courseId, request.orderIndex())) {
            throw new BusinessException(
                    "Bài học thứ %d đã tồn tại trong khóa học này.".formatted(request.orderIndex()));
        }
        Lesson lesson = lessonMapper.toEntity(request);
        course.addLesson(lesson);
        Lesson saved = lessonRepository.save(lesson);
        return lessonMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public LessonResponse updateLesson(Long lessonId, LessonRequest request) {
        Lesson lesson = findOrThrow(lessonId);
        Long courseId = lesson.getCourse().getId();
        if (!lesson.getOrderIndex().equals(request.orderIndex())
                && lessonRepository.existsByCourseIdAndOrderIndex(courseId, request.orderIndex())) {
            throw new BusinessException(
                    "Bài học thứ %d đã tồn tại trong khóa học này.".formatted(request.orderIndex()));
        }
        lessonMapper.updateEntity(lesson, request);
        return lessonMapper.toResponse(lesson);
    }

    @Override
    public List<LessonResponse> getLessonsByCourse(Long courseId) {
        if (!courseRepository.existsById(courseId)) {
            throw ResourceNotFoundException.of("khóa học", courseId);
        }
        return lessonRepository.findByCourseIdOrderByOrderIndexAsc(courseId).stream()
                .map(lessonMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void deleteLesson(Long lessonId) {
        Lesson lesson = findOrThrow(lessonId);
        lessonRepository.delete(lesson);
    }

    private Lesson findOrThrow(Long lessonId) {
        return lessonRepository.findById(lessonId)
                .orElseThrow(() -> ResourceNotFoundException.of("bài học", lessonId));
    }
}
