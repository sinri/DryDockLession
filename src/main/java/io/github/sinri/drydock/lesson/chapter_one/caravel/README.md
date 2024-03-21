# 1.2 初识轻快帆船

# 从桨帆船到轻快帆船

在之前Galley的基础上扩展出了Caravel类，至此，开始面向阿里云生态进行了特化。

其中，`loadLocalConfiguration`方法提供了默认实现，可以从`config.properties`加载本地配置；`buildIssueRecordCenter`
方法默认构建的日志中心从标准输出同步记录日志的实现变更为使用基于阿里云日志服务的异步日志记录器来实现。

此外，定义了`prepareDataSources`方法，用于实现必要的数据源初始化（如MySQL数据库连接池等）；定义了`getMetricRecorder`
方法，可用于时序数据的记录（如阿里云日志服务的时序数据业务）。

在Caravel类中，Galley中定义的`launchAsGalley`方法实现为final方法，其工作流程为:

1. 通过日志中心新建一个以`DryDock`
   为topic的日志记录器，并挂载到基础日志记录器。其目的是保证基础日志记录器收到的日志在标准输出打印之后，也会在日志中心以`DryDock`
   为topic记录一遍。

2. 尝试根据配置文件，建立时序数据记录器（以AliyunSLSMetricRecorder类实现对应阿里云日志服务的时序数据业务）。

3. 尝试加载健康检查检测模块。这个模块默认使用日志记录每分钟的程序运行健康指标。DryDock体系还提供了基于时序数据的实现。

4. 运行`launchAsCaravel`方法，这个方法留待具体业务实现。

# 舰队大家族

现在，我们已经发现了两种实现类，事实上，整个DryDock体系给出了许多层层特化的实现类。

![image](https://alidocs.oss-cn-zhangjiakou.aliyuncs.com/a/deBxKNqNAF0VDOe0/e0fcdee8a6de4599a6c54e69c6ffad430185.png)

不如现在让我们简单深挖一下Caravel所继承的上游类定义。

## CommonUnit接口

DryDock所提供的可运行程序框架都是基于此接口。该接口声明了以下功能要点。

### 框架通用日志记录器

    KeelEventLogger getLogger();

为了记录在程序运作过程中框架产生的问题，需要实现一个日志记录器。

鉴于内容类型和目标单一，为确保日志输出稳定，一般实现为通过标准输出打印日志。

### 日志中心

    KeelIssueRecordCenter getIssueRecordCenter();

在程序运作过程中各种各样的逻辑需要记录日志，因此需要实现一个日志中心，以此为各种场景构建通用日志记录器和特定日志记录器；构建方法已通过default方法体实现。

    default <T extends KeelIssueRecord<?>> KeelIssueRecorder<T> generateIssueRecorder(@Nonnull String topic, @Nonnull Supplier<T> issueRecordBuilder) {
      return this.getIssueRecordCenter().generateIssueRecorder(topic, issueRecordBuilder);
    }
    
    default KeelEventLogger generateEventLogger(@Nonnull String topic) {
      return this.getIssueRecordCenter().generateEventLogger(topic);
    }

### 时序数据记录器

    @Nonnull
    default KeelMetricRecorder getMetricRecorder() {
        throw new NotImplementedException("By default, Metric Recorder is not provided.");
    }

在需要记录时序数据的时候，就需要一个时序数据记录器。

已经在健康检查组件中提供基于此的实现方案。

由于其并非必需之物，简约起见，已经通过default方法体提供了在运行时抛出异常的实现。

## Boat接口

Boat接口继承并扩展了CommonUnit类，增加了以下方法的定义。

### 起航

    void launch();

此方法为基于Boat接口的框架的启动方法。

通常这个方法会在main方法中被调用。

### 船难

    void shipwreck(Throwable var1);

该方法定义以处理框架执行中发生的未被业务逻辑捕获的异常。

通常只能留下Dying Message然后撒手人寰。

### 自沉

    void sink();

该方法用于在框架内完成既定任务后自行了断。

## Warship类

这是一个对Boat接口进行了部分实现的抽象类，作为所有Boat接口实现类的基础内核。

此类实现了基于标准输出的框架通用日志记录器。

在船难时，通过框架通用日志记录器记录异常，然后退出程序，返回码为`1`。

在自沉时，会尝试调用Keel的close方法，然后退出程序，返回码为`0`；期间会通过框架通用日志记录器记录日志。

接下来展开说说起航方法的实现，该方法以final形式实现：

1. 构建基于标准输出的日志中心和框架通用日志记录器

    1. `private KeelIssueRecordCenter issueRecordCenter = KeelIssueRecordCenter.outputCenter();`
       这里的`issueRecordCenter`后面会进行覆写。

    2. `this.unitLogger = this.issueRecordCenter.generateEventLogger("DryDock");`用于getLogger方法。

2. 调用`loadLocalConfiguration`方法加载本地配置；

3. 调用`buildVertxOptions`方法获得Vertx配置；

4. 调用`KeelInstance.Keel.initializeVertx(vertxOptions)`初始化Keel（和Vertx）；自此开始进入异步调用链路；

5. 调用`KeelInstance.Keel.setLogger(this.getLogger());`用框架通用日志记录器覆盖掉Keel提供的通用日志记录器，主要是将Topic和日志级别限制刷掉，本质上都是标准输出。

6. 调用`loadRemoteConfiguration`方法加载远端配置；

7. 正式基于最新的配置构建日志中心并覆盖原有实例；

    1. `this.issueRecordCenter = this.buildIssueRecordCenter();`

8. 调用`launchAsWarship`方法。

前面都是准备配置和日志组件，最后留给开发者一个`launchAsWarship`方法定义来具体进行业务运作。

## Galley类

Galley类是Warship类在Melee系中的最基础实现。由于十分简易，可以自行尝试阅读源代码以理解。

这里需要关注的是建立的launch调用次序，即Boat中launch由Warship最终实现并留下launchAsWarship的方法定义，而Galley则最终实现launchAsWarship并留下launchAsGalley的方法定义，后续的类继承都可以以此类推。

# 夕张号轻快帆船

现在我们通过夕张号轻快帆船来快速建立对轻快帆船的认识。

## 本地配置文件

轻快帆船默认加载了利用阿里云日志服务的日志功能，对于开发者来说，在本地只需要`aliyun.sls.disabled=YES`这样一行配置声明即可将日志打印到标准输出。

现在我们需要了解的一个新的必备物件是MySQL。在Keel 3.x体系下，推荐开发者为每个数据源定义独立的封装类以避免混淆。

首先，准备本地配置文件`config.properties`，在里面放入以下内容。

    # ALIYUN SLS
    aliyun.sls.disabled=YES
    aliyun.sls.project=
    aliyun.sls.logstore=
    aliyun.sls.endpoint=cn-hangzhou-intranet.log.aliyuncs.com
    aliyun.sls.accessKeyId=
    aliyun.sls.accessKeySecret=
    # MYSQL
    mysql.default_data_source_name=yubari
    # MYSQL YUBARI
    mysql.yubari.host=*****
    mysql.yubari.port=3306
    mysql.yubari.username=****
    mysql.yubari.password=****
    mysql.yubari.schema=drydock_lesson
    mysql.yubari.charset=utf8mb4
    mysql.yubari.poolMaxSize=16
    mysql.yubari.poolShared=YES
    mysql.yubari.connectionTimeout=2000
    mysql.yubari.poolConnectionTimeout=2

## 建立指名数据源连接类实现

然后，建立`YubariMySQLConnection`类（继承`io.github.sinri.keel.mysql.NamedMySQLConnection`类），用于相应的数据库连接操作。一般地，将Data
Source Name定义为常量，降低typo的几率。

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

## Yubari类的解析

接下来，准备工作完成了，我们就上一个简单的实现，整个Yubari类的代码如下。

    package io.github.sinri.drydock.lesson.caravel;
    
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
                                .putAll(new JsonObject()
                                        .put("yubari", new JsonObject()
                                                .put("hello", hello)));
                        return Future.succeededFuture();
                    });
        }
    
        public static void main(String[] args) {
            new Yubari().launch();
        }
    }

接下来进行一下解析。

### 自定义VertxOptions

按照Caravel的运行次序，加载完本地配置后，就会去构建VertxOptions实例。这里为了应对某些奇奇怪怪的网络环境，我们重载`buildVertxOptions`
方法，在里面设置一个DNS服务器定义。

### 加载远端配置

接下来，顺带演示一下远端配置加载的玩法。这里我们重载了`loadRemoteConfiguration`方法，在里面实现以下步骤，演示如何基于代码和本地配置动态获取配置。

1. 从本地配置文件读取`yubari.lang`配置，获取一个语言代码，默认为fr。

2. 以此调用某个Open API，获取对应语言的表达式；

3. 将获取到的数据组装为JsonObject格式，加入Keel的配置管理器中。

### 准备数据源

在类`Yubari`中已经定义了属性`dataSource`，这是一个统管Yubari指名数据源连接的指名数据源。

    private NamedMySQLDataSource<YubariMySQLConnection> dataSource;

为了利用MySQL数据库，在`prepareDataSources`方法中初始化数据源，使用工具类`KeelMySQLDataSourceProvider`
的静态方法`initializeNamedMySQLDataSource`即可基于当前Keel配置管理器中的配置内容进行构建。完成初始化之后，返回一个成功的Future即可。

### 业务演示

本次试航的步骤如下：

1. 获取一个指名数据库连接（无需事务）；

    1. 建立一个异步循环；

        1. 基于此指名数据库连接，查询`select rand() as r;`;

        2. 尝试获取第一行结果中的`r`值；

        3. 如果`r`大于0.8，则打印日志`Finally`并退出循环；否则安息1秒再从头循环。

    2. （退出异步循环时将会自动将指名数据库连接还给连接池）

2. 读取配置`yubari.hello`并输出到日志（这个配置是远端的）。

3. 结束程序。

## 输出示例

    C:\Users\aaaa\.jdks\temurin-17.0.9\bin\java.exe "-javaagent:C:\Users\aaaa\AppData\Local\Programs\IntelliJ IDEA Community Edition\lib\idea_rt.jar=54976:C:\Users\aaaa\AppData\Local\Programs\IntelliJ IDEA Community Edition\bin" -Dfile.encoding=UTF-8 -classpath E:\sinri\DryDockLession\target\classes;C:\Users\aaaa\.m2\repository\io\github\sinri\DryDock\1.4.2\DryDock-1.4.2.jar io.github.sinri.drydock.lesson.caravel.Yubari
    Cannot find the file config.properties. Use the embedded one.
    ㏒ 2024-03-18 13:19:42.038 [INFO] DryDock()
     ▪ message: LOCAL CONFIG LOADED (if any)
    ERROR StatusLogger Log4j2 could not find a logging implementation. Please add log4j-core to the classpath. Using SimpleLogger to log to the console...
    ㏒ 2024-03-18 13:19:42.620 [INFO] DryDock()
     ▪ message: KEEL INITIALIZED
    ㏒ 2024-03-18 13:19:44.154 [INFO] DryDock()
     ▪ message: REMOTE CONFIG LOADED (if any)
    ㏒ 2024-03-18 13:19:44.190 [INFO] DryDock()
     ▪ message: Deployed HealthMonitor: a17a2f2e-c3fb-4552-9ae1-85a07f15e31e
    ㏒ 2024-03-18 13:19:44.356 [INFO] DryDock()
     ▪ message: r: 0.46534675030535716
    ㏒ 2024-03-18 13:19:45.388 [INFO] DryDock()
     ▪ message: r: 0.4247478073693326
    ㏒ 2024-03-18 13:19:46.445 [INFO] DryDock()
     ▪ message: r: 0.2710060451841038
    ㏒ 2024-03-18 13:19:47.500 [INFO] DryDock()
     ▪ message: r: 0.9731731163087982
    ㏒ 2024-03-18 13:19:47.500 [NOTICE] DryDock()
     ▪ message: Finally!
    ㏒ 2024-03-18 13:19:47.501 [NOTICE] DryDock()
     ▪ message: Hola
    ㏒ 2024-03-18 13:19:47.501 [FATAL] DryDock()
     ▪ message: SINK
    ㏒ 2024-03-18 13:19:47.523 [FATAL] DryDock()
     ▪ message: Keel Sank.
    
    进程已结束，退出代码为 0