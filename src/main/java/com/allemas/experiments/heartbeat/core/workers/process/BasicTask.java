package com.allemas.experiments.heartbeat.core.workers.process;


import java.util.ArrayList;
import java.util.List;

public class BasicTask extends Thread implements Process {
  static final int NEW = 0;
  private static final int RUNNING = 3;     // runnable-mounted
  private static final int ERROR = 4;
  private static final int TERMINATED = 99;  // final state

  private volatile int state;

  protected List<Exception> exceptions = new ArrayList<>();


  @Override
  public void run() {
    this.state = RUNNING;
    while (!Thread.currentThread().isInterrupted()) {
      System.out.println("Ping");
      try {
        Thread.sleep(2000);
      } catch (InterruptedException e) {
        this.state = ERROR;
        exceptions.add(e);
        throw new RuntimeException(e);
      }
    }
  }

  @Override
  public Boolean isHealthy() {
    return this.isAlive() && this.exceptions.isEmpty();
  }



  @Override
  public Boolean isStucked() {
    return (state == ERROR);
  }

}
