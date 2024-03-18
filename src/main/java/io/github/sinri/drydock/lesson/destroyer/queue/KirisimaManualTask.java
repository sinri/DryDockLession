package io.github.sinri.drydock.lesson.destroyer.queue;

import io.github.sinri.keel.facade.async.KeelAsyncKit;
import io.vertx.core.Future;

import javax.annotation.Nonnull;

public class KirisimaManualTask extends KirisimaQueueTask {
    private final long taskRef;
    private final float v;

    public KirisimaManualTask(long taskRef, float v) {
        this.taskRef = taskRef;
        this.v = v;
    }

    @Nonnull
    @Override
    public String getTaskReference() {
        return "" + taskRef;
    }

    @Nonnull
    @Override
    public String getTaskCategory() {
        return getClass().getName();
    }

    @Override
    protected Future<Void> run() {
        getIssueRecorder().info("In this task, v is +" + v);
        return KeelAsyncKit.sleep((long) (10_000L * v))
                .compose(v -> {
                    getIssueRecorder().info("finished task");
                    return Future.succeededFuture();
                });
    }
}
