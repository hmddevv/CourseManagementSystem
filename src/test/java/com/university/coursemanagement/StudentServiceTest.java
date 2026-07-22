package com.university.coursemanagement;

import com.university.coursemanagement.dto.request.EnrollmentRequest;
import com.university.coursemanagement.dto.request.StudentRequest;
import com.university.coursemanagement.entity.Category;
import com.university.coursemanagement.entity.Course;
import com.university.coursemanagement.entity.Instructor;
import com.university.coursemanagement.entity.Lesson;
import com.university.coursemanagement.entity.Student;
import com.university.coursemanagement.entity.enums.CourseLevel;
import com.university.coursemanagement.entity.enums.CourseStatus;
import com.university.coursemanagement.exception.BusinessException;
import com.university.coursemanagement.exception.DuplicateResourceException;
import com.university.coursemanagement.repository.CategoryRepository;
import com.university.coursemanagement.repository.CourseRepository;
import com.university.coursemanagement.repository.InstructorRepository;
import com.university.coursemanagement.repository.StudentRepository;
import com.university.coursemanagement.service.EnrollmentService;
import com.university.coursemanagement.service.StudentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Kiem tra quy tac nghiep vu cua hoc vien: khong xoa hoc vien con lich su ghi danh,
 * khong trung email, va so ghi danh tra ve dung.
 */
@SpringBootTest
@ActiveProfiles("dev")
class StudentServiceTest {

    @Autowired StudentService studentService;
    @Autowired EnrollmentService enrollmentService;
    @Autowired StudentRepository studentRepository;
    @Autowired CategoryRepository categoryRepository;
    @Autowired InstructorRepository instructorRepository;
    @Autowired CourseRepository courseRepository;

    @Test
    void delete_shouldFailWithBusinessMessage_whenStudentHasEnrollment() {
        Student student = studentRepository.save(Student.builder()
                .fullName("Hoc vien co ghi danh")
                .email("del-" + System.nanoTime() + "@t.edu").build());
        Course course = givenPublishedCourse();
        enrollmentService.enroll(new EnrollmentRequest(student.getId(), course.getId()));

        Long studentId = student.getId();
        // Phai la loi NGHIEP VU ro rang, khong phai loi rang buoc khoa ngoai chung chung
        assertThatThrownBy(() -> studentService.delete(studentId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("lich su ghi danh");

        assertThat(studentRepository.existsById(studentId)).isTrue();
    }

    @Test
    void delete_shouldSucceed_whenStudentHasNoEnrollment() {
        Student student = studentRepository.save(Student.builder()
                .fullName("Hoc vien moi")
                .email("free-" + System.nanoTime() + "@t.edu").build());

        studentService.delete(student.getId());

        assertThat(studentRepository.existsById(student.getId())).isFalse();
    }

    @Test
    void create_shouldFail_whenEmailAlreadyUsed() {
        String email = "dup-" + System.nanoTime() + "@t.edu";
        studentService.create(new StudentRequest("Nguoi thu nhat", email, "0900000001"));

        assertThatThrownBy(() -> studentService.create(new StudentRequest("Nguoi thu hai", email, "0900000002")))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void getById_shouldReturnEnrollmentCount() {
        Student student = studentRepository.save(Student.builder()
                .fullName("Hoc vien dem")
                .email("count-" + System.nanoTime() + "@t.edu").build());
        enrollmentService.enroll(new EnrollmentRequest(student.getId(), givenPublishedCourse().getId()));
        enrollmentService.enroll(new EnrollmentRequest(student.getId(), givenPublishedCourse().getId()));

        assertThat(studentService.getById(student.getId()).enrollmentCount()).isEqualTo(2);
    }

    private Course givenPublishedCourse() {
        Category category = categoryRepository.save(
                Category.builder().name("Cat-" + System.nanoTime()).build());
        Instructor instructor = instructorRepository.save(Instructor.builder()
                .fullName("GV").email("gv-" + System.nanoTime() + "@t.edu").build());
        Course course = Course.builder()
                .title("Khoa hoc mo").level(CourseLevel.BEGINNER).status(CourseStatus.DRAFT)
                .price(BigDecimal.ZERO).capacity(50).category(category).instructor(instructor).build();
        course.addLesson(Lesson.builder().title("Bai 1").orderIndex(1).build());
        course.setStatus(CourseStatus.PUBLISHED);
        return courseRepository.save(course);
    }
}
