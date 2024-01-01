package com.allemas.experiments.heartbeat.core.executor;

import java.util.concurrent.*;

public class ExecutorMainTry {

    public void schedule() {
        ExecutorService ex = Executors.newFixedThreadPool(10);
        Boolean test = true;

        Thread t = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1_000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("ping");
            }
        });

        ex.submit(t);

        try {
            if (!ex.awaitTermination(1, TimeUnit.SECONDS)) {
                System.out.println("????????");
                ex.shutdownNow();
            }
        } catch (InterruptedException e) {
            ex.shutdownNow();
            throw new RuntimeException(e);
        }
    }


    public static void main(final String[] arg) throws Exception {
        ExecutorMainTry s = new ExecutorMainTry();
        s.schedule();
    }

}
