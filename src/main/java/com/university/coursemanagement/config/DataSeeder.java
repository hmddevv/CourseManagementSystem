package com.university.coursemanagement.config;

import com.university.coursemanagement.entity.Category;
import com.university.coursemanagement.entity.Course;
import com.university.coursemanagement.entity.Enrollment;
import com.university.coursemanagement.entity.Instructor;
import com.university.coursemanagement.entity.Lesson;
import com.university.coursemanagement.entity.Review;
import com.university.coursemanagement.entity.Student;
import com.university.coursemanagement.entity.enums.CourseLevel;
import com.university.coursemanagement.entity.enums.CourseStatus;
import com.university.coursemanagement.entity.enums.EnrollmentStatus;
import com.university.coursemanagement.repository.CategoryRepository;
import com.university.coursemanagement.repository.CourseRepository;
import com.university.coursemanagement.repository.EnrollmentRepository;
import com.university.coursemanagement.repository.InstructorRepository;
import com.university.coursemanagement.repository.ReviewRepository;
import com.university.coursemanagement.repository.StudentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Nap du lieu mau khi chay o profile 'dev' (H2). Chi chay khi DB rong.
 * Giup demo/test nhanh ma khong phai nhap tay.
 */
@Component
@Profile("dev")
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final CategoryRepository categoryRepository;
    private final InstructorRepository instructorRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final ReviewRepository reviewRepository;

    public DataSeeder(CategoryRepository categoryRepository,
                      InstructorRepository instructorRepository,
                      StudentRepository studentRepository,
                      CourseRepository courseRepository,
                      EnrollmentRepository enrollmentRepository,
                      ReviewRepository reviewRepository) {
        this.categoryRepository = categoryRepository;
        this.instructorRepository = instructorRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.reviewRepository = reviewRepository;
    }

    @Override
    public void run(String... args) {
        if (categoryRepository.count() > 0) {
            return; // da co du lieu
        }
        log.info("[DataSeeder] Nap du lieu mau cho profile dev...");

        Category programming = categoryRepository.save(Category.builder()
                .name("Lap trinh").description("Cac khoa hoc lap trinh phan mem").build());
        Category language = categoryRepository.save(Category.builder()
                .name("Ngoai ngu").description("Tieng Anh, tieng Nhat...").build());
        Category design = categoryRepository.save(Category.builder()
                .name("Thiet ke").description("UI/UX, do hoa").build());

        Instructor anh = instructorRepository.save(Instructor.builder()
                .fullName("Nguyen Van Anh").email("anh.nguyen@uni.edu")
                .expertise("Java, Spring Boot").bio("10 nam kinh nghiem backend").build());
        Instructor binh = instructorRepository.save(Instructor.builder()
                .fullName("Tran Thi Binh").email("binh.tran@uni.edu")
                .expertise("English").bio("IELTS 8.5").build());

        Student[] students = {
                studentRepository.save(Student.builder()
                        .fullName("Le Van Cuong").email("cuong.le@student.edu").phone("0900000001").build()),
                studentRepository.save(Student.builder()
                        .fullName("Pham Thi Dung").email("dung.pham@student.edu").phone("0900000002").build()),
                studentRepository.save(Student.builder()
                        .fullName("Hoang Van Em").email("em.hoang@student.edu").phone("0900000003").build()),
        };

        // Khoa hoc 1: co bai hoc, da PUBLISHED
        Course java = Course.builder()
                .title("Java Spring Boot tu co ban den nang cao")
                .description("Xay dung REST API hoan chinh voi Spring Boot, JPA, Docker.")
                .level(CourseLevel.INTERMEDIATE).status(CourseStatus.DRAFT)
                .price(new BigDecimal("1200000")).capacity(3).durationHours(40)
                .category(programming).instructor(anh).build();
        java.addLesson(Lesson.builder().title("Gioi thieu Spring Boot").orderIndex(1).durationMinutes(45).build());
        java.addLesson(Lesson.builder().title("JPA & Hibernate").orderIndex(2).durationMinutes(60).build());
        java.addLesson(Lesson.builder().title("REST API & Swagger").orderIndex(3).durationMinutes(50).build());
        java.setStatus(CourseStatus.PUBLISHED);
        java = courseRepository.save(java);

        // Khoa hoc 2: PUBLISHED
        Course english = Course.builder()
                .title("Tieng Anh giao tiep co ban")
                .description("Phat trien ky nang giao tiep hang ngay.")
                .level(CourseLevel.BEGINNER).status(CourseStatus.DRAFT)
                .price(new BigDecimal("800000")).capacity(20).durationHours(30)
                .category(language).instructor(binh).build();
        english.addLesson(Lesson.builder().title("Chao hoi & gioi thieu").orderIndex(1).durationMinutes(40).build());
        english.setStatus(CourseStatus.PUBLISHED);
        english = courseRepository.save(english);

        // Khoa hoc 3: DRAFT, chua co bai hoc
        courseRepository.save(Course.builder()
                .title("Thiet ke UI/UX voi Figma")
                .description("Nhap mon thiet ke giao dien.")
                .level(CourseLevel.BEGINNER).status(CourseStatus.DRAFT)
                .price(new BigDecimal("950000")).capacity(15).durationHours(25)
                .category(design).instructor(anh).build());

        // Ghi danh mau
        enrollmentRepository.saveAll(List.of(
                Enrollment.builder().student(students[0]).course(java)
                        .enrolledAt(LocalDateTime.now().minusDays(5))
                        .status(EnrollmentStatus.ACTIVE).progressPercent(40).build(),
                Enrollment.builder().student(students[1]).course(java)
                        .enrolledAt(LocalDateTime.now().minusDays(3))
                        .status(EnrollmentStatus.ACTIVE).progressPercent(20).build(),
                Enrollment.builder().student(students[0]).course(english)
                        .enrolledAt(LocalDateTime.now().minusDays(2))
                        .status(EnrollmentStatus.ACTIVE).progressPercent(10).build()
        ));

        // Danh gia mau - de bang xep hang co du lieu ngay khi khoi dong
        reviewRepository.saveAll(List.of(
                Review.builder().student(students[0]).course(java).rating(5)
                        .comment("Khoa hoc rat chi tiet, vi du de hieu.").build(),
                Review.builder().student(students[1]).course(java).rating(4)
                        .comment("Noi dung tot, mong co them bai tap.").build(),
                Review.builder().student(students[0]).course(english).rating(4)
                        .comment("Giang vien phat am chuan.").build()
        ));

        log.info("[DataSeeder] Hoan tat: {} danh muc, {} khoa hoc, {} hoc vien.",
                categoryRepository.count(), courseRepository.count(), studentRepository.count());
    }
}
