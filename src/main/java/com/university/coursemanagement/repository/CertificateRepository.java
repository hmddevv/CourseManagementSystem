package com.university.coursemanagement.repository;

import com.university.coursemanagement.entity.Certificate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long> {

    boolean existsByEnrollmentId(Long enrollmentId);

    Optional<Certificate> findByCode(String code);

    Optional<Certificate> findByEnrollmentId(Long enrollmentId);

    boolean existsByCode(String code);

    /** Chung chi cua mot hoc vien; nap san ghi danh de tranh N+1 khi map DTO. */
    @EntityGraph(attributePaths = {"enrollment"})
    Page<Certificate> findByEnrollmentStudentId(Long studentId, Pageable pageable);
}
