package com.allemas.experiments.heartbeat;

import com.allemas.experiments.heartbeat.web.router.RouterFactory;
import io.vertx.core.*;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.impl.ContextImpl;
import io.vertx.ext.web.Router;

import java.util.concurrent.TimeUnit;

public class MainVerticle extends AbstractVerticle {


    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        HttpServer server = vertx.createHttpServer();
        Router router = RouterFactory.createRouter(vertx);
        server.requestHandler(router).listen(8080);
    }


    public static void main(final String[] args) {

        VertxOptions options = new VertxOptions();
        options.setBlockedThreadCheckInterval(5);
        options.setBlockedThreadCheckIntervalUnit(TimeUnit.SECONDS);
        options.setMaxWorkerExecuteTime(2147483647); // maximum number for 2^31 - 1
        Vertx vertx = Vertx.vertx(options);

        vertx.deployVerticle(new MainVerticle());


    }
}
