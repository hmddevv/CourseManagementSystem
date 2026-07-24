package com.university.coursemanagement.dto.mapper;

import com.university.coursemanagement.dto.request.InstructorRequest;
import com.university.coursemanagement.dto.response.InstructorResponse;
import com.university.coursemanagement.entity.Instructor;
import org.springframework.stereotype.Component;

@Component
public class InstructorMapper {
    public Instructor toEntity(InstructorRequest request) {
        return Instructor.builder()
                .fullName(request.fullName())
                .email(request.email())
                .expertise(request.expertise())
                .bio(request.bio())
                .build();
    }

    public void updateEntity(Instructor instructor, InstructorRequest request) {
        instructor.setFullName(request.fullName());
        instructor.setEmail(request.email());
        instructor.setExpertise(request.expertise());
        instructor.setBio(request.bio());
    }

    public InstructorResponse toResponse(Instructor instructor, long courseCount) {
        return new InstructorResponse(
                instructor.getId(),
                instructor.getFullName(),
                instructor.getEmail(),
                instructor.getExpertise(),
                instructor.getBio(),
                (int) courseCount
        );
    }
}
