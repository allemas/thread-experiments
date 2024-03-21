package com.allemas.experiments.heartbeat.core.executor;

import java.util.concurrent.locks.LockSupport;

public class ParkUnPark {

  public static void main(String[] args) throws InterruptedException {
    Thread t = new Thread(() -> {
      int acc = 0;
      for (int i = 1; i <= 100; i++) {
        acc += i;
      }
      System.out.println("Work finished");
      LockSupport.park();
      System.out.println(acc);
    });
    t.setName("PARK-THREAD");
    t.start();


    Thread thread = new Thread(() -> {
      while (true) {
        System.out.print(".");
        try {
          Thread.sleep(200);
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
      }
    });
    thread.start();

    Thread.sleep(3000);
    LockSupport.unpark(t);
  }

}
