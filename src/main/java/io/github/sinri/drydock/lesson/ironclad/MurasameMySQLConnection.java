package io.github.sinri.drydock.lesson.ironclad;

import io.github.sinri.keel.mysql.NamedMySQLConnection;
import io.vertx.sqlclient.SqlConnection;

import javax.annotation.Nonnull;

public class MurasameMySQLConnection extends NamedMySQLConnection {
    public static final String dataSourceName = "murasame";

    public MurasameMySQLConnection(SqlConnection sqlConnection) {
        super(sqlConnection);
    }

    @Nonnull
    @Override
    public String getDataSourceName() {
        return dataSourceName;
    }
}
