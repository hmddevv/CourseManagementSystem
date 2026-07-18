package com.university.coursemanagement.controller;

import com.university.coursemanagement.common.ApiResponse;
import com.university.coursemanagement.common.PageResponse;
import com.university.coursemanagement.dto.request.StudentRequest;
import com.university.coursemanagement.dto.response.StudentResponse;
import com.university.coursemanagement.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@Tag(name = "Students", description = "Quan ly hoc vien")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping
    @Operation(summary = "Them hoc vien")
    public ResponseEntity<ApiResponse<StudentResponse>> create(@Valid @RequestBody StudentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Them hoc vien thanh cong", studentService.create(request)));
    }

    @GetMapping
    @Operation(summary = "Danh sach hoc vien (phan trang)")
    public ApiResponse<PageResponse<StudentResponse>> getAll(
            @PageableDefault(size = 10, sort = "fullName", direction = Sort.Direction.ASC) Pageable pageable) {
        return ApiResponse.ok(studentService.getAll(pageable));
    }

    @GetMapping("/all")
    @Operation(summary = "Danh sach hoc vien gon (cho dropdown)")
    public ApiResponse<List<StudentResponse>> getAllSimple() {
        return ApiResponse.ok(studentService.getAllSimple());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Chi tiet hoc vien")
    public ApiResponse<StudentResponse> getById(@PathVariable Long id) {
        return ApiResponse.ok(studentService.getById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cap nhat hoc vien")
    public ApiResponse<StudentResponse> update(@PathVariable Long id,
                                               @Valid @RequestBody StudentRequest request) {
        return ApiResponse.ok("Cap nhat thanh cong", studentService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xoa hoc vien")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        studentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
