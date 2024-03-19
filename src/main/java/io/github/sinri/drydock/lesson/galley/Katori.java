package io.github.sinri.drydock.lesson.galley;

import io.github.sinri.drydock.naval.melee.Galley;
import io.github.sinri.keel.facade.async.KeelAsyncKit;
import io.vertx.core.Future;

import java.util.Objects;

import static io.github.sinri.keel.facade.KeelInstance.Keel;

public class Katori extends Galley {
    @Override
    protected Future<Void> launchAsGalley() {
        // read an integer from config
        int countingDown;
        // Method 1:
        //countingDown = Objects.requireNonNull(Keel.getConfiguration().readAsInteger("katori", "counting_down"));
        // Method 2:
        String s = Keel.config("katori.counting_down");
        Objects.requireNonNull(s);
        countingDown = Integer.parseInt(s);

        // counting down, each for 1 second
        return KeelAsyncKit.stepwiseCall(countingDown, i -> {
                    getLogger().info("counting down as " + (countingDown - i));
                    // sleep 1 second
                    return KeelAsyncKit.sleep(1_000L);
                })
                .onFailure(throwable -> {
                    // terminate with 1
                    this.shipwreck(throwable);
                })
                .onSuccess(v -> {
                    getLogger().info("Boom!");
                    // terminate with 0
                    this.sink();
                });
    }

    @Override
    protected void loadLocalConfiguration() {
        // load local config
        Keel.getConfiguration().loadPropertiesFile("config.properties");
    }

    @Override
    protected Future<Void> loadRemoteConfiguration() {
        // do not load from remote
        return Future.succeededFuture();
    }

    public static void main(String[] args) {
        new Katori().launch();
    }
}
