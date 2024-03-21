package io.github.sinri.drydock.lesson.chapter_two.logging;

import io.github.sinri.keel.logger.issue.record.BaseIssueRecord;

import javax.annotation.Nonnull;

public class SampleIssueRecord extends BaseIssueRecord<SampleIssueRecord> {
    public static final String topic = "sample";

    @Nonnull
    @Override
    public String topic() {
        return topic;
    }

    @Nonnull
    @Override
    public SampleIssueRecord getImplementation() {
        return this;
    }

    public SampleIssueRecord setLabel(String label) {
        this.attribute("label", label);
        return this;
    }
}
