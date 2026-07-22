package com.university.coursemanagement.repository;

import com.university.coursemanagement.entity.Course;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository cho Course. Ke thua {@link JpaSpecificationExecutor} de ho tro
 * loc dong (keyword, category, level, status, khoang gia) ket hop paging/sorting.
 */
@Repository
public interface CourseRepository extends JpaRepository<Course, Long>, JpaSpecificationExecutor<Course> {

    boolean existsByCategoryId(Long categoryId);

    boolean existsByInstructorId(Long instructorId);

    long countByCategoryId(Long categoryId);

    /** So khoa hoc do mot giang vien phu trach - dung count query thay vi nap collection LAZY. */
    long countByInstructorId(Long instructorId);

    /**
     * Nap Course kem khoa ghi bi quan (SELECT ... FOR UPDATE).
     *
     * <p>Dung khi ghi danh: viec dem so cho da chiem roi moi insert la hai buoc
     * rieng biet, khong co khoa thi hai request dong thoi cung doc thay "con cho"
     * va cung insert -> vuot suc chua. {@code @Version} KHONG chan duoc loi nay
     * vi no chi bao ve viec ghi de tren chinh ban ghi Course, khong bao ve dieu
     * kien dem tren bang enrollments. Khoa hang Course serialize cac giao dich
     * ghi danh cua cung mot khoa hoc.</p>
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Course c WHERE c.id = :id")
    Optional<Course> findByIdForUpdate(@Param("id") Long id);

    /**
     * Ghi de phuong thuc search cua {@link JpaSpecificationExecutor} de nap san
     * category va instructor trong cung mot truy van, tranh N+1 khi map sang DTO.
     */
    @Override
    @EntityGraph(attributePaths = {"category", "instructor"})
    Page<Course> findAll(Specification<Course> spec, Pageable pageable);
}
