package com.university.coursemanagement.service;

import com.university.coursemanagement.common.PageResponse;
import com.university.coursemanagement.dto.request.StudentRequest;
import com.university.coursemanagement.dto.response.StudentResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface StudentService {

    StudentResponse create(StudentRequest request);

    StudentResponse update(Long id, StudentRequest request);

    StudentResponse getById(Long id);

    PageResponse<StudentResponse> getAll(Pageable pageable);

    List<StudentResponse> getAllSimple();

    void delete(Long id);
}
