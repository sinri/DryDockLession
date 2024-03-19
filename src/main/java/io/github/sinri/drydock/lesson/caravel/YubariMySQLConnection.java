package io.github.sinri.drydock.lesson.caravel;

import io.github.sinri.keel.mysql.NamedMySQLConnection;
import io.vertx.sqlclient.SqlConnection;

import javax.annotation.Nonnull;

public class YubariMySQLConnection extends NamedMySQLConnection {
    public static final String dataSourceName = "yubari";

    public YubariMySQLConnection(SqlConnection sqlConnection) {
        super(sqlConnection);
    }

    @Nonnull
    @Override
    public String getDataSourceName() {
        return dataSourceName;
    }
}
