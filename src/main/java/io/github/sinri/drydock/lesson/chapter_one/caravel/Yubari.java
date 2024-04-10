package io.github.sinri.drydock.lesson.chapter_one.caravel;

import io.github.sinri.drydock.naval.melee.Caravel;
import io.github.sinri.keel.facade.async.KeelAsyncKit;
import io.github.sinri.keel.mysql.KeelMySQLDataSourceProvider;
import io.github.sinri.keel.mysql.NamedMySQLDataSource;
import io.github.sinri.keel.mysql.exception.KeelSQLResultRowIndexError;
import io.github.sinri.keel.mysql.statement.AnyStatement;
import io.vertx.core.Future;
import io.vertx.core.VertxOptions;
import io.vertx.core.dns.AddressResolverOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;

import javax.annotation.Nonnull;
import java.util.Objects;

import static io.github.sinri.keel.facade.KeelInstance.Keel;

public class Yubari extends Caravel {
    private NamedMySQLDataSource<YubariMySQLConnection> dataSource;

    /**
     * Override here is for windows under internal device monitoring.
     */
    @Override
    public VertxOptions buildVertxOptions() {
        return super.buildVertxOptions()
                .setAddressResolverOptions(new AddressResolverOptions().addServer("223.5.5.5"));
    }

    @Nonnull
    @Override
    protected Future<Void> prepareDataSources() {
        dataSource = KeelMySQLDataSourceProvider.initializeNamedMySQLDataSource(YubariMySQLConnection.dataSourceName, YubariMySQLConnection::new);
        return Future.succeededFuture();
    }

    @Override
    protected Future<Void> launchAsCaravel() {
        return dataSource.withConnection(yubariMySQLConnection -> {
                    return KeelAsyncKit.repeatedlyCall(routineResult -> {
                        return AnyStatement.select(selectStatement -> selectStatement
                                        .columnWithAlias("rand()", "r")
                                )
                                .execute(yubariMySQLConnection)
                                .compose(resultMatrix -> {
                                    try {
                                        JsonObject firstRow = resultMatrix.getFirstRow();
                                        Double r = firstRow.getDouble("r");
                                        getLogger().info("r: " + r);
                                        if (r > 0.8) {
                                            getLogger().notice("Finally!");
                                            routineResult.stop();
                                            return Future.succeededFuture();
                                        } else {
                                            return KeelAsyncKit.sleep(1_000L);
                                        }
                                    } catch (KeelSQLResultRowIndexError e) {
                                        throw new RuntimeException(e);
                                    }
                                });
                    });
                })
                .onSuccess(v -> {
                    getLogger().notice(Keel.config("yubari.hello"));
                    sink();
                });
    }

    /**
     * Fetch config `yubari.hello`.
     *
     * @see <a href="https://hellosalut.stefanbohacek.dev/hello/">Hello Salut</a> The open api to get hello in various place.
     */
    @Override
    protected Future<Void> loadRemoteConfiguration() {
        String lang = Objects.requireNonNullElse(Keel.config("yubari.lang"), "fr");
        return WebClient.create(Keel.getVertx())
                .getAbs("https://hellosalut.stefanbohacek.dev/?lang=" + lang)
                .ssl(true)
                .send()
                .compose(bufferHttpResponse -> {
                    JsonObject jsonObject = bufferHttpResponse.bodyAsJsonObject();
                    String hello = jsonObject.getString("hello");
                    Keel.getConfiguration()
                            .ensureChild("yubari")
                            .ensureChild("hello")
                            .setValue(hello);
                    return Future.succeededFuture();
                });
    }

    public static void main(String[] args) {
        new Yubari().launch();
    }
}
