package com.university.coursemanagement.service.impl;

import com.university.coursemanagement.common.PageResponse;
import com.university.coursemanagement.config.CacheConfig;
import com.university.coursemanagement.dto.mapper.CourseMapper;
import com.university.coursemanagement.dto.request.CourseRequest;
import com.university.coursemanagement.dto.request.CourseSearchCriteria;
import com.university.coursemanagement.dto.response.CourseResponse;
import com.university.coursemanagement.dto.response.CourseStatisticsResponse;
import com.university.coursemanagement.entity.Category;
import com.university.coursemanagement.entity.Course;
import com.university.coursemanagement.entity.Instructor;
import com.university.coursemanagement.entity.enums.CourseStatus;
import com.university.coursemanagement.entity.enums.EnrollmentStatus;
import com.university.coursemanagement.exception.BusinessException;
import com.university.coursemanagement.exception.ResourceNotFoundException;
import com.university.coursemanagement.repository.CategoryRepository;
import com.university.coursemanagement.repository.CourseRepository;
import com.university.coursemanagement.repository.CourseSpecifications;
import com.university.coursemanagement.repository.EnrollmentRepository;
import com.university.coursemanagement.repository.InstructorRepository;
import com.university.coursemanagement.repository.LessonRepository;
import com.university.coursemanagement.repository.ReviewRepository;
import com.university.coursemanagement.repository.StudentRepository;
import com.university.coursemanagement.service.CourseService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;
    private final CategoryRepository categoryRepository;
    private final InstructorRepository instructorRepository;
    private final LessonRepository lessonRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final ReviewRepository reviewRepository;
    private final CourseMapper courseMapper;

    public CourseServiceImpl(CourseRepository courseRepository,
                             CategoryRepository categoryRepository,
                             InstructorRepository instructorRepository,
                             LessonRepository lessonRepository,
                             EnrollmentRepository enrollmentRepository,
                             StudentRepository studentRepository,
                             ReviewRepository reviewRepository,
                             CourseMapper courseMapper) {
        this.courseRepository = courseRepository;
        this.categoryRepository = categoryRepository;
        this.instructorRepository = instructorRepository;
        this.lessonRepository = lessonRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.studentRepository = studentRepository;
        this.reviewRepository = reviewRepository;
        this.courseMapper = courseMapper;
    }

    @Override
    @Transactional
    @CacheEvict(value = CacheConfig.COURSE_STATISTICS_CACHE, allEntries = true)
    public CourseResponse create(CourseRequest request) {
        Category category = loadCategory(request.categoryId());
        Instructor instructor = loadInstructor(request.instructorId());

        Course course = Course.builder()
                .title(request.title())
                .description(request.description())
                .level(request.level())
                .status(CourseStatus.DRAFT)
                .price(request.price())
                .capacity(request.capacity())
                .durationHours(request.durationHours())
                .category(category)
                .instructor(instructor)
                .build();

        Course saved = courseRepository.save(course);
        return toResponse(saved);
    }

    @Override
    @Transactional
    @CacheEvict(value = CacheConfig.COURSE_STATISTICS_CACHE, allEntries = true)
    public CourseResponse update(Long id, CourseRequest request) {
        Course course = findOrThrow(id);
        if (course.getStatus() == CourseStatus.ARCHIVED) {
            throw new BusinessException("Khóa học đã lưu trữ, không thể chỉnh sửa.");
        }
        long active = enrollmentRepository.countByCourseIdAndStatus(id, EnrollmentStatus.ACTIVE);
        if (request.capacity() < active) {
            throw new BusinessException(
                    "Sức chứa mới (%d) nhỏ hơn số học viên đang học (%d).".formatted(request.capacity(), active));
        }
        course.setTitle(request.title());
        course.setDescription(request.description());
        course.setLevel(request.level());
        course.setPrice(request.price());
        course.setCapacity(request.capacity());
        course.setDurationHours(request.durationHours());
        course.setCategory(loadCategory(request.categoryId()));
        course.setInstructor(loadInstructor(request.instructorId()));
        return toResponse(course);
    }

    @Override
    public CourseResponse getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    @Override
    public PageResponse<CourseResponse> search(CourseSearchCriteria c, Pageable pageable) {
        Specification<Course> spec = Specification.allOf(
                CourseSpecifications.keywordLike(c.keyword()),
                CourseSpecifications.hasCategory(c.categoryId()),
                CourseSpecifications.hasInstructor(c.instructorId()),
                CourseSpecifications.hasLevel(c.level()),
                CourseSpecifications.hasStatus(c.status()),
                CourseSpecifications.priceGreaterOrEqual(c.minPrice()),
                CourseSpecifications.priceLessOrEqual(c.maxPrice())
        );
        Page<Course> page = courseRepository.findAll(spec, pageable);
        return PageResponse.from(page.map(new CourseListMapper(page.getContent())::apply));
    }

    private final class CourseListMapper {
        private final Map<Long, Long> lessonCounts;
        private final Map<Long, Long> activeEnrollmentCounts;
        private final Map<Long, ReviewRepository.CourseRatingAggregate> ratings;

        private CourseListMapper(List<Course> courses) {
            List<Long> ids = courses.stream().map(Course::getId).toList();
            if (ids.isEmpty()) {
                this.lessonCounts = Map.of();
                this.activeEnrollmentCounts = Map.of();
                this.ratings = Map.of();
                return;
            }
            this.lessonCounts = lessonRepository.countGroupedByCourseIds(ids).stream()
                    .collect(Collectors.toMap(
                            LessonRepository.CourseCount::getCourseId,
                            LessonRepository.CourseCount::getTotal));
            this.activeEnrollmentCounts = enrollmentRepository
                    .countGroupedByCourseIds(ids, EnrollmentStatus.ACTIVE).stream()
                    .collect(Collectors.toMap(
                            EnrollmentRepository.CourseEnrollmentCount::getCourseId,
                            EnrollmentRepository.CourseEnrollmentCount::getTotal));
            this.ratings = reviewRepository.aggregateByCourseIds(ids).stream()
                    .collect(Collectors.toMap(
                            ReviewRepository.CourseRatingAggregate::getCourseId,
                            aggregate -> aggregate));
        }

        private CourseResponse apply(Course course) {
            long lessons = lessonCounts.getOrDefault(course.getId(), 0L);
            long active = activeEnrollmentCounts.getOrDefault(course.getId(), 0L);
            ReviewRepository.CourseRatingAggregate rating = ratings.get(course.getId());
            return courseMapper.toResponse(course, (int) lessons, active,
                    roundRating(rating == null ? null : rating.getAverage()),
                    rating == null ? 0L : rating.getTotal());
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = CacheConfig.COURSE_STATISTICS_CACHE, allEntries = true)
    public CourseResponse publish(Long id) {
        Course course = findOrThrow(id);
        if (course.getStatus() == CourseStatus.PUBLISHED) {
            throw new BusinessException("Khóa học đã ở trạng thái PUBLISHED.");
        }
        if (lessonRepository.countByCourseId(id) == 0) {
            throw new BusinessException("Phải có ít nhất 1 bài học trước khi xuất bản khóa học.");
        }
        course.setStatus(CourseStatus.PUBLISHED);
        return toResponse(course);
    }

    @Override
    @Transactional
    @CacheEvict(value = CacheConfig.COURSE_STATISTICS_CACHE, allEntries = true)
    public CourseResponse archive(Long id) {
        Course course = findOrThrow(id);
        course.setStatus(CourseStatus.ARCHIVED);
        return toResponse(course);
    }

    @Override
    @Transactional
    @CacheEvict(value = CacheConfig.COURSE_STATISTICS_CACHE, allEntries = true)
    public void delete(Long id) {
        Course course = findOrThrow(id);

        if (enrollmentRepository.existsByCourseId(id)) {
            throw new BusinessException(
                    "Khóa học đã có lịch sử ghi danh, không thể xóa. Hãy dùng chức năng lưu trữ (archive).");
        }
        courseRepository.delete(course);
    }

    @Override
    @Cacheable(CacheConfig.COURSE_STATISTICS_CACHE)
    public CourseStatisticsResponse getStatistics() {
        long total = courseRepository.count();

        long published = courseRepository.count(CourseSpecifications.hasStatus(CourseStatus.PUBLISHED));
        long draft = courseRepository.count(CourseSpecifications.hasStatus(CourseStatus.DRAFT));

        List<EnrollmentRepository.CourseEnrollmentCount> counts =
                enrollmentRepository.countActiveGroupedByCourse(EnrollmentStatus.ACTIVE);
        long totalActive = counts.stream().mapToLong(EnrollmentRepository.CourseEnrollmentCount::getTotal).sum();

        List<EnrollmentRepository.CourseEnrollmentCount> top = counts.stream().limit(5).toList();
        Map<Long, String> titles = courseRepository.findAllById(
                        top.stream().map(EnrollmentRepository.CourseEnrollmentCount::getCourseId).toList())
                .stream()
                .collect(Collectors.toMap(Course::getId, Course::getTitle));

        List<CourseStatisticsResponse.PopularCourse> popular = top.stream()
                .map(t -> new CourseStatisticsResponse.PopularCourse(
                        t.getCourseId(),
                        titles.getOrDefault(t.getCourseId(), "?"),
                        t.getTotal()))
                .toList();

        return new CourseStatisticsResponse(
                total, published, draft, studentRepository.count(), totalActive, popular);
    }

    private CourseResponse toResponse(Course course) {
        int lessonCount = (int) lessonRepository.countByCourseId(course.getId());
        long active = enrollmentRepository.countByCourseIdAndStatus(course.getId(), EnrollmentStatus.ACTIVE);
        ReviewRepository.CourseRatingAggregate rating = reviewRepository
                .aggregateByCourseIds(List.of(course.getId())).stream().findFirst().orElse(null);
        return courseMapper.toResponse(course, lessonCount, active,
                roundRating(rating == null ? null : rating.getAverage()),
                rating == null ? 0L : rating.getTotal());
    }

    private double roundRating(Double average) {
        return average == null ? 0d : Math.round(average * 10d) / 10d;
    }

    private Course findOrThrow(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("khóa học", id));
    }

    private Category loadCategory(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("danh mục", id));
    }

    private Instructor loadInstructor(Long id) {
        return instructorRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("giảng viên", id));
    }
}
