package io.github.sinri.drydock.lesson.frigate;

import io.github.sinri.drydock.naval.ranged.Frigate;
import io.github.sinri.keel.servant.queue.KeelQueue;
import io.github.sinri.keel.servant.queue.KeelQueueNextTaskSeeker;
import io.github.sinri.keel.servant.sundial.KeelSundial;
import io.github.sinri.keel.servant.sundial.KeelSundialPlan;
import io.vertx.core.Future;

import javax.annotation.Nonnull;
import java.util.Collection;

public class Simusyu extends Frigate {
    @Nonnull
    @Override
    protected Future<Void> prepareDataSources() {
        return Future.succeededFuture();
    }

    @Override
    protected Future<Void> launchAsFrigate() {
        return Future.succeededFuture();
    }

    @Override
    public KeelQueue.SignalReader buildSignalReader() {
        return () -> Future.succeededFuture(KeelQueue.QueueSignal.RUN);
    }

    @Override
    public KeelQueue buildQueue() {
        return null;
    }

    @Override
    public KeelQueueNextTaskSeeker buildQueueNextTaskSeeker() {
        return null;
    }

    @Override
    public KeelSundial buildSundial() {
        return null;
    }

    @Override
    public Future<Collection<KeelSundialPlan>> fetchSundialPlans() {
        return null;
    }
}
