package io.github.sinri.drydock.lesson.destroyer.queue;

import io.vertx.core.Future;

import javax.annotation.Nonnull;

public class KirisimaRandomTask extends KirisimaQueueTask {
    private final long taskRef;
    private final float v;

    public KirisimaRandomTask(long taskRef, float v) {
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
        getIssueRecorder().info("v is +" + v);
        return Future.succeededFuture();
    }
}
