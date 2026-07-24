package com.university.coursemanagement.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }

    public static ResourceNotFoundException of(String resource, Object id) {
        return new ResourceNotFoundException(
                "Không tìm thấy %s với id = %s".formatted(resource, id));
    }
}
