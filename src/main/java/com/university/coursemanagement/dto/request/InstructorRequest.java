package com.university.coursemanagement.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record InstructorRequest(

        @NotBlank(message = "Họ tên giảng viên không được để trống")
        @Size(max = 150, message = "Họ tên tối đa 150 ký tự")
        String fullName,

        @NotBlank(message = "Email không được để trống")
        @Email(message = "Email không hợp lệ")
        @Size(max = 150)
        String email,

        @Size(max = 100, message = "Chuyên môn tối đa 100 ký tự")
        String expertise,

        @Size(max = 1000, message = "Giới thiệu tối đa 1000 ký tự")
        String bio
) {
}
