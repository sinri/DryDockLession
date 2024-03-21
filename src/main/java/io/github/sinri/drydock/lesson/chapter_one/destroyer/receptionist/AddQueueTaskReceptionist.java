package io.github.sinri.drydock.lesson.chapter_one.destroyer.receptionist;

import io.github.sinri.drydock.lesson.chapter_one.destroyer.QueueManager;
import io.github.sinri.keel.web.http.AbstractRequestBody;
import io.github.sinri.keel.web.http.ApiMeta;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.util.Objects;

@ApiMeta(routePath = "/queue/add-task")
public class AddQueueTaskReceptionist extends KirisimaReceptionist {
    public AddQueueTaskReceptionist(RoutingContext routingContext) {
        super(routingContext);
    }

    @Override
    protected Future<Object> handleForFuture() {
        RequestBody requestBody = new RequestBody(getRoutingContext());
        float v = requestBody.v();
        long taskRef = QueueManager.enqueueManaualTask(v);
        return Future.succeededFuture(new JsonObject()
                .put("task_ref", taskRef));
    }

    private static class RequestBody extends AbstractRequestBody {

        public RequestBody(RoutingContext routingContext) {
            super(routingContext);
        }

        public float v() {
            return Objects.requireNonNull(readFloat("v"));
        }
    }
}
