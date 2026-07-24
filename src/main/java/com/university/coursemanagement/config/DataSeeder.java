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
            return;
        }
        log.info("[DataSeeder] Nạp dữ liệu mẫu cho profile dev...");

        Category programming = categoryRepository.save(Category.builder()
                .name("Lập trình").description("Các khóa học lập trình phần mềm").build());
        Category language = categoryRepository.save(Category.builder()
                .name("Ngoại ngữ").description("Tiếng Anh, tiếng Nhật...").build());
        Category design = categoryRepository.save(Category.builder()
                .name("Thiết kế").description("UI/UX, đồ họa").build());

        Instructor anh = instructorRepository.save(Instructor.builder()
                .fullName("Nguyễn Văn Anh").email("anh.nguyen@uni.edu")
                .expertise("Java, Spring Boot").bio("10 năm kinh nghiệm backend").build());
        Instructor binh = instructorRepository.save(Instructor.builder()
                .fullName("Trần Thị Bình").email("binh.tran@uni.edu")
                .expertise("English").bio("IELTS 8.5").build());

        Student[] students = {
                studentRepository.save(Student.builder()
                        .fullName("Lê Văn Cường").email("cuong.le@student.edu").phone("0900000001").build()),
                studentRepository.save(Student.builder()
                        .fullName("Phạm Thị Dung").email("dung.pham@student.edu").phone("0900000002").build()),
                studentRepository.save(Student.builder()
                        .fullName("Hoàng Văn Em").email("em.hoang@student.edu").phone("0900000003").build()),
        };

        Course java = Course.builder()
                .title("Java Spring Boot từ cơ bản đến nâng cao")
                .description("Xây dựng REST API hoàn chỉnh với Spring Boot, JPA, Docker.")
                .level(CourseLevel.INTERMEDIATE).status(CourseStatus.DRAFT)
                .price(new BigDecimal("1200000")).capacity(3).durationHours(40)
                .category(programming).instructor(anh).build();
        java.addLesson(Lesson.builder().title("Giới thiệu Spring Boot").orderIndex(1).durationMinutes(45).build());
        java.addLesson(Lesson.builder().title("JPA & Hibernate").orderIndex(2).durationMinutes(60).build());
        java.addLesson(Lesson.builder().title("REST API & Swagger").orderIndex(3).durationMinutes(50).build());
        java.setStatus(CourseStatus.PUBLISHED);
        java = courseRepository.save(java);

        Course english = Course.builder()
                .title("Tiếng Anh giao tiếp cơ bản")
                .description("Phát triển kỹ năng giao tiếp hàng ngày.")
                .level(CourseLevel.BEGINNER).status(CourseStatus.DRAFT)
                .price(new BigDecimal("800000")).capacity(20).durationHours(30)
                .category(language).instructor(binh).build();
        english.addLesson(Lesson.builder().title("Chào hỏi & giới thiệu").orderIndex(1).durationMinutes(40).build());
        english.setStatus(CourseStatus.PUBLISHED);
        english = courseRepository.save(english);

        courseRepository.save(Course.builder()
                .title("Thiết kế UI/UX với Figma")
                .description("Nhập môn thiết kế giao diện.")
                .level(CourseLevel.BEGINNER).status(CourseStatus.DRAFT)
                .price(new BigDecimal("950000")).capacity(15).durationHours(25)
                .category(design).instructor(anh).build());

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

        reviewRepository.saveAll(List.of(
                Review.builder().student(students[0]).course(java).rating(5)
                        .comment("Khóa học rất chi tiết, ví dụ dễ hiểu.").build(),
                Review.builder().student(students[1]).course(java).rating(4)
                        .comment("Nội dung tốt, mong có thêm bài tập.").build(),
                Review.builder().student(students[0]).course(english).rating(4)
                        .comment("Giảng viên phát âm chuẩn.").build()
        ));

        log.info("[DataSeeder] Hoàn tất: {} danh mục, {} khóa học, {} học viên.",
                categoryRepository.count(), courseRepository.count(), studentRepository.count());
    }
}
