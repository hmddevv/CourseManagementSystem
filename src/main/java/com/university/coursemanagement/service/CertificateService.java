package com.university.coursemanagement.service;

import com.university.coursemanagement.common.PageResponse;
import com.university.coursemanagement.dto.response.CertificateResponse;
import com.university.coursemanagement.entity.Enrollment;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/** Cap va tra cuu chung chi hoan thanh khoa hoc. */
public interface CertificateService {

    /**
     * Cap chung chi cho mot ghi danh da hoan thanh.
     * Goi lai nhieu lan tren cung mot ghi danh se tra ve chung chi da co, khong tao ban ghi trung.
     */
    CertificateResponse issueFor(Enrollment enrollment);

    /** Tra cuu chung chi theo ma. */
    CertificateResponse getByCode(String code);

    /** Chung chi cua mot ghi danh, rong neu chua duoc cap. */
    Optional<CertificateResponse> findByEnrollment(Long enrollmentId);

    /** Danh sach chung chi cua mot hoc vien, co phan trang. */
    PageResponse<CertificateResponse> getByStudent(Long studentId, Pageable pageable);
}
