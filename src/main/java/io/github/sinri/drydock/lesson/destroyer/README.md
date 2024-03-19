# 1.4 驱逐舰一统天下

到目前为止，我们已经展示过一个HTTP服务程序应该如何快速构建。铁甲舰作为纯净的HTTP服务支撑架构，可以快速在SAE架构上部署，并能支持横向扩展，实在是太美妙了。

那么，当一个项目因为体量和实际需求情况等原因，其HTTP服务不需要有横向弹性的功能，同时又需要队列和定时任务等异步玩意的时候，我们可以搞一个驱逐舰（Destroyer）然后把它部署在单个ECS上。

驱逐舰（Destroyer）继承了铁甲舰（Ironclad），因此HTTP服务部分的实现也是一致的；在其上新增的部分即队列和定时任务，这两块功能分别通过实现QueueMixin和SundialMixin这两个接口来纳入。

因此，我们可以先分别观测一下这俩。

# 队列服务组件

队列服务（QueueMixin接口实现）首先提供了一个入口方法`KeelQueue buildQueue()`
，其已经具备默认实现，通常用到的参数均可在余下的接口方法定义中完成改写。如果不需要队列服务，重载这个方法并返回null即可，方便开摆。

如果需要限制同时运行的任务数，可重载`int configuredQueueWorkerPoolSize()`方法。默认返回0，不限制。

必须实现`KeelQueue.SignalReader buildSignalReader();`
方法以提供一个队列运行控制信号的运作机制。如果这个队列不需要手动控制启停，随程序启动和停止即可，则可以给出一个永远发出RUN信号的实现，例如

    @Override
      public KeelQueue.SignalReader buildSignalReader() {
        return () -> Future.succeededFuture(KeelQueue.QueueSignal.RUN);
      }

必须实现`KeelQueueNextTaskSeeker buildQueueNextTaskSeeker();`方法以提供一个队列任务搜索器（`KeelQueueNextTaskSeeker`
类）。此类定义了一个异步的任务准备方法（`Future<KeelQueueTask> get();`）和空闲等待时间参数方法（`long waitingMs()`
，默认10秒）。该类的作用为寻找需要执行的任务（如果没有则异步返回空）并按需将其独占（防止重复运行），然后异步返回之。

抽象任务类`KeelQueueTask`中定义了此类任务的唯一引用标识（reference，例如常见的task id）和类型标识（category），并作为一个Verticle实现自行运作。

# 定时任务服务组件

定时任务服务（SundialMixin接口实现），首先提供了一个入口方法`KeelSundial buildSundial()`
，其已经具备默认实现。如果不需要定时任务，同样重载了返回null就完事了。

定时任务的配置是个动态的过程，因此定义了一个方法`Future<Collection<KeelSundialPlan>> fetchSundialPlans();`
用于进行一次异步全量定时任务计划的获取。每个计划任务均由`KeelSundialPlan`类定义其Cron表达式和运行方式。

定时任务默认实现为每一分钟启动一次，在当前计划清单中找出该当分钟应该运行的计划加以执行；然后进行一次异步全量定时任务计划的获取以更新当前计划清单。

# 雾岛号驱逐舰

我们快速给出一个例子，即雾岛（Kirisima）号驱逐舰，其实现以下要点，并为简单起见，不使用MySQL数据库服务。

* 定时任务：每分钟运行一次`ByMinuteSundialPlan`，其检查队列中尚未运行的任务数量并输出。

* 队列任务：维护一个任务队列，限制任务并行度为2；定义任务`KirisimaManualTask`，接受一个正实数以供模拟耗时操作时长。

* HTTP服务：监听8080端口，提供以下API

    * POST /queue/add-task，请求体为JsonObject，里面包含了参数v，为一个正实数，以此新建任务并加入队列，然后返回任务引用。

代码参见相应代码库，不再一一分析。