package io.github.sinri.drydock.lesson.destroyer;

import io.github.sinri.drydock.lesson.destroyer.queue.KirisimaRandomTask;
import io.github.sinri.keel.helper.KeelHelpersInterface;
import io.github.sinri.keel.servant.queue.KeelQueueNextTaskSeeker;
import io.github.sinri.keel.servant.queue.KeelQueueTask;
import io.vertx.core.Future;

public class QueueManager implements KeelQueueNextTaskSeeker {

    @Override
    public Future<KeelQueueTask> get() {
        float x = KeelHelpersInterface.KeelHelpers.randomHelper().getPRNG().nextFloat();
        if (x > 0.75) {
            // random generate a task
            return Future.succeededFuture(new KirisimaRandomTask(System.currentTimeMillis(), x));
        } else {
            // now no task to be generated
            return Future.succeededFuture();
        }
    }
}
