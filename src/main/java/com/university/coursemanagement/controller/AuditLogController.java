package com.university.coursemanagement.controller;

import com.university.coursemanagement.common.ApiResponse;
import com.university.coursemanagement.common.PageResponse;
import com.university.coursemanagement.dto.response.AuditLogResponse;
import com.university.coursemanagement.entity.enums.AuditAction;
import com.university.coursemanagement.service.AuditLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/audit-logs")
@Tag(name = "Audit Logs", description = "Nhật ký thao tác hệ thống (chỉ đọc)")
public class AuditLogController {
    private final AuditLogService auditLogService;

    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @GetMapping
    @Operation(summary = "Danh sách nhật ký thao tác (phân trang, lọc theo thực thể và hành động)")
    public ApiResponse<PageResponse<AuditLogResponse>> search(
            @RequestParam(required = false) String entityName,
            @RequestParam(required = false) AuditAction action,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ApiResponse.ok(auditLogService.search(entityName, action, pageable));
    }
}
