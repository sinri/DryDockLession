package io.github.sinri.drydock.lesson.chapter_two.logging;

import io.github.sinri.keel.logger.KeelLogLevel;
import io.github.sinri.keel.logger.event.KeelEventLogger;
import io.vertx.core.json.JsonObject;
import org.apache.poi.ss.formula.eval.NotImplementedException;

import static io.github.sinri.keel.facade.KeelInstance.Keel;

public class QuickLogEvents {
    public static void main(String[] args) {
        KeelEventLogger eventLogger = Keel.getLogger();
        //Or use this to create one : KeelIssueRecordCenter.outputCenter().generateEventLogger("sample");


        eventLogger.info("Let start with output center!");

        eventLogger.setVisibleLevel(KeelLogLevel.NOTICE);

        eventLogger.info("here is an information but you cannot see it.");
        eventLogger.notice("but you can see this notice");
        eventLogger.warning("warning comes", new JsonObject().put("directly", "use json object"));
        eventLogger.error("error with context handler", ctx -> ctx.put("modify", "this"));
        eventLogger.fatal(eventLog -> eventLog
                .classification("fatal", "unknown reason")
                .message("long live sinri!")
                .context("a", 1)
                .context("b", 2)
        );

        try {
            throw new NotImplementedException("Sample Ended");
        } catch (Throwable e) {
            eventLogger.exception(new RuntimeException("Let us finish here", e), "But you can try more.");
        }
    }
}
