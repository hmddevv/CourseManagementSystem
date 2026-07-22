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

/**
 * Job dinh ky nhac hoc vien lau khong hoc.
 *
 * <p>Chay 8h sang moi ngay: tim cac ghi danh con ACTIVE nhung khong co thay doi
 * tien do trong {@code app.reminder.inactive-days} ngay va ghi log nhac nho.</p>
 *
 * <p>Co y KHONG gui email that: se phu thuoc may chu SMTP, lam buoi demo de hong
 * va khien test cham. Diem can trinh bay o day la co che {@code @Scheduled}, con
 * viec doi tu ghi log sang gui email chi la thay phan than cua ham.</p>
 *
 * <p>Han che da biet: neu trien khai nhieu instance, moi instance deu chay job nay
 * nen mot hoc vien co the bi nhac nhieu lan. Giai phap dung la khoa phan tan
 * (ShedLock / Quartz cluster) hoac day viec vao hang doi.</p>
 */
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
            log.info("[Reminder] Khong co hoc vien nao can nhac hoc.");
            return;
        }
        log.info("[Reminder] Co {} ghi danh khong hoat dong tren {} ngay:", stale.size(), inactiveDays);
        stale.forEach(enrollment -> log.info(
                "[Reminder] Hoc vien '{}' - khoa hoc '{}' - tien do {}% - cap nhat lan cuoi {}",
                enrollment.getStudent().getFullName(),
                enrollment.getCourse().getTitle(),
                enrollment.getProgressPercent(),
                enrollment.getUpdatedAt()));
    }

    /**
     * Tach rieng de test goi truc tiep duoc ma khong phai cho den 8h sang.
     * {@code readOnly} vi job chi doc du lieu.
     */
    @Transactional(readOnly = true)
    public List<Enrollment> findInactiveEnrollments() {
        LocalDateTime threshold = LocalDateTime.now().minusDays(inactiveDays);
        return enrollmentRepository.findInactiveSince(EnrollmentStatus.ACTIVE, threshold);
    }
}
