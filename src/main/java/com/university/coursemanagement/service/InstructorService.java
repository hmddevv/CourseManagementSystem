package com.university.coursemanagement.service;

import com.university.coursemanagement.common.PageResponse;
import com.university.coursemanagement.dto.request.InstructorRequest;
import com.university.coursemanagement.dto.response.InstructorResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface InstructorService {

    InstructorResponse create(InstructorRequest request);

    InstructorResponse update(Long id, InstructorRequest request);

    InstructorResponse getById(Long id);

    PageResponse<InstructorResponse> getAll(Pageable pageable);

    List<InstructorResponse> getAllSimple();

    void delete(Long id);
}
