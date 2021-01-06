package io.github.musobarlab.javaserver.verticles;

import io.vertx.core.AbstractVerticle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class MainVerticle extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainVerticle.class);

    private List<AbstractVerticle> verticles;

    public MainVerticle(List<AbstractVerticle> verticles) {
        this.verticles = verticles;
    }

    @Override
    public void start() throws Exception {
        verticles.forEach(verticle -> vertx.deployVerticle(verticle));
    }

    @Override
    public void stop() throws Exception {
        LOGGER.info("Closing Main Verticle");
    }

}
