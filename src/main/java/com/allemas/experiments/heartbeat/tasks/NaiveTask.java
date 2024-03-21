package com.allemas.experiments.heartbeat.tasks;

import com.allemas.experiments.heartbeat.core.protocol.ThemeParkTask;
import io.vertx.core.*;
import io.vertx.core.http.HttpServerRequest;

public class NaiveTask implements ThemeParkTask, Verticle {

  public Vertx vertx;

  @Override
  public Vertx getVertx() {
    return null;
  }


  @Override
  public void init(Vertx vertx, Context context) {
  }

  @Override
  public void start(Promise<Void> promise) throws Exception {
    var serv = this.vertx.createHttpServer().requestHandler(httpServerRequest -> {
      System.out.println(httpServerRequest.headers());
      System.out.println("ON PASSE ICI");
      httpServerRequest.response().end("OK");
    }).listen(9100);

    vertx.setPeriodic(2000, id -> {
      System.out.println("COUCOU");
    });


  }

  @Override
  public void stop(Promise<Void> promise) throws Exception {

  }
}


