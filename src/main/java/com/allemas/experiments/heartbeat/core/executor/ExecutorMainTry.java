package com.allemas.experiments.heartbeat.core.executor;

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
    ExecutorMainTry s = new ExecutorMainTry();
    s.schedule();
  }

}
