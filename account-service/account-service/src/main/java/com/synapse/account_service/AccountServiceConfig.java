package com.synapse.account_service;

import java.util.concurrent.Executor;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@EnableAsync
@EnableJpaAuditing
@ComponentScan
@EnableAutoConfiguration
public class AccountServiceConfig {
    
    @Bean(name = "signupTaskExecutor")
    public Executor signupTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("Signup-");
        executor.initialize();
        return executor;
    }
}
