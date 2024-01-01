package com.allemas.experiments.heartbeat.core.workers;

public class Task {
  private String name;
  private Thread localThread;


  private Task(String name, Thread localThread) {
    this.name = name;
    this.localThread = localThread;
  }


  public static Task create(String name, Thread process) {
    return new Task(name, process);
  }
}
