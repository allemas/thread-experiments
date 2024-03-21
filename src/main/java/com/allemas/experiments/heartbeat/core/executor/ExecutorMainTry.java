package com.allemas.experiments.heartbeat.core.executor;

import com.allemas.experiments.heartbeat.tasks.NaiveTask;
import io.vertx.core.*;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.http.HttpMethod;

import java.util.concurrent.*;

public class ExecutorMainTry {

  public void schedule() {
    ExecutorService ex = Executors.newFixedThreadPool(10);
    ex.execute(() -> {
      while (true) {
        ;
        try {
          Thread.sleep(0);
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
      }
    });

    ScheduledExecutorService executorService = Executors.newScheduledThreadPool(2);


    executorService.scheduleAtFixedRate(() -> {
      boolean terminated = ex.isTerminated();
      System.out.println(terminated);
      if (terminated == true) {
        System.out.println("I can stop checking");
        try {
          throw new InterruptedException("Stopping...");
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
      }
    }, 0, 1, TimeUnit.SECONDS);

    executorService.scheduleAtFixedRate(() -> {
      System.out.println("Try to stop");
      ex.shutdownNow();
    }, 5, 5, TimeUnit.SECONDS);
  }


  public static void main(final String[] arg) throws Exception {


    Vertx vertx = Vertx.vertx();
    DeploymentOptions options = new DeploymentOptions().setThreadingModel(ThreadingModel.VIRTUAL_THREAD);
    NaiveTask task = new NaiveTask();
    task.vertx = vertx;
    vertx.deployVerticle(task);

    System.out.println("Start HTTP SERVER");
    var options2 = new HttpClientOptions()
      .setDefaultPort(9100);
    var client = vertx.createHttpClient(options2);


   /* vertx.setPeriodic(1000, id ->{
      client.request(HttpMethod.GET, "/")
        .compose(req -> req.send().compose(HttpClientResponse::body))
        .onSuccess(h -> {
          System.out.println(h);
        }).onFailure(t -> {
          System.out.println(t.fillInStackTrace());
        });
    });
        */


  }

}
