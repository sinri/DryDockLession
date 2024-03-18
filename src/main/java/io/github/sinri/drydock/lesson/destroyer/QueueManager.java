package io.github.sinri.drydock.lesson.destroyer;

import io.github.sinri.drydock.lesson.destroyer.queue.KirisimaManualTask;
import io.github.sinri.keel.servant.queue.KeelQueueNextTaskSeeker;
import io.github.sinri.keel.servant.queue.KeelQueueTask;
import io.vertx.core.Future;

import javax.annotation.Nullable;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class QueueManager implements KeelQueueNextTaskSeeker {
    public final static QueueManager instance = new QueueManager();

    private final Queue<KirisimaManualTask> tasks = new ConcurrentLinkedQueue<>();

    private QueueManager() {

    }

    public static long enqueueManaualTask(float v) {
        long taskRef = System.currentTimeMillis();
        KirisimaManualTask kirisimaManualTask = new KirisimaManualTask(taskRef, v);
        instance.tasks.add(kirisimaManualTask);
        return taskRef;
    }

    @Override
    public Future<KeelQueueTask> get() {
        @Nullable KirisimaManualTask task = tasks.poll();
        return Future.succeededFuture(task);
    }
}
