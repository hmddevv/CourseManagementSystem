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
import com.university.coursemanagement.repository.CategoryRepository;
import com.university.coursemanagement.repository.CertificateRepository;
import com.university.coursemanagement.repository.CourseRepository;
import com.university.coursemanagement.repository.InstructorRepository;
import com.university.coursemanagement.repository.StudentRepository;
import com.university.coursemanagement.service.CertificateService;
import com.university.coursemanagement.service.EnrollmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Kiem tra quy tac cap chung chi: chi cap khi hoan thanh 100%, va khong bao gio
 * cap trung cho cung mot ghi danh.
 */
@SpringBootTest
@ActiveProfiles("dev")
class CertificateServiceTest {

    @Autowired EnrollmentService enrollmentService;
    @Autowired CertificateService certificateService;
    @Autowired CertificateRepository certificateRepository;
    @Autowired CategoryRepository categoryRepository;
    @Autowired InstructorRepository instructorRepository;
    @Autowired StudentRepository studentRepository;
    @Autowired CourseRepository courseRepository;

    @Test
    void certificate_shouldBeIssued_whenProgressReaches100() {
        var enrollment = givenEnrollment();

        var completed = enrollmentService.updateProgress(enrollment.id(), new UpdateProgressRequest(100));

        assertThat(completed.status()).isEqualTo(EnrollmentStatus.COMPLETED);
        assertThat(certificateRepository.existsByEnrollmentId(enrollment.id())).isTrue();

        var certificate = certificateService.findByEnrollment(enrollment.id()).orElseThrow();
        assertThat(certificate.code()).startsWith("CERT-");
        assertThat(certificate.studentName()).isNotBlank();
        assertThat(certificate.courseTitle()).isNotBlank();
    }

    @Test
    void certificate_shouldNotBeIssued_whenProgressBelow100() {
        var enrollment = givenEnrollment();

        enrollmentService.updateProgress(enrollment.id(), new UpdateProgressRequest(99));

        assertThat(certificateRepository.existsByEnrollmentId(enrollment.id())).isFalse();
    }

    @Test
    void certificate_shouldNotBeDuplicated_whenProgressSetTo100Twice() {
        var enrollment = givenEnrollment();

        enrollmentService.updateProgress(enrollment.id(), new UpdateProgressRequest(100));
        var first = certificateService.findByEnrollment(enrollment.id()).orElseThrow();

        enrollmentService.updateProgress(enrollment.id(), new UpdateProgressRequest(100));
        var second = certificateService.findByEnrollment(enrollment.id()).orElseThrow();

        assertThat(second.id()).isEqualTo(first.id());
        assertThat(second.code()).isEqualTo(first.code());
    }

    @Test
    void getByCode_shouldFindIssuedCertificate() {
        var enrollment = givenEnrollment();
        enrollmentService.updateProgress(enrollment.id(), new UpdateProgressRequest(100));
        var issued = certificateService.findByEnrollment(enrollment.id()).orElseThrow();

        assertThat(certificateService.getByCode(issued.code()).id()).isEqualTo(issued.id());
    }

    @Test
    void getByStudent_shouldReturnPagedCertificates() {
        var enrollment = givenEnrollment();
        enrollmentService.updateProgress(enrollment.id(), new UpdateProgressRequest(100));

        var page = certificateService.getByStudent(enrollment.studentId(), PageRequest.of(0, 10));

        assertThat(page.getContent()).hasSize(1);
    }

    // ----- du lieu mau -----

    private com.university.coursemanagement.dto.response.EnrollmentResponse givenEnrollment() {
        Category category = categoryRepository.save(
                Category.builder().name("Cert-Cat-" + System.nanoTime()).build());
        Instructor instructor = instructorRepository.save(Instructor.builder()
                .fullName("GV Cert").email("gv-cert-" + System.nanoTime() + "@t.edu").build());
        Course course = Course.builder()
                .title("Khoa hoc cap chung chi").level(CourseLevel.BEGINNER).status(CourseStatus.DRAFT)
                .price(BigDecimal.ZERO).capacity(50).category(category).instructor(instructor).build();
        course.addLesson(Lesson.builder().title("Bai 1").orderIndex(1).build());
        course.setStatus(CourseStatus.PUBLISHED);
        course = courseRepository.save(course);

        Student student = studentRepository.save(Student.builder()
                .fullName("Hoc vien chung chi")
                .email("cert-" + System.nanoTime() + "@t.edu").build());

        return enrollmentService.enroll(new EnrollmentRequest(student.getId(), course.getId()));
    }
}
