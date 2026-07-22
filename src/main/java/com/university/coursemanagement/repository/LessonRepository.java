package com.university.coursemanagement.repository;

import com.university.coursemanagement.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {

    List<Lesson> findByCourseIdOrderByOrderIndexAsc(Long courseId);

    long countByCourseId(Long courseId);

    boolean existsByCourseIdAndOrderIndex(Long courseId, Integer orderIndex);

    /**
     * Dem so bai hoc cho NHIEU khoa hoc trong MOT truy van (GROUP BY).
     * Dung khi tra ve danh sach khoa hoc: thay vi goi countByCourseId cho tung
     * dong (N truy van), chi con dung 1 truy van cho ca trang.
     */
    @Query("""
            SELECT l.course.id AS courseId, COUNT(l) AS total
            FROM Lesson l
            WHERE l.course.id IN :courseIds
            GROUP BY l.course.id
            """)
    List<CourseCount> countGroupedByCourseIds(@Param("courseIds") Collection<Long> courseIds);

    /** Projection cho ket qua dem theo khoa hoc. */
    interface CourseCount {
        Long getCourseId();
        Long getTotal();
    }
}
