package io.github.sinri.drydock.lesson.destroyer.queue;

import io.github.sinri.drydock.lesson.destroyer.Kirisima;
import io.github.sinri.keel.logger.issue.center.KeelIssueRecordCenter;
import io.github.sinri.keel.logger.issue.recorder.KeelIssueRecorder;
import io.github.sinri.keel.servant.queue.KeelQueueTask;
import io.github.sinri.keel.servant.queue.QueueTaskIssueRecord;

import javax.annotation.Nonnull;

abstract public class KirisimaQueueTask extends KeelQueueTask {


    @Nonnull
    @Override
    protected KeelIssueRecorder<QueueTaskIssueRecord> buildIssueRecorder() {
        return getIssueRecordCenter().generateIssueRecorder("Queue", () -> new QueueTaskIssueRecord(getTaskReference(), getTaskCategory()));
    }

    private KeelIssueRecordCenter getIssueRecordCenter() {
        return Kirisima.getIssueRecordCenterOfKirisima();
    }
}
