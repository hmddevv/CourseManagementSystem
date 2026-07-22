package com.university.coursemanagement.controller;

import com.university.coursemanagement.common.ApiResponse;
import com.university.coursemanagement.common.PageResponse;
import com.university.coursemanagement.dto.response.CertificateResponse;
import com.university.coursemanagement.service.CertificateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Tra cuu chung chi. Khong co endpoint tao chung chi: chung chi duoc cap
 * tu dong khi hoc vien hoan thanh 100% tien do, khong cap thu cong duoc.
 */
@RestController
@RequestMapping("/api/certificates")
@Tag(name = "Certificates", description = "Tra cuu chung chi hoan thanh khoa hoc")
public class CertificateController {

    private final CertificateService certificateService;

    public CertificateController(CertificateService certificateService) {
        this.certificateService = certificateService;
    }

    @GetMapping("/{code}")
    @Operation(summary = "Tra cuu chung chi theo ma")
    public ApiResponse<CertificateResponse> getByCode(@PathVariable String code) {
        return ApiResponse.ok(certificateService.getByCode(code));
    }

    @GetMapping("/students/{studentId}")
    @Operation(summary = "Danh sach chung chi cua mot hoc vien (phan trang)")
    public ApiResponse<PageResponse<CertificateResponse>> getByStudent(
            @PathVariable Long studentId,
            @PageableDefault(size = 10, sort = "issuedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ApiResponse.ok(certificateService.getByStudent(studentId, pageable));
    }

    @GetMapping("/enrollments/{enrollmentId}")
    @Operation(summary = "Chung chi cua mot ghi danh (404 neu chua hoan thanh)")
    public ResponseEntity<ApiResponse<CertificateResponse>> getByEnrollment(@PathVariable Long enrollmentId) {
        return certificateService.findByEnrollment(enrollmentId)
                .map(certificate -> ResponseEntity.ok(ApiResponse.ok(certificate)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
