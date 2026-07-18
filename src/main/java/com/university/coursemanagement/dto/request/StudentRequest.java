package com.university.coursemanagement.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record StudentRequest(

        @NotBlank(message = "Ho ten hoc vien khong duoc de trong")
        @Size(max = 150, message = "Ho ten toi da 150 ky tu")
        String fullName,

        @NotBlank(message = "Email khong duoc de trong")
        @Email(message = "Email khong hop le")
        @Size(max = 150)
        String email,

        @Pattern(regexp = "^$|^[0-9+\\-\\s]{8,20}$", message = "So dien thoai khong hop le")
        String phone
) {
}
