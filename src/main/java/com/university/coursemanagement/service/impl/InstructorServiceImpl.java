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
            throw new DuplicateResourceException("Email '%s' da duoc su dung".formatted(request.email()));
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
            throw new DuplicateResourceException("Email '%s' da duoc su dung".formatted(request.email()));
        }
        instructorMapper.updateEntity(instructor, request);
        return instructorMapper.toResponse(instructor, instructor.getCourses().size());
    }

    @Override
    public InstructorResponse getById(Long id) {
        Instructor instructor = findOrThrow(id);
        return instructorMapper.toResponse(instructor, instructor.getCourses().size());
    }

    @Override
    public PageResponse<InstructorResponse> getAll(Pageable pageable) {
        return PageResponse.from(
                instructorRepository.findAll(pageable)
                        .map(i -> instructorMapper.toResponse(i, i.getCourses().size()))
        );
    }

    @Override
    public List<InstructorResponse> getAllSimple() {
        return instructorRepository.findAll().stream()
                .map(i -> instructorMapper.toResponse(i, i.getCourses().size()))
                .toList();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Instructor instructor = findOrThrow(id);
        if (courseRepository.existsByInstructorId(id)) {
            throw new BusinessException("Khong the xoa giang vien dang phu trach khoa hoc.");
        }
        instructorRepository.delete(instructor);
    }

    private Instructor findOrThrow(Long id) {
        return instructorRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("giang vien", id));
    }
}
