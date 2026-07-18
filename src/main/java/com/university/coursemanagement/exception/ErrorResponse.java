package com.university.coursemanagement.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Dinh dang loi thong nhat cho toan bo API.
 *
 * @param timestamp thoi diem xay ra loi
 * @param status    ma HTTP
 * @param error     ten loi (Not Found, Conflict...)
 * @param message   thong diep than thien
 * @param path      duong dan gay loi
 * @param fieldErrors danh sach loi validate theo tung truong (neu co)
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path,
        List<FieldError> fieldErrors
) {
    public record FieldError(String field, String message) {
    }
}
