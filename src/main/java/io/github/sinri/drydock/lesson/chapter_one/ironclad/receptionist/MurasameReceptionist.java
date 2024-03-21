package io.github.sinri.drydock.lesson.chapter_one.ironclad.receptionist;

import io.github.sinri.drydock.lesson.chapter_one.ironclad.Murasame;
import io.github.sinri.keel.logger.issue.center.KeelIssueRecordCenter;
import io.github.sinri.keel.web.http.receptionist.KeelWebFutureReceptionist;
import io.vertx.ext.web.RoutingContext;

import javax.annotation.Nonnull;

abstract public class MurasameReceptionist extends KeelWebFutureReceptionist {


    public MurasameReceptionist(RoutingContext routingContext) {
        super(routingContext);
    }

    @Nonnull
    @Override
    protected KeelIssueRecordCenter issueRecordCenter() {
        return Murasame.getIssueRecordCenterOfMurasame();
    }
}
