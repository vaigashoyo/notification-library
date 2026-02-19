package com.notification.infrastructure.factory;

import com.notification.domain.exception.ConfigurationException;
import com.notification.domain.exception.ErrorCode;
import com.notification.domain.model.Channel;
import com.notification.domain.port.output.NotificationSender;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.EnumMap;
import java.util.Map;

public class SenderFactory {

    private static final Logger logger = LogManager.getLogger(SenderFactory.class);

    private final Map<Channel, NotificationSender> senders = new EnumMap<>(Channel.class);

    public void registerSender(Channel channel, NotificationSender sender) {
        logger.info("Registrando sender '{}' para canal {}", sender.getProviderName(), channel);
        senders.put(channel, sender);
    }

    public NotificationSender getSender(Channel channel) {
        NotificationSender sender = senders.get(channel);
        if (sender == null) {
            logger.error("No hay sender configurado para el canal: {}", channel);
            throw new ConfigurationException(ErrorCode.C001, channel.name());
        }
        logger.debug("Sender obtenido para canal {}: {}", channel, sender.getProviderName());
        return sender;
    }

    public boolean hasSender(Channel channel) {
        return senders.containsKey(channel);
    }
}
