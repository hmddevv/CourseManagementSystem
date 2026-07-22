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

    /**
     * Cap chung chi. Ham nay <b>idempotent</b>: goi lai tren cung mot ghi danh
     * tra ve chung chi da co thay vi nem loi hay tao ban ghi thu hai. Nho vay
     * viec cap nhat tien do len 100% nhieu lan khong sinh chung chi trung.
     *
     * <p>Khong mo transaction rieng - chay trong transaction cua ham goi
     * (cap nhat tien do), nen neu buoc nao do that bai thi chung chi cung
     * duoc cuon nguoc theo.</p>
     */
    @Override
    @Transactional
    public CertificateResponse issueFor(Enrollment enrollment) {
        if (enrollment.getStatus() != EnrollmentStatus.COMPLETED) {
            throw new BusinessException("Chi cap chung chi khi ghi danh da hoan thanh 100%.");
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
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay chung chi co ma '%s'".formatted(code)));
    }

    @Override
    public Optional<CertificateResponse> findByEnrollment(Long enrollmentId) {
        return certificateRepository.findByEnrollmentId(enrollmentId).map(certificateMapper::toResponse);
    }

    @Override
    public PageResponse<CertificateResponse> getByStudent(Long studentId, Pageable pageable) {
        if (!studentRepository.existsById(studentId)) {
            throw ResourceNotFoundException.of("hoc vien", studentId);
        }
        return PageResponse.from(
                certificateRepository.findByEnrollmentStudentId(studentId, pageable).map(certificateMapper::toResponse));
    }
}
