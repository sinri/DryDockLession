package io.github.sinri.drydock.lesson.chapter_one.destroyer;

import io.github.sinri.drydock.lesson.chapter_one.destroyer.sundail.ByMinuteSundialPlan;
import io.github.sinri.keel.servant.sundial.KeelSundialPlan;
import io.vertx.core.Future;

import java.util.ArrayList;
import java.util.Collection;

public class SundialManager {
    public Future<Collection<KeelSundialPlan>> fetchSundialPlans() {
        Collection<KeelSundialPlan> sundialPlans = new ArrayList<>();
        ByMinuteSundialPlan byMinuteSundialPlan = new ByMinuteSundialPlan();
        sundialPlans.add(byMinuteSundialPlan);
        return Future.succeededFuture(sundialPlans);
    }
}
