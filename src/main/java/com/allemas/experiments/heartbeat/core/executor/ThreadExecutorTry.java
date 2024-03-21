package com.allemas.experiments.heartbeat.core.executor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class ThreadExecutorTry {

  LinkedBlockingQueue<Runnable> tasks = new LinkedBlockingQueue<>();
  private int capacity;

  public ThreadExecutorTry(int capacity) {
    capacity = capacity;
  }

  public void run() {
    while (true) {
      if (this.tasks.size() != 0) {
        tasks.poll().run();
      }
    }
  }


  public void submit(Runnable runnable) {
    try {
      tasks.put(runnable);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }


}

