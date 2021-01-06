package io.github.musobarlab.javaserver.mqtt;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageActionListener implements IMqttActionListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageActionListener.class);

    protected final byte[] message;
    protected final String topic;
    protected final String userContext;

    public MessageActionListener(
            String topic,
            byte[] message,
            String userContext) {
        this.topic = topic;
        this.message = message;
        this.userContext = userContext;
    }

    @Override
    public void onSuccess(IMqttToken asyncActionToken) {
        if ((asyncActionToken != null) && asyncActionToken.getUserContext().equals(userContext)) {
            LOGGER.info(String.format("Message '%s' published to topic '%s'",
                    new String(message), topic));
        }
    }

    @Override
    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
        exception.printStackTrace();
    }
}
