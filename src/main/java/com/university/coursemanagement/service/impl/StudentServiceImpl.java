package com.university.coursemanagement.service.impl;

import com.university.coursemanagement.common.PageResponse;
import com.university.coursemanagement.dto.mapper.StudentMapper;
import com.university.coursemanagement.dto.request.StudentRequest;
import com.university.coursemanagement.dto.response.StudentResponse;
import com.university.coursemanagement.entity.Student;
import com.university.coursemanagement.exception.BusinessException;
import com.university.coursemanagement.exception.DuplicateResourceException;
import com.university.coursemanagement.exception.ResourceNotFoundException;
import com.university.coursemanagement.repository.EnrollmentRepository;
import com.university.coursemanagement.repository.StudentRepository;
import com.university.coursemanagement.service.StudentService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class StudentServiceImpl implements StudentService {
    private final StudentRepository studentRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final StudentMapper studentMapper;

    public StudentServiceImpl(StudentRepository studentRepository,
                              EnrollmentRepository enrollmentRepository,
                              StudentMapper studentMapper) {
        this.studentRepository = studentRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.studentMapper = studentMapper;
    }

    @Override
    @Transactional
    public StudentResponse create(StudentRequest request) {
        if (studentRepository.existsByEmailIgnoreCase(request.email())) {
            throw new DuplicateResourceException("Email '%s' đã được sử dụng".formatted(request.email()));
        }
        Student saved = studentRepository.save(studentMapper.toEntity(request));
        return studentMapper.toResponse(saved, 0);
    }

    @Override
    @Transactional
    public StudentResponse update(Long id, StudentRequest request) {
        Student student = findOrThrow(id);
        if (!student.getEmail().equalsIgnoreCase(request.email())
                && studentRepository.existsByEmailIgnoreCase(request.email())) {
            throw new DuplicateResourceException("Email '%s' đã được sử dụng".formatted(request.email()));
        }
        studentMapper.updateEntity(student, request);
        return toResponse(student);
    }

    @Override
    public StudentResponse getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    @Override
    public PageResponse<StudentResponse> getAll(Pageable pageable) {
        return PageResponse.from(studentRepository.findAll(pageable).map(this::toResponse));
    }

    @Override
    public List<StudentResponse> getAllSimple() {
        return studentRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Student student = findOrThrow(id);
        if (enrollmentRepository.existsByStudentId(id)) {
            throw new BusinessException(
                    "Học viên đã có lịch sử ghi danh, không thể xóa. Hãy hủy các ghi danh trước.");
        }
        studentRepository.delete(student);
    }

    private StudentResponse toResponse(Student student) {
        return studentMapper.toResponse(student, enrollmentRepository.countByStudentId(student.getId()));
    }

    private Student findOrThrow(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("học viên", id));
    }
}
