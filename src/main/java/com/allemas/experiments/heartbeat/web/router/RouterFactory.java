package com.allemas.experiments.heartbeat.web.router;

import com.allemas.experiments.heartbeat.core.workers.ProcessRunner;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.ThreadingModel;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

import java.util.concurrent.CompletableFuture;

public class RouterFactory {

    public static Router createRouter(Vertx vertx) {
        Router router = Router.router(vertx);

        ProcessRunner runner = new ProcessRunner();


        router.get("/prepare").respond(
                ctx -> {
                    runner.parkAThread();
                    return Future.succeededFuture(true);
                })
        ;

        router.get("/startall").respond(
                ctx -> {
                    vertx.executeBlocking(f -> {
                        runner.forceStartAll();
                    });
                    return Future.succeededFuture(new JsonObject().put("Try", "true"));
                });

        router.get("/stop").respond(
                ctx -> {
                    runner.forceStopAll();
                    return Future.succeededFuture(new JsonObject().put("Try", "true"));
                });







/*    router.get("/heath").respond(
      ctx -> {
        Map<Worker, Boolean> map = runner.checkState();

        Map<String, Boolean> result = new HashMap();

        map.forEach((worker, aBoolean) -> {
          result.put(worker.localThread.getName(), aBoolean);
        });


        ObjectMapper objectMapper = new ObjectMapper();
        String jacksonData = null;
        try {
          jacksonData = objectMapper.writeValueAsString(result);
        } catch (JsonProcessingException e) {
          throw new RuntimeException(e);
        }
        return Future.succeededFuture(jacksonData);
      }
    );*/


        return router;
    }
}
