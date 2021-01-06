package io.github.musobarlab.javaserver;

import io.github.musobarlab.javaserver.mqtt.MQTTConnection;
import io.github.musobarlab.javaserver.verticles.HttpServerVerticle;
import io.github.musobarlab.javaserver.verticles.MainVerticle;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Hello world!
 */
public class App {

    private final static Logger LOGGER = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {

        int port = 9001;

        Vertx vertx = Vertx.vertx();

        MQTTConnection mqttConnection = new MQTTConnection("mqtt-java-client", "192.168.33.14",
                1883, "mylord", "12345");

        mqttConnection.connect();

        if (mqttConnection.isConnected()) {
            mqttConnection.subscribe("test1", (String topic, MqttMessage mqttMessage) -> {
                LOGGER.info("received message from topic {} = {}", topic, mqttMessage.toString());
            });
        }

        HttpServerVerticle httpServerVerticle = new HttpServerVerticle(port, mqttConnection);

        List<AbstractVerticle> verticles = new ArrayList<>();
        verticles.add(httpServerVerticle);
        //verticles.add(mqttVerticle);

        MainVerticle mainVerticle = new MainVerticle(verticles);


        vertx.deployVerticle(mainVerticle);


        // gracefully shutdown
        //Scheduling finalScheduling = scheduling;
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {

            // shutdown scheduling
            //finalScheduling.shutdown();
            mqttConnection.disconnect();

            //destroy vertx
            vertx.close();
        }, "shutdown-thread"));

    }
}
