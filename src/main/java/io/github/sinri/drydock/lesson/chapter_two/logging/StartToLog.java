package io.github.sinri.drydock.lesson.chapter_two.logging;

import io.github.sinri.keel.logger.KeelLogLevel;
import io.github.sinri.keel.logger.issue.center.KeelIssueRecordCenter;
import io.github.sinri.keel.logger.issue.recorder.KeelIssueRecorder;
import org.apache.poi.ss.formula.eval.NotImplementedException;

public class StartToLog {
    public static void main(String[] args) {
        KeelIssueRecorder<SampleIssueRecord> issueRecorder = KeelIssueRecordCenter.outputCenter().generateIssueRecorder(
                SampleIssueRecord.topic,
                () -> new SampleIssueRecord()
                        .classification("logging", "start")
        );

        issueRecorder.notice("Default Issue Recording with output center!");

        issueRecorder.setRecordFormatter(sampleIssueRecord -> {
            sampleIssueRecord.context("format", "labeled");
        });

        issueRecorder.info(sampleIssueRecord -> sampleIssueRecord.setLabel("good"));
        issueRecorder.notice(sampleIssueRecord -> sampleIssueRecord.setLabel("common"));
        issueRecorder.warning(sampleIssueRecord -> sampleIssueRecord.setLabel("bad"));

        issueRecorder.setVisibleLevel(KeelLogLevel.NOTICE);

        issueRecorder.info(sampleIssueRecord -> sampleIssueRecord.setLabel("good, but this line would not be logged"));
        issueRecorder.notice(sampleIssueRecord -> sampleIssueRecord.setLabel("common"));
        issueRecorder.warning(sampleIssueRecord -> sampleIssueRecord.setLabel("bad"));


        try {
            throw new NotImplementedException("Sample Ended");
        } catch (Throwable e) {
            issueRecorder.exception(new RuntimeException("Let us finish here", e), "But you can try more.");
        }
    }
}
