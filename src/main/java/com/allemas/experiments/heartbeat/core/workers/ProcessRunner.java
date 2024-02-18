package com.allemas.experiments.heartbeat.core.workers;

import com.allemas.experiments.heartbeat.core.workers.process.BasicTask;
import com.allemas.experiments.heartbeat.core.workers.process.Process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProcessRunner {
  public List<Thread> process = new ArrayList<>();
  private Thread localThread;

  public void parkAThread() {
    localThread = new Thread(() -> {
      int number = 1;
      System.out.println("start");

      while (true) {
        System.out.println("PING");
        try {
          Thread.sleep(1000);
          number += 1;
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
        if (number == 5) {
          Thread.currentThread().interrupt();
          System.out.println("STOP");
        }
      }

    });
  }

  public void forceStartAll() {
    localThread.run();
  }

  public void forceStopAll() {
    localThread.interrupt();
    System.out.println("STOPPP");
  }


}
