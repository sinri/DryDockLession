package io.github.sinri.drydock.lesson.test.kirisima;

import io.github.sinri.drydock.naval.raider.Privateer;
import io.github.sinri.keel.facade.async.FutureForRange;
import io.github.sinri.keel.facade.async.KeelAsyncKit;
import io.github.sinri.keel.tesuto.TestUnit;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;

import javax.annotation.Nonnull;

import static io.github.sinri.keel.facade.KeelInstance.Keel;

public class KirisimaAddTaskTest extends Privateer {
    @Nonnull
    @Override
    protected Future<Void> prepareEnvironment() {
        return Future.succeededFuture();
    }

    @TestUnit
    public Future<Void> addTask() {
        return KeelAsyncKit.stepwiseCall(new FutureForRange.Options().setStart(10).setStep(-2).setEnd(0), i -> {
            return WebClient.create(Keel.getVertx())
                    .postAbs("http://localhost:8080/queue/add-task")
                    .sendJsonObject(new JsonObject()
                            .put("v", i * 1.0)
                    )
                    .compose(bufferHttpResponse -> {
                        Long taskRef = bufferHttpResponse.bodyAsJsonObject().getLong("data");
                        getLogger().info("task: " + taskRef);
                        return Future.succeededFuture();
                    });
        });

    }
}