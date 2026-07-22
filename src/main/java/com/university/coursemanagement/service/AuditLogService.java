package com.university.coursemanagement.service;

import com.university.coursemanagement.common.PageResponse;
import com.university.coursemanagement.dto.response.AuditLogResponse;
import com.university.coursemanagement.entity.enums.AuditAction;
import org.springframework.data.domain.Pageable;

/** Ghi va tra cuu nhat ky thao tac. */
public interface AuditLogService {

    /** Ghi mot dong nhat ky. Duoc goi tu AuditAspect sau khi thao tac ghi thanh cong. */
    void record(String entityName, Long entityId, AuditAction action, String actor, String detail);

    /** Tra cuu nhat ky co phan trang, loc tuy chon theo thuc the va loai thao tac. */
    PageResponse<AuditLogResponse> search(String entityName, AuditAction action, Pageable pageable);
}
