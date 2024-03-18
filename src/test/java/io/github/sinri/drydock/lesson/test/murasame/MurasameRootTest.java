package io.github.sinri.drydock.lesson.test.murasame;

import io.github.sinri.drydock.naval.raider.Privateer;
import io.github.sinri.keel.tesuto.TestUnit;
import io.vertx.core.Future;
import io.vertx.ext.web.client.WebClient;

import javax.annotation.Nonnull;

import static io.github.sinri.keel.facade.KeelInstance.Keel;

public class MurasameRootTest extends Privateer {
    @Nonnull
    @Override
    protected Future<Void> prepareEnvironment() {
        return Future.succeededFuture();
    }

    @TestUnit
    public Future<Void> callMurasameRoot0() {
        return WebClient.create(Keel.getVertx())
                .getAbs("http://localhost:8080/")
                .send()
                .compose(bufferHttpResponse -> {
                    return Future.succeededFuture(bufferHttpResponse.bodyAsString());
                })
                .compose(s -> {
                    getLogger().info("RESP: " + s);
                    return Future.succeededFuture();
                });
    }

    @TestUnit
    public Future<Void> callMurasameRoot1() {
        return WebClient.create(Keel.getVertx())
                .getAbs("http://localhost:8080/")
                .addQueryParam("name", "murasame")
                .send()
                .compose(bufferHttpResponse -> {
                    return Future.succeededFuture(bufferHttpResponse.bodyAsString());
                })
                .compose(s -> {
                    getLogger().info("RESP: " + s);
                    return Future.succeededFuture();
                });
    }

    @TestUnit
    public Future<Void> callMurasameRoot2() {
        return WebClient.create(Keel.getVertx())
                .getAbs("http://localhost:8080/")
                .addQueryParam("name", "murasame")
                .addQueryParam("name", "murasama")
                .send()
                .compose(bufferHttpResponse -> {
                    return Future.succeededFuture(bufferHttpResponse.bodyAsString());
                })
                .compose(s -> {
                    getLogger().info("RESP: " + s);
                    return Future.succeededFuture();
                });
    }
}
