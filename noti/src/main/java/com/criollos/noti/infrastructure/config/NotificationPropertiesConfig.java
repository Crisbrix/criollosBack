package com.criollos.noti.infrastructure.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(OrderNotificationProperties.class)
public class NotificationPropertiesConfig {
}
