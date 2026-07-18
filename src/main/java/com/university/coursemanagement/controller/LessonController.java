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
@Tag(name = "Lessons", description = "Quan ly bai hoc trong khoa hoc")
public class LessonController {

    private final LessonService lessonService;

    public LessonController(LessonService lessonService) {
        this.lessonService = lessonService;
    }

    @PostMapping("/api/courses/{courseId}/lessons")
    @Operation(summary = "Them bai hoc vao khoa hoc")
    public ResponseEntity<ApiResponse<LessonResponse>> add(@PathVariable Long courseId,
                                                           @Valid @RequestBody LessonRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Them bai hoc thanh cong", lessonService.addLesson(courseId, request)));
    }

    @GetMapping("/api/courses/{courseId}/lessons")
    @Operation(summary = "Danh sach bai hoc cua khoa hoc (theo thu tu)")
    public ApiResponse<List<LessonResponse>> byCourse(@PathVariable Long courseId) {
        return ApiResponse.ok(lessonService.getLessonsByCourse(courseId));
    }

    @PutMapping("/api/lessons/{lessonId}")
    @Operation(summary = "Cap nhat bai hoc")
    public ApiResponse<LessonResponse> update(@PathVariable Long lessonId,
                                              @Valid @RequestBody LessonRequest request) {
        return ApiResponse.ok("Cap nhat thanh cong", lessonService.updateLesson(lessonId, request));
    }

    @DeleteMapping("/api/lessons/{lessonId}")
    @Operation(summary = "Xoa bai hoc")
    public ResponseEntity<Void> delete(@PathVariable Long lessonId) {
        lessonService.deleteLesson(lessonId);
        return ResponseEntity.noContent().build();
    }
}
