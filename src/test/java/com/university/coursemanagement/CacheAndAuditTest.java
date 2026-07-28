package com.university.coursemanagement;

import com.university.coursemanagement.config.CacheConfig;
import com.university.coursemanagement.dto.request.CategoryRequest;
import com.university.coursemanagement.entity.enums.AuditAction;
import com.university.coursemanagement.repository.AuditLogRepository;
import com.university.coursemanagement.service.AuditLogService;
import com.university.coursemanagement.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class CacheAndAuditTest {
    @Autowired CategoryService categoryService;
    @Autowired AuditLogService auditLogService;
    @Autowired AuditLogRepository auditLogRepository;
    @Autowired CacheManager cacheManager;

    @Test
    void getAllSimple_shouldBeCached_andEvictedAfterWrite() {
        Cache cache = cacheManager.getCache(CacheConfig.CATEGORIES_CACHE);
        assertThat(cache).isNotNull();
        cache.clear();

        categoryService.getAllSimple();
        assertThat(cache.get(org.springframework.cache.interceptor.SimpleKey.EMPTY)).isNotNull();

        categoryService.create(new CategoryRequest("Cache-Cat-" + System.nanoTime(), "mo ta"));
        assertThat(cache.get(org.springframework.cache.interceptor.SimpleKey.EMPTY)).isNull();
    }

    @Test
    void createCategory_shouldBeRecordedInAuditLog() {
        long before = auditLogRepository.count();

        var created = categoryService.create(new CategoryRequest("Audit-Cat-" + System.nanoTime(), "mo ta"));

        assertThat(auditLogRepository.count()).isGreaterThan(before);

        var logs = auditLogService.search("Category", AuditAction.CREATE, PageRequest.of(0, 20));
        assertThat(logs.getContent())
                .anySatisfy(entry -> {
                    assertThat(entry.entityId()).isEqualTo(created.id());
                    assertThat(entry.action()).isEqualTo(AuditAction.CREATE);
                    assertThat(entry.actor()).isNotBlank();
                    assertThat(entry.detail()).contains("create");
                });
    }

    @Test
    void readOperations_shouldNotBeAudited() {
        categoryService.getAllSimple();
        long afterFirstRead = auditLogRepository.count();

        categoryService.getAll(PageRequest.of(0, 5));

        assertThat(auditLogRepository.count()).isEqualTo(afterFirstRead);
    }
}
