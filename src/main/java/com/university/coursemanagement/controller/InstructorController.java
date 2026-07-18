package com.university.coursemanagement.controller;

import com.university.coursemanagement.common.ApiResponse;
import com.university.coursemanagement.common.PageResponse;
import com.university.coursemanagement.dto.request.InstructorRequest;
import com.university.coursemanagement.dto.response.InstructorResponse;
import com.university.coursemanagement.service.InstructorService;
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
@RequestMapping("/api/instructors")
@Tag(name = "Instructors", description = "Quan ly giang vien")
public class InstructorController {

    private final InstructorService instructorService;

    public InstructorController(InstructorService instructorService) {
        this.instructorService = instructorService;
    }

    @PostMapping
    @Operation(summary = "Them giang vien")
    public ResponseEntity<ApiResponse<InstructorResponse>> create(@Valid @RequestBody InstructorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Them giang vien thanh cong", instructorService.create(request)));
    }

    @GetMapping
    @Operation(summary = "Danh sach giang vien (phan trang)")
    public ApiResponse<PageResponse<InstructorResponse>> getAll(
            @PageableDefault(size = 10, sort = "fullName", direction = Sort.Direction.ASC) Pageable pageable) {
        return ApiResponse.ok(instructorService.getAll(pageable));
    }

    @GetMapping("/all")
    @Operation(summary = "Danh sach giang vien gon (cho dropdown)")
    public ApiResponse<List<InstructorResponse>> getAllSimple() {
        return ApiResponse.ok(instructorService.getAllSimple());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Chi tiet giang vien")
    public ApiResponse<InstructorResponse> getById(@PathVariable Long id) {
        return ApiResponse.ok(instructorService.getById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cap nhat giang vien")
    public ApiResponse<InstructorResponse> update(@PathVariable Long id,
                                                  @Valid @RequestBody InstructorRequest request) {
        return ApiResponse.ok("Cap nhat thanh cong", instructorService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xoa giang vien (chi khi khong phu trach khoa hoc)")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        instructorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
