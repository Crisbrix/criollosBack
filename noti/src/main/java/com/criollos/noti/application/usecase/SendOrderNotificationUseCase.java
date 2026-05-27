package com.criollos.noti.application.usecase;

import com.criollos.noti.application.dto.SendOrderNotificationCommand;

public interface SendOrderNotificationUseCase {

    void send(SendOrderNotificationCommand command);
}
