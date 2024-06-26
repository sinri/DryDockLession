package io.github.sinri.drydock.lesson.chapter_one.destroyer.sundail;

import io.github.sinri.drydock.lesson.chapter_one.destroyer.QueueManager;
import io.github.sinri.keel.core.KeelCronExpression;
import io.vertx.core.Future;

import java.util.Calendar;

public class ByMinuteSundialPlan extends KirisimaSundialPlan {

    @Override
    public KeelCronExpression cronExpression() {
        return new KeelCronExpression("* * * * *");
    }

    @Override
    public Future<Void> execute(Calendar calendar) {
        getLogger().info("Now, " + QueueManager.currentTasksInQueue() + " tasks pending in queue.");
        return Future.succeededFuture();
    }
}
