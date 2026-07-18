package com.university.coursemanagement.exception;

/**
 * Nem ra khi vi pham quy tac nghiep vu (khoa hoc da day, publish khi chua co bai
 * hoc, ghi danh trung...). Map sang HTTP 400/409 tuy ngu canh - o day dung 409/400.
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}
