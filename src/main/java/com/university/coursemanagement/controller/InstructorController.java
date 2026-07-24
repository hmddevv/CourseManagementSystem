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
@Tag(name = "Instructors", description = "Quản lý giảng viên")
public class InstructorController {
    private final InstructorService instructorService;

    public InstructorController(InstructorService instructorService) {
        this.instructorService = instructorService;
    }

    @PostMapping
    @Operation(summary = "Thêm giảng viên")
    public ResponseEntity<ApiResponse<InstructorResponse>> create(@Valid @RequestBody InstructorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Thêm giảng viên thành công", instructorService.create(request)));
    }

    @GetMapping
    @Operation(summary = "Danh sách giảng viên (phân trang)")
    public ApiResponse<PageResponse<InstructorResponse>> getAll(
            @PageableDefault(size = 10, sort = "fullName", direction = Sort.Direction.ASC) Pageable pageable) {
        return ApiResponse.ok(instructorService.getAll(pageable));
    }

    @GetMapping("/all")
    @Operation(summary = "Danh sách giảng viên gọn (cho dropdown)")
    public ApiResponse<List<InstructorResponse>> getAllSimple() {
        return ApiResponse.ok(instructorService.getAllSimple());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Chi tiết giảng viên")
    public ApiResponse<InstructorResponse> getById(@PathVariable Long id) {
        return ApiResponse.ok(instructorService.getById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật giảng viên")
    public ApiResponse<InstructorResponse> update(@PathVariable Long id,
                                                  @Valid @RequestBody InstructorRequest request) {
        return ApiResponse.ok("Cập nhật thành công", instructorService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa giảng viên (chỉ khi không phụ trách khóa học)")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        instructorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
