package com.university.coursemanagement.scheduler;

import com.university.coursemanagement.entity.Enrollment;
import com.university.coursemanagement.entity.enums.EnrollmentStatus;
import com.university.coursemanagement.repository.EnrollmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class EnrollmentReminderScheduler {
    private static final Logger log = LoggerFactory.getLogger(EnrollmentReminderScheduler.class);

    private final EnrollmentRepository enrollmentRepository;
    private final int inactiveDays;

    public EnrollmentReminderScheduler(EnrollmentRepository enrollmentRepository,
                                       @Value("${app.reminder.inactive-days:7}") int inactiveDays) {
        this.enrollmentRepository = enrollmentRepository;
        this.inactiveDays = inactiveDays;
    }

    @Scheduled(cron = "${app.reminder.cron:0 0 8 * * *}")
    public void remindInactiveStudents() {
        List<Enrollment> stale = findInactiveEnrollments();
        if (stale.isEmpty()) {
            log.info("[Reminder] Không có học viên nào cần nhắc học.");
            return;
        }
        log.info("[Reminder] Có {} ghi danh không hoạt động trên {} ngày:", stale.size(), inactiveDays);
        stale.forEach(enrollment -> log.info(
                "[Reminder] Học viên '{}' - khóa học '{}' - tiến độ {}% - cập nhật lần cuối {}",
                enrollment.getStudent().getFullName(),
                enrollment.getCourse().getTitle(),
                enrollment.getProgressPercent(),
                enrollment.getUpdatedAt()));
    }

    @Transactional(readOnly = true)
    public List<Enrollment> findInactiveEnrollments() {
        LocalDateTime threshold = LocalDateTime.now().minusDays(inactiveDays);
        return enrollmentRepository.findInactiveSince(EnrollmentStatus.ACTIVE, threshold);
    }
}
