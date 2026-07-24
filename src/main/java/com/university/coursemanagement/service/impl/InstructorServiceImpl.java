package com.university.coursemanagement.service.impl;

import com.university.coursemanagement.common.PageResponse;
import com.university.coursemanagement.dto.mapper.InstructorMapper;
import com.university.coursemanagement.dto.request.InstructorRequest;
import com.university.coursemanagement.dto.response.InstructorResponse;
import com.university.coursemanagement.entity.Instructor;
import com.university.coursemanagement.exception.BusinessException;
import com.university.coursemanagement.exception.DuplicateResourceException;
import com.university.coursemanagement.exception.ResourceNotFoundException;
import com.university.coursemanagement.repository.CourseRepository;
import com.university.coursemanagement.repository.InstructorRepository;
import com.university.coursemanagement.service.InstructorService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class InstructorServiceImpl implements InstructorService {
    private final InstructorRepository instructorRepository;
    private final CourseRepository courseRepository;
    private final InstructorMapper instructorMapper;

    public InstructorServiceImpl(InstructorRepository instructorRepository,
                                 CourseRepository courseRepository,
                                 InstructorMapper instructorMapper) {
        this.instructorRepository = instructorRepository;
        this.courseRepository = courseRepository;
        this.instructorMapper = instructorMapper;
    }

    @Override
    @Transactional
    public InstructorResponse create(InstructorRequest request) {
        if (instructorRepository.existsByEmailIgnoreCase(request.email())) {
            throw new DuplicateResourceException("Email '%s' đã được sử dụng".formatted(request.email()));
        }
        Instructor saved = instructorRepository.save(instructorMapper.toEntity(request));
        return instructorMapper.toResponse(saved, 0);
    }

    @Override
    @Transactional
    public InstructorResponse update(Long id, InstructorRequest request) {
        Instructor instructor = findOrThrow(id);
        if (!instructor.getEmail().equalsIgnoreCase(request.email())
                && instructorRepository.existsByEmailIgnoreCase(request.email())) {
            throw new DuplicateResourceException("Email '%s' đã được sử dụng".formatted(request.email()));
        }
        instructorMapper.updateEntity(instructor, request);
        return toResponse(instructor);
    }

    @Override
    public InstructorResponse getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    @Override
    public PageResponse<InstructorResponse> getAll(Pageable pageable) {
        return PageResponse.from(instructorRepository.findAll(pageable).map(this::toResponse));
    }

    @Override
    public List<InstructorResponse> getAllSimple() {
        return instructorRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Instructor instructor = findOrThrow(id);
        if (courseRepository.existsByInstructorId(id)) {
            throw new BusinessException("Không thể xóa giảng viên đang phụ trách khóa học.");
        }
        instructorRepository.delete(instructor);
    }

    private InstructorResponse toResponse(Instructor instructor) {
        return instructorMapper.toResponse(instructor, courseRepository.countByInstructorId(instructor.getId()));
    }

    private Instructor findOrThrow(Long id) {
        return instructorRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("giảng viên", id));
    }
}
