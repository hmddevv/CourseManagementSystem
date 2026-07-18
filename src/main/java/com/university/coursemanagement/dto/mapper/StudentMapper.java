package com.university.coursemanagement.dto.mapper;

import com.university.coursemanagement.dto.request.StudentRequest;
import com.university.coursemanagement.dto.response.StudentResponse;
import com.university.coursemanagement.entity.Student;
import org.springframework.stereotype.Component;

@Component
public class StudentMapper {

    public Student toEntity(StudentRequest request) {
        return Student.builder()
                .fullName(request.fullName())
                .email(request.email())
                .phone(request.phone())
                .build();
    }

    public void updateEntity(Student student, StudentRequest request) {
        student.setFullName(request.fullName());
        student.setEmail(request.email());
        student.setPhone(request.phone());
    }

    public StudentResponse toResponse(Student student, long enrollmentCount) {
        return new StudentResponse(
                student.getId(),
                student.getFullName(),
                student.getEmail(),
                student.getPhone(),
                (int) enrollmentCount
        );
    }
}
