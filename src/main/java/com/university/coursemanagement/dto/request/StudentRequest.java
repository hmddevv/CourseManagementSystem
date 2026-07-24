package com.university.coursemanagement.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record StudentRequest(

        @NotBlank(message = "Họ tên học viên không được để trống")
        @Size(max = 150, message = "Họ tên tối đa 150 ký tự")
        String fullName,

        @NotBlank(message = "Email không được để trống")
        @Email(message = "Email không hợp lệ")
        @Size(max = 150)
        String email,

        @Pattern(regexp = "^$|^[0-9+\\-\\s]{8,20}$", message = "Số điện thoại không hợp lệ")
        String phone
) {
}
