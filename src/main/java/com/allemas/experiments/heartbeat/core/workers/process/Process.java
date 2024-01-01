package com.allemas.experiments.heartbeat.core.workers.process;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public interface Process {


  public abstract Boolean isHealthy();

  public abstract Boolean isStucked();

  public abstract void run();

}
