package com.university.coursemanagement.entity.enums;

/**
 * Trang thai ghi danh cua hoc vien trong mot khoa hoc.
 * <ul>
 *   <li>ACTIVE    - dang hoc</li>
 *   <li>COMPLETED - da hoan thanh (progress = 100%)</li>
 *   <li>CANCELLED - da huy ghi danh</li>
 * </ul>
 */
public enum EnrollmentStatus {
    ACTIVE,
    COMPLETED,
    CANCELLED
}
