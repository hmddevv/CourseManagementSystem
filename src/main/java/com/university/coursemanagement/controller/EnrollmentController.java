package com.university.coursemanagement.controller;

import com.university.coursemanagement.common.ApiResponse;
import com.university.coursemanagement.common.PageResponse;
import com.university.coursemanagement.dto.request.EnrollmentRequest;
import com.university.coursemanagement.dto.request.UpdateProgressRequest;
import com.university.coursemanagement.dto.response.EnrollmentResponse;
import com.university.coursemanagement.service.EnrollmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/enrollments")
@Tag(name = "Enrollments", description = "Ghi danh, huy, cap nhat tien do hoc tap")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @PostMapping
    @Operation(summary = "Ghi danh hoc vien vao khoa hoc")
    public ResponseEntity<ApiResponse<EnrollmentResponse>> enroll(@Valid @RequestBody EnrollmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Ghi danh thanh cong", enrollmentService.enroll(request)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Chi tiet ghi danh")
    public ApiResponse<EnrollmentResponse> getById(@PathVariable Long id) {
        return ApiResponse.ok(enrollmentService.getById(id));
    }

    @GetMapping
    @Operation(summary = "Danh sach ghi danh theo hoc vien HOAC khoa hoc (phan trang)")
    public ApiResponse<PageResponse<EnrollmentResponse>> list(
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Long courseId,
            @PageableDefault(size = 10, sort = "enrolledAt", direction = Sort.Direction.DESC) Pageable pageable) {
        if (studentId != null) {
            return ApiResponse.ok(enrollmentService.getByStudent(studentId, pageable));
        }
        if (courseId != null) {
            return ApiResponse.ok(enrollmentService.getByCourse(courseId, pageable));
        }
        throw new IllegalArgumentException("Can cung cap studentId hoac courseId");
    }

    @PatchMapping("/{id}/progress")
    @Operation(summary = "Cap nhat tien do (100% -> COMPLETED)")
    public ApiResponse<EnrollmentResponse> updateProgress(@PathVariable Long id,
                                                          @Valid @RequestBody UpdateProgressRequest request) {
        return ApiResponse.ok("Cap nhat tien do thanh cong", enrollmentService.updateProgress(id, request));
    }

    @PatchMapping("/{id}/cancel")
    @Operation(summary = "Huy ghi danh")
    public ApiResponse<EnrollmentResponse> cancel(@PathVariable Long id) {
        return ApiResponse.ok("Huy ghi danh thanh cong", enrollmentService.cancel(id));
    }
}
