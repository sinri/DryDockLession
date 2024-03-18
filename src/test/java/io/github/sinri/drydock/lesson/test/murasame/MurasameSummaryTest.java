package io.github.sinri.drydock.lesson.test.murasame;

import io.github.sinri.drydock.naval.raider.Privateer;
import io.github.sinri.keel.tesuto.TestUnit;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;

import javax.annotation.Nonnull;

import static io.github.sinri.keel.facade.KeelInstance.Keel;

public class MurasameSummaryTest extends Privateer {
    @Nonnull
    @Override
    protected Future<Void> prepareEnvironment() {
        return Future.succeededFuture();
    }

    @TestUnit
    public Future<Void> summary1() {
        return WebClient.create(Keel.getVertx())
                .postAbs("http://localhost:8080/api/summary-of-numbers")
                .sendJsonObject(new JsonObject()
                        .put("numbers", new JsonArray()
                                .add(1)
                                .add(2)
                                .add(3)
                        )
                )
                .compose(bufferHttpResponse -> {
                    Long data = bufferHttpResponse.bodyAsJsonObject().getLong("data");
                    getLogger().info("1+2+3=" + data);
                    if (data == 6) {
                        return Future.succeededFuture();
                    } else {
                        return Future.failedFuture("Mistook");
                    }
                }, throwable -> {
                    getLogger().exception(throwable);
                    return Future.failedFuture(throwable);
                });
    }
}
