package com.university.coursemanagement.service;

import com.university.coursemanagement.common.PageResponse;
import com.university.coursemanagement.dto.response.AuditLogResponse;
import com.university.coursemanagement.entity.enums.AuditAction;
import org.springframework.data.domain.Pageable;

public interface AuditLogService {
    void record(String entityName, Long entityId, AuditAction action, String actor, String detail);

    PageResponse<AuditLogResponse> search(String entityName, AuditAction action, Pageable pageable);
}
