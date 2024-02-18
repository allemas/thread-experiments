package com.allemas.experiments.heartbeat.core.executor;

import com.allemas.experiments.heartbeat.core.workers.Task;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

public class HeartbeatExecutor {

  public static void main(String[] args) throws InterruptedException {

    Runnable server = (() -> {
      System.out.println("Pong");
    });

    Runnable clientBeat = (() -> {
      System.out.println("Ping");
      server.run();
    });

    AtomicBoolean atomicBoolean = new AtomicBoolean(true);

    Thread thread = new Thread(() -> {

      Boolean isHealthy = false;

      Timer t = new Timer();
      t.schedule(new TimerTask() {
        @Override
        public void run() {
          clientBeat.run();
        }
      }, 0, 3000);

      while (atomicBoolean.get()) {
        System.out.println("????");
        try {
          Thread.sleep(5000);
          isHealthy = true;
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }

      }
    });

    thread.run();
    Thread.sleep(3000);
    atomicBoolean.set(false);


  }


}
