package com.notification.facade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NotificationConfig {

    private final List<ChannelConfig> channels = new ArrayList<>();

    private NotificationConfig() {
    }

    public static NotificationConfig create() {
        return new NotificationConfig();
    }

    public NotificationConfig channel(ChannelConfig channelConfig) {
        channels.add(channelConfig);
        return this;
    }

    public List<ChannelConfig> getChannels() {
        return Collections.unmodifiableList(channels);
    }
}
