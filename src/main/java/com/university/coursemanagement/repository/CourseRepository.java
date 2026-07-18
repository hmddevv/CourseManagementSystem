package com.university.coursemanagement.repository;

import com.university.coursemanagement.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Repository cho Course. Ke thua {@link JpaSpecificationExecutor} de ho tro
 * loc dong (keyword, category, level, status, khoang gia) ket hop paging/sorting.
 */
@Repository
public interface CourseRepository extends JpaRepository<Course, Long>, JpaSpecificationExecutor<Course> {

    boolean existsByCategoryId(Long categoryId);

    boolean existsByInstructorId(Long instructorId);

    long countByCategoryId(Long categoryId);
}
