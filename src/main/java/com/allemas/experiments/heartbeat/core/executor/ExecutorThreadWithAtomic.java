package com.allemas.experiments.heartbeat.core.executor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class ExecutorThreadWithAtomic {

  public static void main(String[] args) throws InterruptedException {
    AtomicBoolean start = new AtomicBoolean(true);

    Thread thread = new Thread(() -> {
      while (start.get()) {
        System.out.println("Continue...");
      }
      System.out.println("Stop");
    });

    ExecutorService ex = Executors.newFixedThreadPool(10);
    ex.submit(thread);

    Thread.sleep(1000);
    System.out.println("Try stop");
    start.set(false);
  }


}
