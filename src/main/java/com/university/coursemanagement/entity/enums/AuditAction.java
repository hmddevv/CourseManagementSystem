package com.university.coursemanagement.entity.enums;

/**
 * Loai thao tac ghi duoc ghi vao nhat ky. Chi ghi thao tac GHI
 * (tao / sua / xoa / doi trang thai), khong ghi thao tac doc.
 */
public enum AuditAction {
    CREATE,
    UPDATE,
    DELETE,
    PUBLISH,
    ARCHIVE,
    ENROLL,
    CANCEL
}
