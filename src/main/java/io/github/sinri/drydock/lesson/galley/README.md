# 桨帆船驾驶之术

-----


# 常识回顾

Java程序的运行的基本为：启动、获取参数、准备组件、提供服务（或完成特定工作）、结束。

## 启动

一般来说，我们可以直接运用 `java -jar JAR.jar`来启动一个Java程序。后面可以加一些启动参数。

## 获取参数

Java获取参数的途径有

*   命令行启动参数

*   系统环境变量

*   本地文件读入（i.e. config.properties）

*   远端文件读入（i.e. Nacos, Kumori Baiyatan）


## 准备组件

比如打日志的东西、连数据库的东西之类的基础物件，健康检查之类的监测器，HTTP WEB服务、队列服务、定时任务服务等。

## 提供服务

让各个组件跑起来以运行业务代码完成需求。

## 结束

驻留服务一般死于kill信号。

也有任务型的程序会自己结束。

# 香取号桨帆船

桨帆船`Galley`是最基础的一个驻留服务的基础。

首先，我们建立一个基本的桨帆船类作为香取号。

为了方便起见，这里预先加上了所需的所有import。

    package io.github.sinri.drydock.lesson.galley;
    
    import io.github.sinri.drydock.naval.melee.Galley;
    import io.github.sinri.keel.facade.async.KeelAsyncKit;
    import io.vertx.core.Future;
    
    import java.util.Objects;
    
    import static io.github.sinri.keel.facade.KeelInstance.Keel;
    
    public class Katori extends Galley {
        @Override
        protected Future<Void> launchAsGalley() {
            return null;
        }
    
        @Override
        protected void loadLocalConfiguration() {
    
        }
    
        @Override
        protected Future<Void> loadRemoteConfiguration() {
            return null;
        }
    }

作为一个简单的案例，其采用默认的Vert.x配置，使用标准输出日志记录器，加载本地日志文件`config.properties`，无需加载远端配置。启动后，读取配置中的`katori.counting_down`的数值进行读秒，读完后程序结束。

## 加载本地配置

将配置交给Keel的configuration管理器进行汇总。

放置在`resources`目录下采用Properties文件格式的`config.properties`文件目前是标准的本地配置手段。

    @Override
    protected void loadLocalConfiguration() {
      // load local config
      Keel.getConfiguration().loadPropertiesFile("config.properties");
    }

## 加载远端配置（空转）

本案例中，我们不需要加载远端配置。

目前加载远端配置仅在SAE实践中应用了Kumori Baiyatan体系。

    @Override
    protected Future<Void> loadRemoteConfiguration() {
      // do not load from remote
      return Future.succeededFuture();
    }

## 准备组件

桨帆船默认使用了实时写入标准输出的事件日志器实现，因此不必改动代码即可直接调用`this.getLogger()`获取`io.github.sinri.keel.logger.event.KeelEventLogger`实例。

## 业务逻辑

由于业务逻辑是读秒倒计时，我们采用简单的拟合法，即读取配置中的数值后，开启一个多次异步任务，每次执行后等待1秒。

### 从配置中读取

Keel的配置管理器提供了readAs系列方法，可以直接获取配置中的文本值并转化为相应类型。

注意，配置文件是可变的，因此极有可能未配置或配置的内容不符合预期，因此需要针对直接读到的值进行判空。

    // read an integer from config
    int countingDown;
    // Method 1:
    countingDown = Objects.requireNonNull(Keel.getConfiguration().readAsInteger("katori", "counting_down"));

如果不使用Keel的配置管理器，可以使用其config方法通过配置中的一行的键来快速获取其对应的文本值，再自行进行转化。该方式对获取文本参数会更方便。

    // Method 2:
    String s = Keel.config("katori.counting_down");
    Objects.requireNonNull(s);
    countingDown = Integer.parseInt(s);

### 通过异步循环和异步睡眠来进行倒计时

Keel在KeelAsyncKit中封装了一系列的异步运行控制工具方法。这里用到了KeelAsyncKit和sleep。

    KeelAsyncKit.stepwiseCall(countingDown, i -> {
      getLogger().info("counting down as " + (countingDown - i));
      // sleep 1 second
      return KeelAsyncKit.sleep(1_000L);
    })

类似常规代码的for循环，stepwiseCall可以指定一个数字和变更步长（默认+1），然后给定一个异步循环体；循环体异步执行后需要返回一个`Future<Void>`实例；循环次数满，返回`Future<Void>`实例。

Thread类的sleep方法会使当前线程陷入睡眠，这在event loop下是不妥的，属于得不到就毁掉的恶行，对应地，应该使用KeelAsyncKit的sleep方法，其本质是让出event loop的占用，待等候时间满足之后再被唤回返回一个`Future<Void>`实例，然后可以从此开始执行下游代码。

这里还可以注意到日志记录的简单运用。在Galley类内提供了`getLogger`方法，可以获取相应日志记录器的实例进行日志记录。

## 结束

提供驻留服务的程序一般不需要自行了断，但这个需求有所要求。

随着读秒的完毕，程序需要结束。

    .onFailure(throwable -> {
      // terminate with 1
      this.shipwreck(throwable);
    })
    .onSuccess(v -> {
      getLogger().info("Boom!");
      // terminate with 0
      this.sink();
    });

在Galley内调用sink方法即可关闭Keel内的Vertx实例结束事件循环并结束程序，对操作系统返回0。

如果遇到极端异常，可以使用shipwreck方法申报异常以留下遗言，并会当场结束程序，对操作系统返回1。

## 运行输出示例

    C:\Users\ljni\.jdks\temurin-17.0.9\bin\java.exe "-javaagent:C:\Users\ljni\AppData\Local\Programs\IntelliJ IDEA Community Edition\lib\idea_rt.jar=51290:C:\Users\ljni\AppData\Local\Programs\IntelliJ IDEA Community Edition\bin" -Dfile.encoding=UTF-8 -classpath E:\sinri\DryDockLession\target\classes;C:\Users\ljni\.m2\repository\io\github\sinri\DryDock\1.4.2\DryDock-1.4.2.jar io.github.sinri.drydock.lesson.galley.Katori
    Cannot find the file config.properties. Use the embedded one.
    ㏒ 2024-03-15 13:57:07.259 [INFO] DryDock()
     ▪ message: LOCAL CONFIG LOADED (if any)
    ERROR StatusLogger Log4j2 could not find a logging implementation. Please add log4j-core to the classpath. Using SimpleLogger to log to the console...
    ㏒ 2024-03-15 13:57:07.625 [INFO] DryDock()
     ▪ message: KEEL INITIALIZED
    ㏒ 2024-03-15 13:57:07.626 [INFO] DryDock()
     ▪ message: REMOTE CONFIG LOADED (if any)
    ㏒ 2024-03-15 13:57:07.633 [INFO] DryDock()
     ▪ message: counting down as 5
    ㏒ 2024-03-15 13:57:08.648 [INFO] DryDock()
     ▪ message: counting down as 4
    ㏒ 2024-03-15 13:57:09.662 [INFO] DryDock()
     ▪ message: counting down as 3
    ㏒ 2024-03-15 13:57:10.688 [INFO] DryDock()
     ▪ message: counting down as 2
    ㏒ 2024-03-15 13:57:11.708 [INFO] DryDock()
     ▪ message: counting down as 1
    ㏒ 2024-03-15 13:57:12.732 [INFO] DryDock()
     ▪ message: Boom!
    ㏒ 2024-03-15 13:57:12.733 [FATAL] DryDock()
     ▪ message: SINK
    ㏒ 2024-03-15 13:57:12.752 [FATAL] DryDock()
     ▪ message: Keel Sank.
    
    进程已结束，退出代码为 0