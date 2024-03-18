package io.github.sinri.drydock.lesson.destroyer;

import io.github.sinri.drydock.lesson.destroyer.receptionist.KirisimaReceptionist;
import io.github.sinri.drydock.naval.melee.Destroyer;
import io.github.sinri.keel.logger.issue.center.KeelIssueRecordCenter;
import io.github.sinri.keel.servant.queue.KeelQueue;
import io.github.sinri.keel.servant.queue.KeelQueueNextTaskSeeker;
import io.github.sinri.keel.servant.sundial.KeelSundialPlan;
import io.github.sinri.keel.web.http.receptionist.KeelWebReceptionistKit;
import io.vertx.core.Future;
import io.vertx.ext.web.Router;

import javax.annotation.Nonnull;
import java.util.Collection;

public class Kirisima extends Destroyer {
    private static final Kirisima instance = new Kirisima();
    private SundialManager sundialManager;

    private Kirisima() {
        super();
    }

    public static void main(String[] args) {
        instance.launch();
    }

    public static KeelIssueRecordCenter getIssueRecordCenterOfKirisima() {
        return instance.getIssueRecordCenter();
    }

    @Override
    protected Future<Void> launchAsDestroyer() {
        return Future.succeededFuture();
    }

    @Override
    public void configureHttpServerRoutes(Router router) {
        KeelWebReceptionistKit<KirisimaReceptionist> receptionistKit = new KeelWebReceptionistKit<>(
                KirisimaReceptionist.class,
                router
        );
        receptionistKit.loadPackage("io.github.sinri.drydock.lesson.destroyer.receptionist");
    }

    @Override
    public KeelQueue.SignalReader buildSignalReader() {
        return () -> Future.succeededFuture(KeelQueue.QueueSignal.RUN);
    }

    @Override
    public KeelQueueNextTaskSeeker buildQueueNextTaskSeeker() {
        return QueueManager.instance;
    }

    @Override
    public Future<Collection<KeelSundialPlan>> fetchSundialPlans() {
        return sundialManager.fetchSundialPlans();
    }

    @Nonnull
    @Override
    protected Future<Void> prepareDataSources() {
        sundialManager = new SundialManager();
        return Future.succeededFuture();
    }

    @Override
    public int configuredQueueWorkerPoolSize() {
        return 2;
    }
}
