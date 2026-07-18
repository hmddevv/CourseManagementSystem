package com.university.coursemanagement.exception;

/** Nem ra khi vi pham rang buoc duy nhat (email, ten danh muc...). Map sang HTTP 409. */
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }
}
