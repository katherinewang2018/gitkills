### JFianl框架中定时任务的使用
1.pom.xml中
```
<!-- 调度任务 -->
        <dependency>
            <groupId>org.quartz-scheduler</groupId>
            <artifactId>quartz</artifactId>
            <version>2.2.2</version>
        </dependency>
```
2.配置类中 DemoConfig.java

```
@Override
    public void configPlugin(Plugins plugins) {
        //读取xxx.sql文件 该文件定义了项目需要的sql语句 增删改查之类
        //例如：
        //#sql("removeAddress")
        //UPDATE user SET `status` = ? WHERE user_id = ?
        //#end
        
        arp.setBaseSqlTemplatePath(PathKit.getRootClassPath() + "/sqls");
        arp.addSqlTemplate("all.sql");



        //配置quartz插件
        plugins.add(new QuartzPlugin());

    }

```
QuartzPlugin类是implements IPlugin 的类
它加载了任务调度的配置文件quartz.properties和jobs.properties 
加载的定时任务存放在Map中；以及获得cron表达式，最终从jobs.properties中获得
2.1 jobs.properties 配置文件中
```
#定时更新(每一分钟执行一次)
a.job= job执行的任务
a.cron=0 0/1 * * * ?  //这里是jfinal的cron4j表达式，跟原生的表达式稍微有些区别
a.enable=true

xxxx.job=com.job.OneDayJob//任务调度的类
xxxx.cron=0 58 23 ? * *//cron表达式 我这个是每天的23:58执行一次
xxxx.enable=true  //是否开启  true开  false关闭
```
2.2 任务调度的类 实现Job的类
```
public class OneDayJob implements Job {
    //一定要实现 Job这个类
    //这是个抽象方法
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        try {
                   System.out.println("--------任务调度-----");          

        } catch (Exception e) {
            System.out.println("一天任务调度失败");
        }
    }
}

```
2.3 quartz.properties 配置文件
```

# 调度器属性
org.quartz.scheduler.instanceName=myScheduler
org.quartz.scheduler.instanceId=AUTO
org.quartz.scheduler.skipUpdateCheck=true

# 线程池属性
org.quartz.threadPool.class=org.quartz.simpl.SimpleThreadPool
org.quartz.threadPool.threadCount=5
org.quartz.threadPool.threadPriority=5

# 存储方式
org.quartz.jobStore.misfireThreshold=600000
org.quartz.jobStore.class=org.quartz.impl.jdbcjobstore.JobStoreTX
org.quartz.jobStore.driverDelegateClass=org.quartz.impl.jdbcjobstore.StdJDBCDelegate
org.quartz.jobStore.useProperties=false
org.quartz.jobStore.dataSource=my
org.quartz.jobStore.tablePrefix=qrtz_
org.quartz.jobStore.isClustered=true
org.quartz.jobStore.clusterCheckinInterval=20000

# 提供数据库连接
org.quartz.dataSource.my.connectionProvider.class= 数据库连接类

```
数据库连接类实现ConnectionProvider
读取database.properties 配置文件内容 并初始化连接,进行初始化时添加： 
WallFilter wallFilter = new WallFilter();
为了防御SQL注入攻击
