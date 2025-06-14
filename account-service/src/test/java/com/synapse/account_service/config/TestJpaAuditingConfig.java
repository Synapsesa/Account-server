package com.synapse.account_service.config;

import java.util.Optional;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@TestConfiguration
@EnableJpaAuditing
public class TestJpaAuditingConfig {

    // @CreatedBy, @LastModifiedBy를 테스트하기 위해 임시 AuditorAware 빈을 등록합니다.
    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> Optional.of("test_user");
    }
}
