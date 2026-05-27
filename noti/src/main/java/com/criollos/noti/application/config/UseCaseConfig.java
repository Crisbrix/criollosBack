package com.criollos.noti.application.config;

import com.criollos.noti.application.service.SendOrderNotificationService;
import com.criollos.noti.application.usecase.SendOrderNotificationUseCase;
import com.criollos.noti.domain.Gateway.EmailNotificationSender;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public SendOrderNotificationUseCase sendOrderNotificationUseCase(
            EmailNotificationSender emailNotificationSender
    ) {
        return new SendOrderNotificationService(emailNotificationSender);
    }
}
