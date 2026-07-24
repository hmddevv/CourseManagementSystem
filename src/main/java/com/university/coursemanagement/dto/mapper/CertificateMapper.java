package com.university.coursemanagement.dto.mapper;

import com.university.coursemanagement.dto.response.CertificateResponse;
import com.university.coursemanagement.entity.Certificate;
import org.springframework.stereotype.Component;

@Component
public class CertificateMapper {
    public CertificateResponse toResponse(Certificate certificate) {
        return new CertificateResponse(
                certificate.getId(),
                certificate.getCode(),
                certificate.getEnrollment().getId(),
                certificate.getEnrollment().getStudent().getId(),
                certificate.getStudentName(),
                certificate.getEnrollment().getCourse().getId(),
                certificate.getCourseTitle(),
                certificate.getIssuedAt()
        );
    }
}
