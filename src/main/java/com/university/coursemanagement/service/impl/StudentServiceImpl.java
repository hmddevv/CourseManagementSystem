package com.university.coursemanagement.service.impl;

import com.university.coursemanagement.common.PageResponse;
import com.university.coursemanagement.dto.mapper.StudentMapper;
import com.university.coursemanagement.dto.request.StudentRequest;
import com.university.coursemanagement.dto.response.StudentResponse;
import com.university.coursemanagement.entity.Student;
import com.university.coursemanagement.exception.DuplicateResourceException;
import com.university.coursemanagement.exception.ResourceNotFoundException;
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
    private final StudentMapper studentMapper;

    public StudentServiceImpl(StudentRepository studentRepository, StudentMapper studentMapper) {
        this.studentRepository = studentRepository;
        this.studentMapper = studentMapper;
    }

    @Override
    @Transactional
    public StudentResponse create(StudentRequest request) {
        if (studentRepository.existsByEmailIgnoreCase(request.email())) {
            throw new DuplicateResourceException("Email '%s' da duoc su dung".formatted(request.email()));
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
            throw new DuplicateResourceException("Email '%s' da duoc su dung".formatted(request.email()));
        }
        studentMapper.updateEntity(student, request);
        return studentMapper.toResponse(student, student.getEnrollments().size());
    }

    @Override
    public StudentResponse getById(Long id) {
        Student student = findOrThrow(id);
        return studentMapper.toResponse(student, student.getEnrollments().size());
    }

    @Override
    public PageResponse<StudentResponse> getAll(Pageable pageable) {
        return PageResponse.from(
                studentRepository.findAll(pageable)
                        .map(s -> studentMapper.toResponse(s, s.getEnrollments().size()))
        );
    }

    @Override
    public List<StudentResponse> getAllSimple() {
        return studentRepository.findAll().stream()
                .map(s -> studentMapper.toResponse(s, s.getEnrollments().size()))
                .toList();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Student student = findOrThrow(id);
        studentRepository.delete(student);
    }

    private Student findOrThrow(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("hoc vien", id));
    }
}
