package io.github.sinri.drydock.lesson.destroyer.receptionist;

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
        return null;
    }
}
