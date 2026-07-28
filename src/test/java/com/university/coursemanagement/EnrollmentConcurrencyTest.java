package com.university.coursemanagement;

import com.university.coursemanagement.dto.request.EnrollmentRequest;
import com.university.coursemanagement.entity.Category;
import com.university.coursemanagement.entity.Course;
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
import com.university.coursemanagement.service.EnrollmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:concurrencydb;DB_CLOSE_DELAY=-1;MODE=MySQL;LOCK_TIMEOUT=20000",
        "spring.datasource.hikari.maximum-pool-size=16"
})
@ActiveProfiles("test")
class EnrollmentConcurrencyTest {
    private static final int THREADS = 8;

    @Autowired EnrollmentService enrollmentService;
    @Autowired CategoryRepository categoryRepository;
    @Autowired InstructorRepository instructorRepository;
    @Autowired StudentRepository studentRepository;
    @Autowired CourseRepository courseRepository;
    @Autowired EnrollmentRepository enrollmentRepository;

    @Test
    void enroll_shouldRespectCapacity_whenManyStudentsRaceForTheLastSeat() throws Exception {
        Course course = givenPublishedCourseWithCapacity(1);
        List<Student> students = givenStudents(THREADS);

        AtomicInteger succeeded = new AtomicInteger();
        AtomicInteger rejected = new AtomicInteger();

        CountDownLatch startGate = new CountDownLatch(1);
        CountDownLatch finishGate = new CountDownLatch(THREADS);
        ExecutorService pool = Executors.newFixedThreadPool(THREADS);

        for (Student student : students) {
            pool.submit(() -> {
                try {
                    startGate.await();
                    enrollmentService.enroll(new EnrollmentRequest(student.getId(), course.getId()));
                    succeeded.incrementAndGet();
                } catch (Exception e) {
                    rejected.incrementAndGet();
                } finally {
                    finishGate.countDown();
                }
            });
        }

        startGate.countDown();
        assertThat(finishGate.await(60, TimeUnit.SECONDS)).isTrue();
        pool.shutdown();

        long active = enrollmentRepository.countByCourseIdAndStatus(course.getId(), EnrollmentStatus.ACTIVE);

        assertThat(succeeded.get()).isEqualTo(1);
        assertThat(rejected.get()).isEqualTo(THREADS - 1);
        assertThat(active).isEqualTo(1);
    }

    private Course givenPublishedCourseWithCapacity(int capacity) {
        Category category = categoryRepository.save(
                Category.builder().name("Race-Cat-" + System.nanoTime()).build());
        Instructor instructor = instructorRepository.save(Instructor.builder()
                .fullName("GV Race").email("gv-race-" + System.nanoTime() + "@t.edu").build());

        Course course = Course.builder()
                .title("Khoa hoc chi con 1 cho")
                .level(CourseLevel.BEGINNER)
                .status(CourseStatus.DRAFT)
                .price(BigDecimal.ZERO)
                .capacity(capacity)
                .category(category)
                .instructor(instructor)
                .build();
        course.addLesson(Lesson.builder().title("Bai 1").orderIndex(1).build());
        course.setStatus(CourseStatus.PUBLISHED);
        return courseRepository.save(course);
    }

    private List<Student> givenStudents(int count) {
        List<Student> students = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            students.add(studentRepository.save(Student.builder()
                    .fullName("Hoc vien " + i)
                    .email("race-%d-%d@t.edu".formatted(i, System.nanoTime()))
                    .build()));
        }
        return students;
    }
}
