package com.allemas.experiments.heartbeat.core.executor;

import javax.security.auth.callback.Callback;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Supplier;

class MyThread extends Thread {

  // Initially initializing states using boolean methods
  boolean waiting = true;
  boolean ready = false;

  // Constructor of this class
  MyThread() {
  }

  // Methods of this class are as follows:

  // Method 1
  synchronized void startWait() {
    try {
      while (!ready)
        wait();
    } catch (InterruptedException exc) {
      System.out.println("wait() interrupted");
    }
  }

  // Method 2
  synchronized void notice() {
    ready = true;
    notify();
  }

  // Method 3
  // To run threads when called using start()
  public void run() {

    // Getting the name of current thread
    // using currentThread() and getName() methods
    String thrdName = Thread.currentThread().getName();

    // Print the corresponding thread
    System.out.println(thrdName + " starting.");

    // While the thread is in waiting state
    while (waiting)
      System.out.println("waiting:" + waiting);

    // Display message
    System.out.println("waiting...");

    // calling the Method1
    startWait();

    // Try block to check for exceptions
    try {

      // Making thread to pause execution for a
      // certain time of 1 second using sleep() method
      Thread.sleep(1000);
      System.out.println("OUT OF SLEEEEEEP");
    }

    // Catch block to handle the exceptions
    catch (Exception exc) {

      // Display if interrupted
      System.out.println(thrdName + " interrupted.");
    }

    // Else display the thread is terminated.
    System.out.println(thrdName + " terminating.");
  }
}


public class ThreadServices {

  // Method 1
  // To get the thread status
  static void showThreadStatus(Thread thrd) {
    System.out.println(thrd.getName()
      + "  Alive:=" + thrd.isAlive()
      + " State:=" + thrd.getState());
  }

  // Method 2
  // Main driver method
  public static void main(String args[]) throws Exception {

    // Creating an object of our thread class
    // in the main() method
    MyThread thrd = new MyThread();

    // Setting the name for the threads
    // using setname() method
    thrd.setName("MyThread #1");

    // getting the status of current thread
    showThreadStatus(thrd);

    // Starting the thread which automatically invokes
    // the run() method for the thread
    thrd.start();

    // Similarly repeating the same
    Thread.sleep(50);
    showThreadStatus(thrd);

    // here notice we change the flag value
    // that is no more in waiting state now
    thrd.waiting = false;

    Thread.sleep(500);
    showThreadStatus(thrd);
    thrd.notice();

    showThreadStatus(thrd);

    while (thrd.isAlive())   // Calling the method
      Thread.sleep(50);
      showThreadStatus(thrd);
  }
}
