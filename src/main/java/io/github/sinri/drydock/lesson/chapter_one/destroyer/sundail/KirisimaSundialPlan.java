package io.github.sinri.drydock.lesson.chapter_one.destroyer.sundail;

import io.github.sinri.drydock.lesson.chapter_one.destroyer.Kirisima;
import io.github.sinri.keel.logger.event.KeelEventLogger;
import io.github.sinri.keel.logger.issue.center.KeelIssueRecordCenter;
import io.github.sinri.keel.servant.sundial.KeelSundialPlan;

public abstract class KirisimaSundialPlan implements KeelSundialPlan {
    private final KeelEventLogger logger;

    public KirisimaSundialPlan() {
        this.logger = getIssueRecordCenter().generateEventLogger("Sundial", keelEventLog -> {
            keelEventLog.classification("plan", key());
        });
    }

    public final KeelEventLogger getLogger() {
        return logger;
    }

    @Override
    public String key() {
        return getClass().getName();
    }

    private KeelIssueRecordCenter getIssueRecordCenter() {
        return Kirisima.getIssueRecordCenterOfKirisima();
    }
}
