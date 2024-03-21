# 2.1 关于我非得打日志那件事

从人类开始写应用型程序以来，打日志就是比获取程序计算结果更加优先实现的功能，广义上，为了验证程序能跑起来而输出的“哈啰，挖路的！”就是一种日志——毕竟应用型程序是需要提供正经的业务功能的，光会哈啰又不能恰饭。作为一个入门系列，这一篇将在开始业务功能之前介绍打日志的各种妖术。

# 日志（Issue Record）

Keel的设计者认为：

* 日志由基本要素（时间、等级、类型）和按需要素（记录文本、各种属性等）构成；

* 在不同场景下应该有不同的日志记录规范，即按需要素有多样性也会有稳定性（反映到代码实现和日志观测上，就是不同场景下的日志字段应该是不同的，但同一类型的日志应该具备同样的日志字段）；

* 其中必然存在一套最为通用的按需要素组合；

按照这个思想，在Keel体系下，所有日志记录都需要符合`KeelIssueRecord<T>`接口的定义。

## 日志接口

`KeelIssueRecord<T>`接口定义了基本要素如下：

* timestamp 日志时间。

* level 日志级别，由`KeelLogLevel`枚举类定义，下详。

* topic 日志话题。

* classification 日志类别，设计为一个字符串数组，用以细分。

* exception 日志承载异常时，异常内容放于此处。

并为此外要素提供了`attributes`容器以供存放，其中在默认定义中已经包括如下两种。

* message 日志内容文本。

* context 日志上下文，以对象形式存放信息。

Keel提供了上述接口的抽象的实现（`BaseIssueRecord<T>`类）以供重载扩展；并在此基础上以`KeelEventLog`最终类提供了通用日志的方案。

### 日志等级（KeelLogLevel）

从轻到严重依次为：

* DEBUG

* INFO

* NOTICE

* WARNING

* ERROR

* FATAL

## 特定日志（Certain Issue Record）

基于日志接口和其抽象实现，可自定义一种特定日志类，包括但不限于：

* 通过定义特定的attribute和其写入方式

* 实现初始化方法并对要素进行预写入

具体例子可参见Keel源码中的`ReceptionistIssueRecord`类等具体实现。

同时还需注意，只要实现的类不一样，就应视为不同的特定日志。

## 通用日志（Event Log）

Keel体系中定义了通用日志，以`KeelEventLog`类实现，支持日志接口中定义的所有要素。

通用日志是特定日志的一种，也是其最简单的一种。

# 日志中心

为了实现打日志这件无比重要的事情，在Keel体系内定义了`KeelIssuRecordCenter`
接口，用于确保一个机制来维护一个日志IO运作内核，并基于此提供日志记录器，完成记录接受到的日志。围绕上述目的，在代码开发中用到日志中心的主要作用是创建日志记录器以供后续使用。

日志中心默认实现有沉默处理、同步处理和异步处理三种；其中真正实现日志处理的是日志中心里的IO运作内核（由`KeelIssueRecorderAdapter`
接口定义），这个目前暂不展开。

通过静态方法`KeelIssueRecordCenter.silentCenter()`可以得到一个沉默处理日志的日志中心，仅提供代码语法兼容，不会记录日志到任何地方。

同步处理的日志中心，可以通过静态方法`KeelIssueRecordCenter.outputCenter()`
得到一个基于同步标准输出打印日志的实现。这是开箱即用的日志中心，Keel的默认通用日志记录器也是基于此运作的。

之前已经见识过的DryDock中基于阿里云日志服务的日志记录方式，就是异步处理的典范。

通常，在DryDock体系下，已经默认准备好了日志中心，所以起步阶段不必过多关心日志中心的实现。

# 日志记录器

一个日志中心实例具备`<T extends KeelIssueRecord<?>> KeelIssueRecorder<T> generateIssueRecorder(@Nonnull String topic, @Nonnull Supplier<T> issueRecordBuilder)`方法来生成一个对应特定日志实现类的日志记录器（`KeelIssueRecorder<T extends KeelIssueRecord<?>>`
）。这个方法生成日志记录器时，需要为其提供一个日志话题，以及一个特定日志实现类的生成器。所生成的日志记录器默认针对这一日志话题，并为每次日志记录提供一个特定日志实现类的初始化生成。

对应特定日志实现类的日志记录器提供一系列记录日志的方法和设定。

* `void setVisibleLevel(@Nonnull KeelLogLevel var1);`设置某一级别以上的日志才输出，不到等级的日志就扔了。

* `void addBypassIssueRecorder(@Nonnull KeelIssueRecorder<T> var1);`
  增加一个旁受日志记录器，与当前日志记录器支持同一类特定日志，将会再次处理一次当前日志记录器处理过的日志。一般开发用不到。

* `void setRecordFormatter(@Nullable Handler<T> var1);`设置一个日志格式化工具，作用是将接收到的日志格式化一番之后再输出。

* 以日志级别输出日志的方法（debug、info、notice、warning、error、fatal），可以给定一个Handler基于特定日志模板填充，也可以直接使用字符串作为message快速完成之。

* 针对异常，给出了`exception`方法，其级别默认为ERROR。

可以参考如下代码，运行以观察效果。

首先定义一种特定的日志实现。

    package io.github.sinri.drydock.lesson.chapter_two.logging;
    
    import io.github.sinri.keel.logger.issue.record.BaseIssueRecord;
    
    import javax.annotation.Nonnull;
    
    public class SampleIssueRecord extends BaseIssueRecord<SampleIssueRecord> {
        public static final String topic = "sample";
    
        @Nonnull
        @Override
        public String topic() {
            return topic;
        }
    
        @Nonnull
        @Override
        public SampleIssueRecord getImplementation() {
            return this;
        }
    
        public SampleIssueRecord setLabel(String label) {
            this.attribute("label", label);
            return this;
        }
    }

然后尝试使用各种方式编辑打印日志。

    package io.github.sinri.drydock.lesson.chapter_two.logging;
    
    import io.github.sinri.keel.logger.KeelLogLevel;
    import io.github.sinri.keel.logger.issue.center.KeelIssueRecordCenter;
    import io.github.sinri.keel.logger.issue.recorder.KeelIssueRecorder;
    import org.apache.poi.ss.formula.eval.NotImplementedException;
    
    public class StartToLog {
        public static void main(String[] args) {
            KeelIssueRecorder<SampleIssueRecord> issueRecorder = KeelIssueRecordCenter.outputCenter().generateIssueRecorder(
                    SampleIssueRecord.topic,
                    () -> new SampleIssueRecord()
                            .classification("logging", "start")
            );
    
            issueRecorder.notice("Default Issue Recording with output center!");
    
            issueRecorder.setRecordFormatter(sampleIssueRecord -> {
                sampleIssueRecord.context("format", "labeled");
            });
    
            issueRecorder.info(sampleIssueRecord -> sampleIssueRecord.setLabel("good"));
            issueRecorder.notice(sampleIssueRecord -> sampleIssueRecord.setLabel("common"));
            issueRecorder.warning(sampleIssueRecord -> sampleIssueRecord.setLabel("bad"));
    
            issueRecorder.setVisibleLevel(KeelLogLevel.NOTICE);
    
            issueRecorder.info(sampleIssueRecord -> sampleIssueRecord.setLabel("good, but this line would not be logged"));
            issueRecorder.notice(sampleIssueRecord -> sampleIssueRecord.setLabel("common"));
            issueRecorder.warning(sampleIssueRecord -> sampleIssueRecord.setLabel("bad"));
    
    
            try {
                throw new NotImplementedException("Sample Ended");
            } catch (Throwable e) {
                issueRecorder.exception(new RuntimeException("Let us finish here", e), "But you can try more.");
            }
        }
    }

## 通用日志记录器

通用日志记录器(`KeelEventLogger`类)是围绕通用日志（`KeelEventLog`类）进行日志输出的实现。在Keel体系下，广泛用于各种非特定场景，如

* `Keel.getLogger()`Keel实例的默认日志记录途径

* `KeelSundial`的默认日志记录途径

* DryDock中`CommonUnit.getLogger()`作为各类实现的通用默认日志记录途径

相对于特定日志记录器，通用日志记录器提供了更丰富的日志记录方式。

具体还是对着代码自己体验吧。

    package io.github.sinri.drydock.lesson.chapter_two.logging;
    
    import io.github.sinri.keel.logger.KeelLogLevel;
    import io.github.sinri.keel.logger.event.KeelEventLogger;
    import io.github.sinri.keel.logger.issue.center.KeelIssueRecordCenter;
    import io.vertx.core.json.JsonObject;
    import org.apache.poi.ss.formula.eval.NotImplementedException;
    
    import static io.github.sinri.keel.facade.KeelInstance.Keel;
    
    public class QuickLogEvents {
        public static void main(String[] args) {
            KeelEventLogger eventLogger = Keel.getLogger();
            //Or use this to create one : KeelIssueRecordCenter.outputCenter().generateEventLogger("sample");
    
    
            eventLogger.info("Let start with output center!");
    
            eventLogger.setVisibleLevel(KeelLogLevel.NOTICE);
    
            eventLogger.info("here is an information but you cannot see it.");
            eventLogger.notice("but you can see this notice");
            eventLogger.warning("warning comes", new JsonObject().put("directly", "use json object"));
            eventLogger.error("error with context handler", ctx -> ctx.put("modify", "this"));
            eventLogger.fatal(eventLog -> eventLog
                    .classification("fatal", "unknown reason")
                    .message("long live sinri!")
                    .context("a", 1)
                    .context("b", 2)
            );
    
            try {
                throw new NotImplementedException("Sample Ended");
            } catch (Throwable e) {
                eventLogger.exception(new RuntimeException("Let us finish here", e), "But you can try more.");
            }
        }
    }