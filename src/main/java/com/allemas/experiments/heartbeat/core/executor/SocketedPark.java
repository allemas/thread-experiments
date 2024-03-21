package com.allemas.experiments.heartbeat.core.executor;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.LockSupport;

class ThreadMonitor extends Thread {

  ThreadMonitor() {
    super();
  }

  private ServerSocket serverSocket;
  private Socket clientSocket;
  private PrintWriter out;
  private BufferedReader in;


  public void run() {
    AtomicBoolean isRunning = new AtomicBoolean(true);

    Thread heath = new Thread(() -> {
      try {
        System.out.println("Start...");
        serverSocket = new ServerSocket(5050);
        while (isRunning.get()) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            clientSocket = serverSocket.accept();
          out = new PrintWriter(clientSocket.getOutputStream(), true);
          in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
          String greeting = in.readLine();
          System.out.println("---> " + greeting);
          if (greeting.equals("STOP"))
            isRunning.set(false);
        }
        System.out.println("???????");

      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    });
    heath.start();

    while (isRunning.get()){
        try {
            Thread.sleep(500);
            System.out.println("ping");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    System.out.println("stop?");
    System.out.println(heath.isAlive());
  }


}



public class SocketedPark {
  public static void main(String args[]) throws Exception {
    ThreadMonitor m = new ThreadMonitor();
    m.setName("Monitor");
    m.start();

    Thread.sleep(500);
    System.out.println("?");
    Socket s = new Socket("127.0.0.1", 5050);
    PrintWriter out = new PrintWriter(s.getOutputStream(), true);
    out.println("STOP");
  }
}
