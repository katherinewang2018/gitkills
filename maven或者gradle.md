### maven项目中解决jar包冲突

```
<dependency>
<groupId>org.apache.hadoop</groupId>
<artifactId>hadoop-common</artifactId>
<exclusions>
<exclusion>
  <groupId>javax.servlet</groupId>
  <artifactId>*</artifactId>
</exclusion>
</exclusions>
</dependency>
```

### 1.jfinal项目使用以下方式启动时： 使用maven项目管理时
```
<dependency>
			<groupId>com.jfinal</groupId>
			<artifactId>jfinal-undertow</artifactId>
			<version>1.4</version>
		</dependency>
```
需要注意jar包依赖冲突
原先pom文件中的依赖要去掉
```
<!--<dependency>
			<groupId>com.jfinal</groupId>
			<artifactId>jetty-server</artifactId>
			<version>8.1.8</version>
			&lt;!&ndash;
			此处的 scope 值为 compile 仅为支持 IDEA 下启动项目
			打 war 包时需要改成 provided，以免将一些无用的 jar 打进去
			&ndash;&gt;
			<scope>compile</scope>
		</dependency>-->
```

### 启动
在这个配置类中
```
public class JFinalClubConfig extends JFinalConfig 
```
添加main 方法 用于启动项目
```
public static void main(String[] args) {
		
		/**
		 * 特别注意：IDEA 之下建议的启动方式，仅比 eclipse 之下少了最后一个参数
		 */
		/*JFinal.start("src/main/webapp", 8080, "/");*/ //jfinal自带的jetty-server启动方式


		UndertowServer.start(JFinalClubConfig.class, 80, true);

	}
```


### 2.jfinal项目使用以下方式启动时： 使用gradle项目管理时
#### 依赖
在build.gradle 添加
```
compile group: 'com.jfinal', name: 'jfinal-undertow', version: '1.4'
```
一定要去掉
```
compile group: 'com.jfinal', name: 'jetty-server', version: '2018.11'
```
不然会有jar包冲突问题


#### 启动
在jfinal配置类中 添加
```
public static void main(String[] args) {

    UndertowServer.create(SystemConfig.class)
        .addHotSwapClassPrefix("com.eshanren.").setResourcePath("E:\\testCode\\V3\\web") //存放视图的文件夹(子文件夹有WEB-INF)
        .start();

    }
```

这里使用了默认的80端口
```
Starting Undertow Server http://localhost:80
```
### 其他内容可以参考：
https://www.jfinal.com/doc/1-4

### IDE 环境下的热加载问题 参考
https://www.jfinal.com/doc/1-5
