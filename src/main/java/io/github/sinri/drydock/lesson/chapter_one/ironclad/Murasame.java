package io.github.sinri.drydock.lesson.chapter_one.ironclad;

import io.github.sinri.drydock.lesson.chapter_one.ironclad.receptionist.MurasameReceptionist;
import io.github.sinri.drydock.naval.melee.Ironclad;
import io.github.sinri.keel.helper.KeelHelpersInterface;
import io.github.sinri.keel.logger.issue.center.KeelIssueRecordCenter;
import io.github.sinri.keel.mysql.KeelMySQLDataSourceProvider;
import io.github.sinri.keel.mysql.NamedMySQLDataSource;
import io.github.sinri.keel.web.http.receptionist.KeelWebReceptionistKit;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.Router;

import javax.annotation.Nonnull;

import static io.github.sinri.keel.facade.KeelInstance.Keel;

public class Murasame extends Ironclad {
    private static final Murasame instance = new Murasame();
    private NamedMySQLDataSource<MurasameMySQLConnection> dataSource;

    private Murasame() {
        super();
    }

    public static NamedMySQLDataSource<MurasameMySQLConnection> getDataSource() {
        return instance.dataSource;
    }

    public static KeelIssueRecordCenter getIssueRecordCenterOfMurasame() {
        return instance.getIssueRecordCenter();
    }

    public static void main(String[] args) {
        instance.launch();
    }

    @Override
    protected Future<Void> launchAsIronclad() {
        return Future.succeededFuture();
    }

    @Override
    public void configureHttpServerRoutes(Router router) {
        // native
        router.get("/").handler(routingContext -> {
            JsonArray names = new JsonArray();
            for (String name : routingContext.queryParam("name")) {
                names.add(name);
            }
            String namesJoined = KeelHelpersInterface.KeelHelpers.stringHelper().joinStringArray(names, " and ");
            routingContext.end("Hello " + namesJoined + "!");
        });
        // keel
        Router apiRouter = Router.router(Keel.getVertx());
        router.route("/api/*").subRouter(apiRouter);

        KeelWebReceptionistKit<MurasameReceptionist> receptionistKit = new KeelWebReceptionistKit<>(
                MurasameReceptionist.class,
                apiRouter
        );
        receptionistKit.loadPackage("io.github.sinri.drydock.lesson.ironclad.receptionist");
    }

    @Nonnull
    @Override
    protected Future<Void> prepareDataSources() {
        dataSource = KeelMySQLDataSourceProvider.initializeNamedMySQLDataSource(MurasameMySQLConnection.dataSourceName, MurasameMySQLConnection::new);
        return Future.succeededFuture();
    }

    @Override
    protected Future<Void> loadRemoteConfiguration() {
        return Future.succeededFuture();
    }
}
