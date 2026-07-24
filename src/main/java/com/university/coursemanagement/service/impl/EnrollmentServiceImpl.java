package com.university.coursemanagement.service.impl;

import com.university.coursemanagement.common.PageResponse;
import com.university.coursemanagement.dto.mapper.EnrollmentMapper;
import com.university.coursemanagement.dto.request.EnrollmentRequest;
import com.university.coursemanagement.dto.request.UpdateProgressRequest;
import com.university.coursemanagement.dto.response.EnrollmentResponse;
import com.university.coursemanagement.entity.Course;
import com.university.coursemanagement.entity.Enrollment;
import com.university.coursemanagement.entity.Student;
import com.university.coursemanagement.entity.enums.EnrollmentStatus;
import com.university.coursemanagement.exception.BusinessException;
import com.university.coursemanagement.exception.ResourceNotFoundException;
import com.university.coursemanagement.factory.EnrollmentFactory;
import com.university.coursemanagement.repository.CourseRepository;
import com.university.coursemanagement.repository.EnrollmentRepository;
import com.university.coursemanagement.repository.StudentRepository;
import com.university.coursemanagement.service.CertificateService;
import com.university.coursemanagement.service.EnrollmentService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
public class EnrollmentServiceImpl implements EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentFactory enrollmentFactory;
    private final EnrollmentMapper enrollmentMapper;
    private final CertificateService certificateService;

    public EnrollmentServiceImpl(EnrollmentRepository enrollmentRepository,
                                 StudentRepository studentRepository,
                                 CourseRepository courseRepository,
                                 EnrollmentFactory enrollmentFactory,
                                 EnrollmentMapper enrollmentMapper,
                                 CertificateService certificateService) {
        this.enrollmentRepository = enrollmentRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.enrollmentFactory = enrollmentFactory;
        this.enrollmentMapper = enrollmentMapper;
        this.certificateService = certificateService;
    }

    @Override
    @Transactional
    public EnrollmentResponse enroll(EnrollmentRequest request) {
        Student student = studentRepository.findById(request.studentId())
                .orElseThrow(() -> ResourceNotFoundException.of("học viên", request.studentId()));
        Course course = courseRepository.findByIdForUpdate(request.courseId())
                .orElseThrow(() -> ResourceNotFoundException.of("khóa học", request.courseId()));

        if (!course.isPublished()) {
            throw new BusinessException("Khóa học chưa được xuất bản, không thể ghi danh.");
        }

        Enrollment existing = enrollmentRepository
                .findByStudentIdAndCourseId(student.getId(), course.getId())
                .orElse(null);
        if (existing != null && existing.getStatus() != EnrollmentStatus.CANCELLED) {
            throw new BusinessException("Học viên đã ghi danh khóa học này.");
        }

        long active = enrollmentRepository.countByCourseIdAndStatus(course.getId(), EnrollmentStatus.ACTIVE);
        if (active >= course.getCapacity()) {
            throw new BusinessException("Khóa học đã đầy (%d/%d).".formatted(active, course.getCapacity()));
        }

        Enrollment enrollment = (existing != null)
                ? reactivate(existing)
                : enrollmentFactory.createActiveEnrollment(student, course);

        Enrollment saved = enrollmentRepository.save(enrollment);
        return enrollmentMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public EnrollmentResponse cancel(Long enrollmentId) {
        Enrollment enrollment = findOrThrow(enrollmentId);
        if (enrollment.getStatus() == EnrollmentStatus.CANCELLED) {
            throw new BusinessException("Ghi danh này đã bị hủy trước đó.");
        }
        enrollment.setStatus(EnrollmentStatus.CANCELLED);
        return enrollmentMapper.toResponse(enrollment);
    }

    @Override
    @Transactional
    public EnrollmentResponse updateProgress(Long enrollmentId, UpdateProgressRequest request) {
        Enrollment enrollment = findOrThrow(enrollmentId);
        if (enrollment.getStatus() == EnrollmentStatus.CANCELLED) {
            throw new BusinessException("Ghi danh đã hủy, không thể cập nhật tiến độ.");
        }
        int progress = request.progressPercent();
        enrollment.setProgressPercent(progress);
        if (progress >= 100) {
            enrollment.setStatus(EnrollmentStatus.COMPLETED);
            if (enrollment.getCompletedAt() == null) {
                enrollment.setCompletedAt(LocalDateTime.now());
            }
            certificateService.issueFor(enrollment);
        } else {
            enrollment.setStatus(EnrollmentStatus.ACTIVE);
            enrollment.setCompletedAt(null);
        }
        return enrollmentMapper.toResponse(enrollment);
    }

    @Override
    public EnrollmentResponse getById(Long enrollmentId) {
        return enrollmentMapper.toResponse(findOrThrow(enrollmentId));
    }

    @Override
    public PageResponse<EnrollmentResponse> getByStudent(Long studentId, Pageable pageable) {
        if (!studentRepository.existsById(studentId)) {
            throw ResourceNotFoundException.of("học viên", studentId);
        }
        return PageResponse.from(
                enrollmentRepository.findByStudentId(studentId, pageable).map(enrollmentMapper::toResponse));
    }

    @Override
    public PageResponse<EnrollmentResponse> getByCourse(Long courseId, Pageable pageable) {
        if (!courseRepository.existsById(courseId)) {
            throw ResourceNotFoundException.of("khóa học", courseId);
        }
        return PageResponse.from(
                enrollmentRepository.findByCourseId(courseId, pageable).map(enrollmentMapper::toResponse));
    }

    private Enrollment reactivate(Enrollment existing) {
        existing.setStatus(EnrollmentStatus.ACTIVE);
        existing.setProgressPercent(0);
        existing.setCompletedAt(null);
        existing.setEnrolledAt(LocalDateTime.now());
        return existing;
    }

    private Enrollment findOrThrow(Long id) {
        return enrollmentRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("ghi danh", id));
    }
}
