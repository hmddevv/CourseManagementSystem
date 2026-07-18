package com.university.coursemanagement.exception;

/** Nem ra khi khong tim thay ban ghi theo id/khoa. Map sang HTTP 404. */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public static ResourceNotFoundException of(String resource, Object id) {
        return new ResourceNotFoundException(
                "Khong tim thay %s voi id = %s".formatted(resource, id));
    }
}
