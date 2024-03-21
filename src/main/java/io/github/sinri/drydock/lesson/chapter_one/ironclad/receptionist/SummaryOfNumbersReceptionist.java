package io.github.sinri.drydock.lesson.chapter_one.ironclad.receptionist;

import io.github.sinri.keel.web.http.AbstractRequestBody;
import io.github.sinri.keel.web.http.ApiMeta;
import io.vertx.core.Future;
import io.vertx.ext.web.RoutingContext;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

@ApiMeta(routePath = "/summary-of-numbers", allowMethods = {"POST"}, requestBodyNeeded = true)
public class SummaryOfNumbersReceptionist extends MurasameReceptionist {
    public SummaryOfNumbersReceptionist(RoutingContext routingContext) {
        super(routingContext);
    }

    @Override
    protected Future<Object> handleForFuture() {
        RequestBody requestBody = new RequestBody(getRoutingContext());
        List<Integer> numbers = requestBody.numbers();
        AtomicLong sum = new AtomicLong(0);

        getIssueRecorder().info("" + sum.get());
        numbers.forEach(x -> {
            getIssueRecorder().info("=" + sum.get() + "+" + x);
            sum.addAndGet(x);
        });
        getIssueRecorder().info("=" + sum.get());
        return Future.succeededFuture(sum.get());
    }

    private static class RequestBody extends AbstractRequestBody {
        public RequestBody(RoutingContext routingContext) {
            super(routingContext);
        }

        @Nonnull
        public List<Integer> numbers() {
            return Objects.requireNonNull(this.readIntegerArray("numbers"));
        }
    }
}