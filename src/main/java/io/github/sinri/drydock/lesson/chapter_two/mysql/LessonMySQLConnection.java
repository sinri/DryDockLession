package io.github.sinri.drydock.lesson.chapter_two.mysql;

import io.github.sinri.keel.mysql.NamedMySQLConnection;
import io.vertx.sqlclient.SqlConnection;

import javax.annotation.Nonnull;

public class LessonMySQLConnection extends NamedMySQLConnection {
    public static final String dataSourceName = "drydock_lesson";

    public LessonMySQLConnection(SqlConnection sqlConnection) {
        super(sqlConnection);
    }

    @Nonnull
    @Override
    public String getDataSourceName() {
        return dataSourceName;
    }
}
