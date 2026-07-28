package com.university.coursemanagement;

import com.university.coursemanagement.entity.Category;
import com.university.coursemanagement.entity.Course;
import com.university.coursemanagement.entity.Enrollment;
import com.university.coursemanagement.entity.Instructor;
import com.university.coursemanagement.entity.Lesson;
import com.university.coursemanagement.entity.Student;
import com.university.coursemanagement.entity.enums.CourseLevel;
import com.university.coursemanagement.entity.enums.CourseStatus;
import com.university.coursemanagement.entity.enums.EnrollmentStatus;
import com.university.coursemanagement.repository.CategoryRepository;
import com.university.coursemanagement.repository.CourseRepository;
import com.university.coursemanagement.repository.EnrollmentRepository;
import com.university.coursemanagement.repository.InstructorRepository;
import com.university.coursemanagement.repository.StudentRepository;
import com.university.coursemanagement.scheduler.EnrollmentReminderScheduler;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = "app.reminder.inactive-days=7")
@ActiveProfiles("test")
class EnrollmentReminderSchedulerTest {
    @Autowired EnrollmentReminderScheduler scheduler;
    @Autowired EnrollmentRepository enrollmentRepository;
    @Autowired CategoryRepository categoryRepository;
    @Autowired InstructorRepository instructorRepository;
    @Autowired StudentRepository studentRepository;
    @Autowired CourseRepository courseRepository;
    @Autowired TransactionTemplate transactionTemplate;
    @PersistenceContext EntityManager entityManager;

    @Test
    void findInactiveEnrollments_shouldOnlyReturnStaleActiveEnrollments() {
        Enrollment fresh = givenEnrollment(EnrollmentStatus.ACTIVE, LocalDateTime.now());
        Enrollment stale = givenEnrollment(EnrollmentStatus.ACTIVE, LocalDateTime.now().minusDays(30));
        Enrollment staleButCompleted = givenEnrollment(EnrollmentStatus.COMPLETED, LocalDateTime.now().minusDays(30));

        var inactive = scheduler.findInactiveEnrollments();
        var ids = inactive.stream().map(Enrollment::getId).toList();

        assertThat(ids).contains(stale.getId());
        assertThat(ids).doesNotContain(fresh.getId());
        assertThat(ids).doesNotContain(staleButCompleted.getId());
    }

    @Test
    void remindInactiveStudents_shouldRunWithoutError() {
        givenEnrollment(EnrollmentStatus.ACTIVE, LocalDateTime.now().minusDays(30));

        scheduler.remindInactiveStudents();
    }

    private Enrollment givenEnrollment(EnrollmentStatus status, LocalDateTime updatedAt) {
        Category category = categoryRepository.save(
                Category.builder().name("Rem-Cat-" + System.nanoTime()).build());
        Instructor instructor = instructorRepository.save(Instructor.builder()
                .fullName("GV Rem").email("gv-rem-" + System.nanoTime() + "@t.edu").build());
        Course course = Course.builder()
                .title("Khoa hoc nhac hoc").level(CourseLevel.BEGINNER).status(CourseStatus.DRAFT)
                .price(BigDecimal.ZERO).capacity(50).category(category).instructor(instructor).build();
        course.addLesson(Lesson.builder().title("Bai 1").orderIndex(1).build());
        course.setStatus(CourseStatus.PUBLISHED);
        course = courseRepository.save(course);

        Student student = studentRepository.save(Student.builder()
                .fullName("Hoc vien nhac hoc")
                .email("rem-" + System.nanoTime() + "@t.edu").build());

        Enrollment enrollment = enrollmentRepository.save(Enrollment.builder()
                .student(student).course(course)
                .enrolledAt(LocalDateTime.now().minusDays(40))
                .status(status).progressPercent(20).build());

        forceUpdatedAt(enrollment.getId(), updatedAt);
        return enrollment;
    }

    private void forceUpdatedAt(Long enrollmentId, LocalDateTime updatedAt) {
        transactionTemplate.executeWithoutResult(status ->
                entityManager.createQuery("UPDATE Enrollment e SET e.updatedAt = :ts WHERE e.id = :id")
                        .setParameter("ts", updatedAt)
                        .setParameter("id", enrollmentId)
                        .executeUpdate());
        entityManager.clear();
    }
}
