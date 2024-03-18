package io.github.sinri.drydock.lesson.destroyer.sundail;

import io.github.sinri.keel.core.KeelCronExpression;

import java.util.Calendar;

public class ByMinuteSundialPlan extends KirisimaSundialPlan {

    @Override
    public KeelCronExpression cronExpression() {
        return new KeelCronExpression("* * * * *");
    }

    @Override
    public void execute(Calendar calendar) {
        getLogger().info("execute on " + calendar.toString());
    }
}
