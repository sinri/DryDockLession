package io.github.sinri.drydock.lesson.test.chapter_two.mysql;

import io.github.sinri.drydock.lesson.chapter_two.mysql.LessonMySQLConnection;
import io.github.sinri.drydock.naval.raider.ClassFileGeneratorForMySQLTables;
import io.github.sinri.keel.tesuto.TestUnit;
import io.vertx.core.Future;
import io.vertx.core.VertxOptions;
import io.vertx.core.dns.AddressResolverOptions;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MySQLSourceCodeFileGen extends ClassFileGeneratorForMySQLTables {

    @Nonnull
    @Override
    protected VertxOptions buildVertxOptions() {
        return super.buildVertxOptions()
                .setAddressResolverOptions(new AddressResolverOptions()
                        .addServer("223.5.5.5"));
    }

    @Override
    protected String getTablePackage() {
        return "io.github.sinri.drydock.lesson.chapter_two.mysql.tables";
    }

    @Nullable
    @Override
    public String getStrictEnumPackage() {
        // 用于解析列注释中的Enum<EnumClassName>枚举类的包配置，本例不使用
        return null;
    }

    @Nullable
    @Override
    public String getEnvelopePackage() {
        // 用于解析列注释中关于解密类的包配置，本例不使用
        return null;
    }


    @Nonnull
    @Override
    protected Future<Void> prepareEnvironment() {
        return Future.succeededFuture();
    }

    @TestUnit
    public Future<Void> generateClassFiles() {
        return this.rebuildTablesInSchema(LessonMySQLConnection.dataSourceName, LessonMySQLConnection::new, "drydock_lesson");
    }
}
