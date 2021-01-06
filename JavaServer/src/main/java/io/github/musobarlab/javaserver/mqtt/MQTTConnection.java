package io.github.musobarlab.javaserver.mqtt;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;

public class MQTTConnection implements MqttCallback, IMqttActionListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(MQTTConnection.class);

    // Quality of Service = Exactly once
    // receive all messages exactly once
    public static final int QUALITY_OF_SERVICE = 2;
    public static final String ENCODING = "UTF-8";

    private String host;
    private int port;
    private String username;
    private String password;

    protected String name;
    protected String clientId;
    protected MqttAsyncClient client;
    protected MemoryPersistence memoryPersistence;
    protected IMqttToken connectToken;
    protected IMqttToken subscribeToken;


    public MQTTConnection(String name, String host, int port, String username, String password) {
        this.name = name;
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void connect() {
        LOGGER.info("connecting to MQTT broker");
        try {
            MqttConnectOptions options =
                    new MqttConnectOptions();
            options.setUserName(username);
            options.setPassword(password.toCharArray());
            // Replace with ssl:// and work with TLS/SSL
            // best practices in a
            // production environment
            memoryPersistence = new MemoryPersistence();
            String serverURI = String.format("tcp://%s:%d", host, port);

            clientId = MqttAsyncClient.generateClientId();
            client = new MqttAsyncClient(serverURI, clientId, memoryPersistence);

            //use this instance as the callback
            client.setCallback(this);

            connectToken = client.connect(options, null, this);

            LOGGER.info("connected to MQTT broker");
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        LOGGER.info("disconnecting from mqtt broker");
        if (isConnected()) {
            try {
                client.disconnect();
                client.close();
            } catch (MqttException e) {
                LOGGER.error("error closing MQTT Connection");
            }
        }
    }

    public boolean isConnected() {
        return (client != null) &&
                (client.isConnected());
    }

    public MessageActionListener publish(String topic, String messageText) {
        try {
            byte[] bytesMessage = messageText.getBytes(ENCODING);
            return publish(topic, bytesMessage);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public MessageActionListener publish(String topic, byte[] messageByte) {
        try {
            MqttMessage message;
            message = new MqttMessage(messageByte);
            String userContext = "ListeningMessage";
            MessageActionListener actionListener = new MessageActionListener(topic, messageByte, userContext);

            client.publish(topic, message, userContext, actionListener);

            return actionListener;
        } catch (MqttException e) {
            e.printStackTrace();
            return null;
        }
    }

    // TODO
    public void subscribe(String topic, IMqttMessageListener callback) {
        LOGGER.info(String.format("%s successfully subscribed", name));
        try {
            subscribeToken = client.subscribe(topic, QUALITY_OF_SERVICE, null, this, callback);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSuccess(IMqttToken iMqttToken) {
        if (iMqttToken.equals(connectToken)) {
            LOGGER.info(String.format("%s successfully connected", name));

        } else if (iMqttToken.equals(subscribeToken)) {
            LOGGER.info(String.format("%s subscribed to the %s topic", name, ""));
//            publishTextMessage( String.format(
//                    "%s is listening.", name));
        }
    }

    @Override
    public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
        throwable.printStackTrace();
    }

    @Override
    public void connectionLost(Throwable throwable) {

    }

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {

    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }
}
