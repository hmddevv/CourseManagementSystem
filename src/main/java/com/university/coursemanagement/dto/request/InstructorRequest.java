package com.university.coursemanagement.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record InstructorRequest(

        @NotBlank(message = "Ho ten giang vien khong duoc de trong")
        @Size(max = 150, message = "Ho ten toi da 150 ky tu")
        String fullName,

        @NotBlank(message = "Email khong duoc de trong")
        @Email(message = "Email khong hop le")
        @Size(max = 150)
        String email,

        @Size(max = 100, message = "Chuyen mon toi da 100 ky tu")
        String expertise,

        @Size(max = 1000, message = "Gioi thieu toi da 1000 ky tu")
        String bio
) {
}
