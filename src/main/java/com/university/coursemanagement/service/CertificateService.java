package com.university.coursemanagement.service;

import com.university.coursemanagement.common.PageResponse;
import com.university.coursemanagement.dto.response.CertificateResponse;
import com.university.coursemanagement.entity.Enrollment;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CertificateService {
    CertificateResponse issueFor(Enrollment enrollment);

    CertificateResponse getByCode(String code);

    Optional<CertificateResponse> findByEnrollment(Long enrollmentId);

    PageResponse<CertificateResponse> getByStudent(Long studentId, Pageable pageable);
}
