package io.github.sinri.drydock.lesson.chapter_one.destroyer.receptionist;

import io.github.sinri.drydock.lesson.chapter_one.destroyer.Kirisima;
import io.github.sinri.keel.logger.issue.center.KeelIssueRecordCenter;
import io.github.sinri.keel.web.http.receptionist.KeelWebFutureReceptionist;
import io.vertx.ext.web.RoutingContext;

import javax.annotation.Nonnull;

public abstract class KirisimaReceptionist extends KeelWebFutureReceptionist {


    public KirisimaReceptionist(RoutingContext routingContext) {
        super(routingContext);
    }

    @Nonnull
    @Override
    protected KeelIssueRecordCenter issueRecordCenter() {
        return Kirisima.getIssueRecordCenterOfKirisima();
    }
}
