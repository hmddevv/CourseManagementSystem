package com.university.coursemanagement.service.impl;

import com.university.coursemanagement.common.PageResponse;
import com.university.coursemanagement.dto.mapper.CertificateMapper;
import com.university.coursemanagement.dto.response.CertificateResponse;
import com.university.coursemanagement.entity.Certificate;
import com.university.coursemanagement.entity.Enrollment;
import com.university.coursemanagement.entity.enums.EnrollmentStatus;
import com.university.coursemanagement.exception.BusinessException;
import com.university.coursemanagement.exception.ResourceNotFoundException;
import com.university.coursemanagement.factory.CertificateFactory;
import com.university.coursemanagement.repository.CertificateRepository;
import com.university.coursemanagement.repository.StudentRepository;
import com.university.coursemanagement.service.CertificateService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class CertificateServiceImpl implements CertificateService {
    private final CertificateRepository certificateRepository;
    private final StudentRepository studentRepository;
    private final CertificateFactory certificateFactory;
    private final CertificateMapper certificateMapper;

    public CertificateServiceImpl(CertificateRepository certificateRepository,
                                  StudentRepository studentRepository,
                                  CertificateFactory certificateFactory,
                                  CertificateMapper certificateMapper) {
        this.certificateRepository = certificateRepository;
        this.studentRepository = studentRepository;
        this.certificateFactory = certificateFactory;
        this.certificateMapper = certificateMapper;
    }

    @Override
    @Transactional
    public CertificateResponse issueFor(Enrollment enrollment) {
        if (enrollment.getStatus() != EnrollmentStatus.COMPLETED) {
            throw new BusinessException("Chỉ cấp chứng chỉ khi ghi danh đã hoàn thành 100%.");
        }
        return certificateRepository.findByEnrollmentId(enrollment.getId())
                .map(certificateMapper::toResponse)
                .orElseGet(() -> {
                    Certificate saved = certificateRepository.save(certificateFactory.createFor(enrollment));
                    return certificateMapper.toResponse(saved);
                });
    }

    @Override
    public CertificateResponse getByCode(String code) {
        return certificateRepository.findByCode(code)
                .map(certificateMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy chứng chỉ có mã '%s'".formatted(code)));
    }

    @Override
    public Optional<CertificateResponse> findByEnrollment(Long enrollmentId) {
        return certificateRepository.findByEnrollmentId(enrollmentId).map(certificateMapper::toResponse);
    }

    @Override
    public PageResponse<CertificateResponse> getByStudent(Long studentId, Pageable pageable) {
        if (!studentRepository.existsById(studentId)) {
            throw ResourceNotFoundException.of("học viên", studentId);
        }
        return PageResponse.from(
                certificateRepository.findByEnrollmentStudentId(studentId, pageable).map(certificateMapper::toResponse));
    }
}
