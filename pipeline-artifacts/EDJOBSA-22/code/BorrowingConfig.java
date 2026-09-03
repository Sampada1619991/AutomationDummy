package com.library.borrowing.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.time.Clock;

/**
 * Configuration for the borrowing module.
 */
@Configuration
public class BorrowingConfig {
    
    /**
     * Provide a Clock bean for dependency injection.
     * This aids in testing (can be mocked) and allows timezone flexibility.
     */
    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }
}

