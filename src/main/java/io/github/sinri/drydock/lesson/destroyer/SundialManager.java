package io.github.sinri.drydock.lesson.destroyer;

import io.github.sinri.drydock.lesson.destroyer.sundail.ByMinuteSundialPlan;
import io.github.sinri.keel.servant.sundial.KeelSundialPlan;
import io.vertx.core.Future;

import java.util.ArrayList;
import java.util.Collection;

public class SundialManager {
    public Future<Collection<KeelSundialPlan>> fetchSundialPlans() {
        Collection<KeelSundialPlan> sundialPlans = new ArrayList<>();
        ByMinuteSundialPlan byMinuteSundialPlan = new ByMinuteSundialPlan();
        sundialPlans.add(byMinuteSundialPlan);

        var xxx = Kirisima.getIssueRecordCenterOfKirisima().generateIssueRecorderForEventLogger("xxx");
        xxx.info("aaa");
        System.out.println("xxx " + xxx.getVisibleLevel()
                + " adapter " + xxx.issueRecordCenter().getAdapter()
                + " closed " + xxx.issueRecordCenter().getAdapter().isClosed()
                + " stopped " + xxx.issueRecordCenter().getAdapter().isStopped()
        );



        return Future.succeededFuture(sundialPlans);
    }
}
