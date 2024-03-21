# 1.5 护卫舰的一席之地

在驱逐舰篇已经提到过，铁甲舰可以支持横向扩展，这个代价就是不能在其中容设队列和定时任务服务。因此护卫舰可以和铁甲舰配合使用，通常配置为一台ECS运行护卫舰，SAE上若干实例弹性运行铁甲舰，各方数据共享。

# 占守号护卫舰

护卫舰不提供HTTP服务，且不能横向扩展。这里给出一个护卫舰的样例，由于队列和定时任务服务的相关组件应用在驱逐舰中已经讲过，因此这里就仅展示一下代码框架就拉倒。

代码参见 [Simusyu.java](src/main/java/io/github/sinri/drydock/lesson/frigate/Simusyu.java)