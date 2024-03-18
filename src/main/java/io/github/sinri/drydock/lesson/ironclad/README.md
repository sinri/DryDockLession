# 1.3 铁甲舰的黑烟

铁甲舰`Ironclad`
是对轻快帆船的又一次继承，封装了最为常见的Java项目的功能：提供HTTP服务。在DryDock体系中，HTTP服务基于`io.vertx::vertx-web`
库实现，可以自行阅读相关文档以资细节。

本文中，将建立一个`Icronclad`类的实现，即村雨号铁甲舰（`Murasame`类）。

# 村雨号铁甲舰

首先，通过继承，建立一个相对简洁的框架。

    package io.github.sinri.drydock.lesson.ironclad;
    
    import io.github.sinri.drydock.naval.melee.Ironclad;
    import io.github.sinri.keel.helper.KeelHelpersInterface;
    import io.github.sinri.keel.mysql.KeelMySQLDataSourceProvider;
    import io.github.sinri.keel.mysql.NamedMySQLDataSource;
    import io.vertx.core.Future;
    import io.vertx.core.json.JsonArray;
    import io.vertx.ext.web.Router;
    
    import javax.annotation.Nonnull;
    
    public class Murasame extends Ironclad {
        private NamedMySQLDataSource<MurasameMySQLConnection> dataSource;
    
        private Murasame() {
            super();
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
    
        private static final Murasame instance = new Murasame();
    
        public static NamedMySQLDataSource<MurasameMySQLConnection> getDataSource() {
            return instance.dataSource;
        }
    
        public static void main(String[] args) {
            instance.launch();
        }
    }

基于Caravel的部分相信大佬们已经能够看懂，就不再多解释了；而将主类静态实例化并将`dataSource`通过静态方法`getDataSource`
输出的方法也是十分的好懂。

默认HTTP服务会启动并监听8080端口。`configureHttpServerRoutes`中的实现，即设置HTTP服务的路由则需要开发者自行定义。

## HTTP服务路由设置

首先看一个原生的案例，将主路径`/`加以处理，接收参数并返回组装好的文本；然后，快进到使用Keel体系内封装好的玩法。

### 原生模式

设计实现当用户以GET访问`/`时，可以在url的query部分携带`name`参数，程序对所有的参数打招呼。

原生代码如下：

    router.get("/").handler(routingContext -> {
      JsonArray names = new JsonArray();
      for (String name : routingContext.queryParam("name")) {
        names.add(name);
      }
      String namesJoined = KeelHelpersInterface.KeelHelpers.stringHelper().joinStringArray(names, " and ");
      routingContext.end("Hello " + namesJoined + "!");
    });

运行后Murasame可以尝试构造GET请求访问以观测效果。

提供了测试类`io.github.sinri.drydock.lesson.test.murasame.MurasameRootTest`，可以直接在IDEA中运行以观察不同参数情况下的返回效果。

### 接待模式

接待模式适用于按一定规范编排的API设计实现。

为后续需要，在`Murasame`类中新增静态透出方法`getIssueRecordCenterOfMurasame`，供接待类实现使用。

    public static KeelIssueRecordCenter getIssueRecordCenterOfMurasame() {
      return instance.getIssueRecordCenter();
    }

建立集中放置接待类的包`io.github.sinri.drydock.lesson.ironclad.receptionist`
；在包中建立一个抽象的接待类`MurasameReceptionist`。

    package io.github.sinri.drydock.lesson.ironclad.receptionist;
    
    import io.github.sinri.drydock.lesson.ironclad.Murasame;
    import io.github.sinri.keel.logger.issue.center.KeelIssueRecordCenter;
    import io.github.sinri.keel.web.http.receptionist.KeelWebFutureReceptionist;
    import io.vertx.ext.web.RoutingContext;
    
    import javax.annotation.Nonnull;
    
    abstract public class MurasameReceptionist extends KeelWebFutureReceptionist {
    
    
        public MurasameReceptionist(RoutingContext routingContext) {
            super(routingContext);
        }
    
        @Nonnull
        @Override
        protected KeelIssueRecordCenter issueRecordCenter() {
            return Murasame.getIssueRecordCenterOfMurasame();
        }
    }

此类的作用是统一使用Murasame定义的日志中心来记录接待中生成的日志，并作为本项目的接待基准用于接待能力发现。即，在`configureHttpServerRoutes`
方法中，建立一个新的路由器实例，以处理`/api/*`下所有的路由，并为其设置仅识别基于`MurasameReceptionist`类的接待类实现。

    Router apiRouter = Router.router(Keel.getVertx());
    router.route("/api/*").subRouter(apiRouter);
    
    KeelWebReceptionistKit<MurasameReceptionist> receptionistKit = new KeelWebReceptionistKit<>(
      MurasameReceptionist.class,
      apiRouter
    );
    receptionistKit.loadPackage("io.github.sinri.drydock.lesson.ironclad.receptionist");

由于本案例中无需鉴权等要求，因此，直接在路由器中挂载接待类集中的包路径即可。

完成设置后，现在可以在放置接待类的包中建立所需的基于`MurasameReceptionist`类的接待类了。

这里我们设计一个接口，通过POST方法，接受一个JsonObject参数，从其中获取整数数组numbers，并将其中的数字加和后输出。代码如下，后面对要点进行分析。

    package io.github.sinri.drydock.lesson.ironclad.receptionist;
    
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

#### 接待能力声明`@ApiMeta`

接待类必须声明`@ApiMeta`才会被纳入可接待范围，并且必须声明`routePath`
作为所在路由器中的相对路径。案例中的路由器路径前缀为`/api/*`，因此这个接待类针对路径`/api/summary-of-numbers`
。此外还可以设置可支持的方法、是否解析请求体、超时时间等。

#### 请求体封装对象`RequestBody`

一般地，接待类会接受POST请求，其请求体通常是一个JsonObject。建立一个私有的静态`RequestBody`
类可以方便地从接待路由上下文（RoutingContext）中抽取出这个请求体，并标准化内容读取的途径。这个案例中，`numbers`
方法就将一个整数数组从请求体中抽取出来，当格式不符时，可以提前发现参数问题并抛出异常。

#### 日志记录

在`MurasameReceptionist`类中定义的日志中心使得每个基于其实现的接待类可以直接使用`getIssueRecorder`方法进行日志记录。

#### 接口输出

Keel体系的接待类的标准JSON格式输出为

    {"code":"OK","data":dynamic}

以及

    {"code":"FAILED","data":dynamic}

两种形式。在`handleForFuture`方法中成功返回的值将作为成功返回报文中的data部分，而其中抛出的异常则将组装为失败返回报文中的data部分。