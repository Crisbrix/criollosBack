package com.criollos.noti.domain.Gateway;

import com.criollos.noti.domain.model.EmailNotification;

public interface EmailNotificationSender {

    void send(EmailNotification notification);
}
