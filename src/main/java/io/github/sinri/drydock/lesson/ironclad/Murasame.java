package io.github.sinri.drydock.lesson.ironclad;

import io.github.sinri.drydock.naval.melee.Ironclad;
import io.github.sinri.keel.mysql.KeelMySQLDataSourceProvider;
import io.github.sinri.keel.mysql.NamedMySQLDataSource;
import io.vertx.core.Future;
import io.vertx.ext.web.Router;

import javax.annotation.Nonnull;

public class Murasame extends Ironclad {
    private static final Murasame instance = new Murasame();
    private NamedMySQLDataSource<MurasameMySQLConnection> dataSource;

    private Murasame() {
        super();
    }

    public static NamedMySQLDataSource<MurasameMySQLConnection> getDataSource() {
        return instance.dataSource;
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
        // todo
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
