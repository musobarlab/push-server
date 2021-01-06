package io.github.musobarlab.javaserver.verticles;

import io.github.musobarlab.javaserver.EmptyJson;
import io.github.musobarlab.javaserver.Notification;
import io.github.musobarlab.javaserver.Response;
import io.github.musobarlab.javaserver.mqtt.MQTTConnection;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class HttpServerVerticle extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpServerVerticle.class);

    private final int port;
    private final MQTTConnection mqttConnection;

    public HttpServerVerticle(int port, MQTTConnection mqttConnection) {
        this.port = port;
        this.mqttConnection = mqttConnection;
    }

    @Override
    public void start(Promise<Void> promise) throws Exception {
        LOGGER.info("start http server");

        Future<Void> httpServerFuture = startHttpServer();

        httpServerFuture.onComplete(r -> {
            if (r.succeeded()) {
                LOGGER.info("server listen on port " + port);
                promise.complete();
            } else {
                LOGGER.error("server failed to start : " + r.cause().getMessage());
                promise.fail(r.cause());
            }
        });

    }

    @Override
    public void stop() throws Exception {
        LOGGER.info("stopping http server");
    }

    private Future<Void> startHttpServer() {
        Promise<Void> promise = Promise.promise();

        HttpServerOptions httpServerOptions = new HttpServerOptions();
        httpServerOptions.setPort(this.port);

        Router baseRouter = Router.router(vertx);

        // enables the reading of the request body for all routes
        baseRouter.route().handler(BodyHandler.create());

        //baseRouter.mountSubRouter("/api", restHandler.getRouters());

        baseRouter.get("/").handler(this::indexHandler);
        baseRouter.post("/send-notif").handler(this::sendNotif);

        HttpServer httpServer = vertx.createHttpServer(httpServerOptions);

        httpServer.requestHandler(baseRouter);

        httpServer.listen((result) -> {
            if (result.succeeded()) {
                promise.complete();
            } else {
                promise.fail(result.cause());
            }
        });

        return promise.future();
    }


    private void indexHandler(RoutingContext context) {
        context.response()
                .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .setStatusCode(200)
                .end(Json.encodePrettily(new Response(200, true, new EmptyJson(),
                        "server up and running")));
    }

    // curl --header "Content-Type: application/json" \
    //   --request POST \
    //   --data '{"header":"Absen Harian","content":"kamu belum absen ya kayaknya..."}' \
    //   http://localhost:9001/send-notif
    private void sendNotif(RoutingContext context) {
        if (context.getBody().length() <= 0) {
            context.response()
                    .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                    .setStatusCode(400)
                    .end(Json.encodePrettily(new Response(400, false, new EmptyJson(),
                            "invalid data")));
            return;
        }

        Notification notification = Notification.fromBuffer(context.getBody());
        notification.setDate(new Date());
        byte[] notificationMessage = notification.toJson();

        if (!mqttConnection.isConnected()) {
            LOGGER.error("mqtt does not connect");
        } else {
            mqttConnection.publish("test1", notificationMessage);
        }

        context.response()
                .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .setStatusCode(200)
                .end(Json.encodePrettily(new Response(200, true, new EmptyJson(),
                        "success send notification")));

    }

}
