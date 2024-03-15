package io.github.sinri.drydock.lesson.caravel;

import io.github.sinri.drydock.naval.melee.Caravel;
import io.github.sinri.keel.cache.KeelAsyncEverlastingCacheInterface;
import io.github.sinri.keel.facade.async.KeelAsyncKit;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;

import javax.annotation.Nonnull;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import static io.github.sinri.keel.facade.KeelInstance.Keel;
import static io.github.sinri.keel.helper.KeelHelpersInterface.KeelHelpers;

public class Yubari extends Caravel {

    private AtomicInteger counter;

    @Nonnull
    @Override
    protected Future<Void> prepareDataSources() {
        counter = new AtomicInteger(0);
        return Future.succeededFuture();
    }

    @Override
    protected Future<Void> launchAsCaravel() {
        return KeelAsyncKit.repeatedlyCall(routineResult -> {
                    counter.incrementAndGet();
                    getLogger().info("counting: "+counter.get());
                    if (counter.get() > 3) {
                        routineResult.stop();
                        return Future.succeededFuture();
                    } else {
                        return KeelAsyncKit.sleep(1000L);
                    }
                })
                .onSuccess(v -> {
                    getLogger().notice(Keel.config("yubari.hello"));
                    sink();
                });
    }

    /**
     * Fetch config `yubari.hello`.
     *
     * @see <a href="https://hellosalut.stefanbohacek.dev/hello/">Hello Salut</a> The open api to get hello in various place.
     */
    @Override
    protected Future<Void> loadRemoteConfiguration() {
        String lang = Objects.requireNonNullElse(Keel.config("yubari.lang"), "fr");
        return WebClient.create(Keel.getVertx())
                .getAbs("https://hellosalut.stefanbohacek.dev/?lang=" + lang)
                .ssl(true)
                .send()
                .compose(bufferHttpResponse -> {
                    JsonObject jsonObject = bufferHttpResponse.bodyAsJsonObject();
                    String hello = jsonObject.getString("hello");
                    Keel.getConfiguration()
                            .putAll(new JsonObject()
                                    .put("yubari", new JsonObject()
                                            .put("hello", hello)));
                    return Future.succeededFuture();
                });
    }

    public static void main(String[] args) {
        new Yubari().launch();
    }
}
