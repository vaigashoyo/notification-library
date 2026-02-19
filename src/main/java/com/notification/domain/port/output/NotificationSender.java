package com.notification.domain.port.output;

import com.notification.domain.model.Channel;
import com.notification.domain.model.Notification;
import com.notification.domain.model.NotificationResult;
import com.notification.domain.model.ProviderConfig;

public interface NotificationSender {

    NotificationResult send(Notification notification);

    Channel getChannel();

    String getProviderName();

    boolean supports(Channel channel);

    void configure(ProviderConfig config);
}
