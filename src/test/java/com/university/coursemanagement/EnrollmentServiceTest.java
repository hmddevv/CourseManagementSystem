package com.university.coursemanagement;

import com.university.coursemanagement.dto.request.EnrollmentRequest;
import com.university.coursemanagement.dto.request.UpdateProgressRequest;
import com.university.coursemanagement.entity.Category;
import com.university.coursemanagement.entity.Course;
import com.university.coursemanagement.entity.Instructor;
import com.university.coursemanagement.entity.Lesson;
import com.university.coursemanagement.entity.Student;
import com.university.coursemanagement.entity.enums.CourseLevel;
import com.university.coursemanagement.entity.enums.CourseStatus;
import com.university.coursemanagement.entity.enums.EnrollmentStatus;
import com.university.coursemanagement.exception.BusinessException;
import com.university.coursemanagement.repository.CategoryRepository;
import com.university.coursemanagement.repository.CertificateRepository;
import com.university.coursemanagement.repository.CourseRepository;
import com.university.coursemanagement.repository.InstructorRepository;
import com.university.coursemanagement.repository.StudentRepository;
import com.university.coursemanagement.service.EnrollmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
class EnrollmentServiceTest {
    @Autowired EnrollmentService enrollmentService;
    @Autowired CertificateRepository certificateRepository;
    @Autowired CategoryRepository categoryRepository;
    @Autowired InstructorRepository instructorRepository;
    @Autowired StudentRepository studentRepository;
    @Autowired CourseRepository courseRepository;

    @Test
    void enroll_shouldFail_whenCourseIsFull() {
        Category cat = categoryRepository.save(Category.builder().name("Test-Cat-" + System.nanoTime()).build());
        Instructor ins = instructorRepository.save(Instructor.builder()
                .fullName("GV Test").email("gv-" + System.nanoTime() + "@t.edu").build());

        Course course = Course.builder()
                .title("Khoa day").level(CourseLevel.BEGINNER).status(CourseStatus.DRAFT)
                .price(BigDecimal.ZERO).capacity(1).category(cat).instructor(ins).build();
        course.addLesson(Lesson.builder().title("B1").orderIndex(1).build());
        course.setStatus(CourseStatus.PUBLISHED);
        course = courseRepository.save(course);

        Student s1 = studentRepository.save(Student.builder()
                .fullName("HV1").email("hv1-" + System.nanoTime() + "@t.edu").build());
        Student s2 = studentRepository.save(Student.builder()
                .fullName("HV2").email("hv2-" + System.nanoTime() + "@t.edu").build());

        var ok = enrollmentService.enroll(new EnrollmentRequest(s1.getId(), course.getId()));
        assertThat(ok.id()).isNotNull();

        Long courseId = course.getId();
        assertThatThrownBy(() -> enrollmentService.enroll(new EnrollmentRequest(s2.getId(), courseId)))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("đã đầy");
    }

    @Test
    void enroll_shouldFail_whenCourseNotPublished() {
        Category cat = categoryRepository.save(Category.builder().name("Cat-" + System.nanoTime()).build());
        Instructor ins = instructorRepository.save(Instructor.builder()
                .fullName("GV").email("gv2-" + System.nanoTime() + "@t.edu").build());
        Course draft = courseRepository.save(Course.builder()
                .title("Khoa nhap").level(CourseLevel.BEGINNER).status(CourseStatus.DRAFT)
                .price(BigDecimal.ZERO).capacity(10).category(cat).instructor(ins).build());
        Student s = studentRepository.save(Student.builder()
                .fullName("HV").email("hv3-" + System.nanoTime() + "@t.edu").build());

        Long courseId = draft.getId();
        assertThatThrownBy(() -> enrollmentService.enroll(new EnrollmentRequest(s.getId(), courseId)))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("chưa được xuất bản");
    }

    @Test
    void enroll_shouldFail_whenStudentAlreadyEnrolled() {
        Course course = publishedCourse(10);
        Student s = newStudent();

        enrollmentService.enroll(new EnrollmentRequest(s.getId(), course.getId()));

        Long courseId = course.getId();
        Long studentId = s.getId();
        assertThatThrownBy(() -> enrollmentService.enroll(new EnrollmentRequest(studentId, courseId)))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("đã ghi danh");
    }

    @Test
    void enroll_shouldReactivate_whenPreviousEnrollmentWasCancelled() {
        Course course = publishedCourse(10);
        Student s = newStudent();

        var first = enrollmentService.enroll(new EnrollmentRequest(s.getId(), course.getId()));
        enrollmentService.updateProgress(first.id(), new UpdateProgressRequest(40));
        enrollmentService.cancel(first.id());

        var again = enrollmentService.enroll(new EnrollmentRequest(s.getId(), course.getId()));

        // Reuses the same row instead of inserting a second one, and resets progress.
        assertThat(again.id()).isEqualTo(first.id());
        assertThat(again.status()).isEqualTo(EnrollmentStatus.ACTIVE);
        assertThat(again.progressPercent()).isZero();
    }

    @Test
    void cancel_shouldReleaseSeatForAnotherStudent() {
        Course course = publishedCourse(1);
        Student first = newStudent();
        Student second = newStudent();

        var enrolled = enrollmentService.enroll(new EnrollmentRequest(first.getId(), course.getId()));
        Long courseId = course.getId();
        Long secondId = second.getId();

        assertThatThrownBy(() -> enrollmentService.enroll(new EnrollmentRequest(secondId, courseId)))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("đã đầy");

        var cancelled = enrollmentService.cancel(enrolled.id());
        assertThat(cancelled.status()).isEqualTo(EnrollmentStatus.CANCELLED);

        // The freed seat only counts because the capacity check looks at ACTIVE rows.
        var third = enrollmentService.enroll(new EnrollmentRequest(secondId, courseId));
        assertThat(third.status()).isEqualTo(EnrollmentStatus.ACTIVE);
    }

    @Test
    void cancel_shouldFail_whenAlreadyCancelled() {
        Course course = publishedCourse(5);
        Student s = newStudent();
        var enrolled = enrollmentService.enroll(new EnrollmentRequest(s.getId(), course.getId()));

        enrollmentService.cancel(enrolled.id());

        Long enrollmentId = enrolled.id();
        assertThatThrownBy(() -> enrollmentService.cancel(enrollmentId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("đã bị hủy");
    }

    @Test
    void updateProgress_shouldCompleteAndIssueCertificate_whenReaching100() {
        Course course = publishedCourse(5);
        Student s = newStudent();
        var enrolled = enrollmentService.enroll(new EnrollmentRequest(s.getId(), course.getId()));

        var halfway = enrollmentService.updateProgress(enrolled.id(), new UpdateProgressRequest(50));
        assertThat(halfway.status()).isEqualTo(EnrollmentStatus.ACTIVE);
        assertThat(halfway.completedAt()).isNull();
        assertThat(certificateRepository.existsByEnrollmentId(enrolled.id())).isFalse();

        var done = enrollmentService.updateProgress(enrolled.id(), new UpdateProgressRequest(100));

        assertThat(done.status()).isEqualTo(EnrollmentStatus.COMPLETED);
        assertThat(done.completedAt()).isNotNull();
        // The certificate is issued inside the same transaction as the progress update.
        assertThat(certificateRepository.existsByEnrollmentId(enrolled.id())).isTrue();
    }

    @Test
    void updateProgress_shouldFail_whenEnrollmentCancelled() {
        Course course = publishedCourse(5);
        Student s = newStudent();
        var enrolled = enrollmentService.enroll(new EnrollmentRequest(s.getId(), course.getId()));
        enrollmentService.cancel(enrolled.id());

        Long enrollmentId = enrolled.id();
        assertThatThrownBy(() ->
                enrollmentService.updateProgress(enrollmentId, new UpdateProgressRequest(60)))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("đã hủy");
    }

    /** A published course with one lesson, so it satisfies the publish rule. */
    private Course publishedCourse(int capacity) {
        Category cat = categoryRepository.save(
                Category.builder().name("Cat-" + System.nanoTime()).build());
        Instructor ins = instructorRepository.save(Instructor.builder()
                .fullName("GV").email("gv-" + System.nanoTime() + "@t.edu").build());
        Course course = Course.builder()
                .title("Khoa hoc " + System.nanoTime())
                .level(CourseLevel.BEGINNER).status(CourseStatus.DRAFT)
                .price(BigDecimal.ZERO).capacity(capacity)
                .category(cat).instructor(ins).build();
        course.addLesson(Lesson.builder().title("Bai 1").orderIndex(1).build());
        course.setStatus(CourseStatus.PUBLISHED);
        return courseRepository.save(course);
    }

    private Student newStudent() {
        return studentRepository.save(Student.builder()
                .fullName("HV").email("hv-" + System.nanoTime() + "@t.edu").build());
    }
}
