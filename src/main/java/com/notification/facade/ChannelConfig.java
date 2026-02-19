package com.notification.facade;

import com.notification.domain.model.Channel;

import java.util.HashMap;
import java.util.Map;

public class ChannelConfig {

    private final Channel channel;
    private String providerName;
    private final Map<String, String> credentials = new HashMap<>();

    private ChannelConfig(Channel channel) {
        this.channel = channel;
    }

    public static ChannelConfig email() {
        return new ChannelConfig(Channel.EMAIL);
    }

    public static ChannelConfig sms() {
        return new ChannelConfig(Channel.SMS);
    }

    public static ChannelConfig push() {
        return new ChannelConfig(Channel.PUSH);
    }

    public ChannelConfig provider(String providerName) {
        this.providerName = providerName;
        return this;
    }

    public ChannelConfig host(String host) {
        credentials.put("host", host);
        return this;
    }

    public ChannelConfig port(String port) {
        credentials.put("port", port);
        return this;
    }

    public ChannelConfig username(String username) {
        credentials.put("username", username);
        return this;
    }

    public ChannelConfig password(String password) {
        credentials.put("password", password);
        return this;
    }

    public ChannelConfig from(String from) {
        credentials.put("from", from);
        return this;
    }

    public ChannelConfig credential(String key, String value) {
        credentials.put(key, value);
        return this;
    }

    public Channel getChannel() {
        return channel;
    }

    public String getProviderName() {
        return providerName;
    }

    public Map<String, String> getCredentials() {
        return Map.copyOf(credentials);
    }
}
