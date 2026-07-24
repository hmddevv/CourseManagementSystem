package com.university.coursemanagement.controller;

import com.university.coursemanagement.common.ApiResponse;
import com.university.coursemanagement.dto.request.LessonRequest;
import com.university.coursemanagement.dto.response.LessonResponse;
import com.university.coursemanagement.service.LessonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Tag(name = "Lessons", description = "Quản lý bài học trong khóa học")
public class LessonController {
    private final LessonService lessonService;

    public LessonController(LessonService lessonService) {
        this.lessonService = lessonService;
    }

    @PostMapping("/api/courses/{courseId}/lessons")
    @Operation(summary = "Thêm bài học vào khóa học")
    public ResponseEntity<ApiResponse<LessonResponse>> add(@PathVariable Long courseId,
                                                           @Valid @RequestBody LessonRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Thêm bài học thành công", lessonService.addLesson(courseId, request)));
    }

    @GetMapping("/api/courses/{courseId}/lessons")
    @Operation(summary = "Danh sách bài học của khóa học (theo thứ tự)")
    public ApiResponse<List<LessonResponse>> byCourse(@PathVariable Long courseId) {
        return ApiResponse.ok(lessonService.getLessonsByCourse(courseId));
    }

    @PutMapping("/api/lessons/{lessonId}")
    @Operation(summary = "Cập nhật bài học")
    public ApiResponse<LessonResponse> update(@PathVariable Long lessonId,
                                              @Valid @RequestBody LessonRequest request) {
        return ApiResponse.ok("Cập nhật thành công", lessonService.updateLesson(lessonId, request));
    }

    @DeleteMapping("/api/lessons/{lessonId}")
    @Operation(summary = "Xóa bài học")
    public ResponseEntity<Void> delete(@PathVariable Long lessonId) {
        lessonService.deleteLesson(lessonId);
        return ResponseEntity.noContent().build();
    }
}
