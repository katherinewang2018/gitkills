## 关于zbus需要注意的地方
1. zbus版本很多，每个版本变化很大，高版本启动zbus.bat时，浏览器输入localhost:http://localhost:15555/ 回车后进入 这个页面：（图片不能直接粘贴，直接使用源码）

```
<html>
<head>
<title>Welcome to zbus!</title>
<style>
    body {
        width: 64em;
        margin: 0 auto;
        font-family: 20pt Times New Roman;
    }
</style>
</head>
<body>
<h1>Welcome to zbus!</h1>
<p>If you see this page, the zbus server is successfully started and working for MQ and RPC</p>  
<p>Config location: conf/zbus.xml</p> 
<p><em>Thank you for using <a href="http://zbus.io" target="_blank" >zbus</a>.</em></p>
</body>
</html>

```
无法进入监控消费者和生产者页面：
```
<html>
<head>
<title>Welcome to zbus!</title>
<style>
    body {
        width: 64em;
        margin: 0 auto;
        font-family: 20pt Times New Roman;
    }
</style>
</head>
<body>
<h1>Welcome to zbus!</h1>
<p>If you see this page, the zbus server is successfully started and working for MQ and RPC</p>  
<p>Config location: conf/zbus.xml</p> 
<p><em>Thank you for using <a href="http://zbus.io" target="_blank" >zbus</a>.</em></p>
</body>
</html>

```
另外http://zbus.io 网页无法进入。

总之在版本上挣扎了很久。。。。。。。

## 正题
1.maven坐标
```
<dependency>
            <groupId>org.zbus</groupId>
            <artifactId>zbus</artifactId>
            <version>0.11.3</version>
        </dependency>-->
```
如果无法加载，就只能使用jar包，倒jar包到lib文件夹下，

这里使用低版本，因为启动zbus.bat后，http://localhost:15555/可以直接进入监控页面

2.源码下载
网站地址
https://gitee.com/rushmore/zbus/tree/legacy-0.11.5/

注意这里不下载master中的源码，而是下载legacy-0.11.5分支中的源码

3.为了快速入门，我们打开test目录下的examples目录下给的案例，
改目录下的mq目录下有producer和Consumer文件夹，里面有简单示例：
ProducerExample.java  (生产者)
```
public class ProducerExample { 
	public static void main(String[] args) throws Exception { 
		Broker broker = new Broker("zbus.io:15555"); //!!!Share it in Application!!! 这里在自己电脑运行时,改为localhost:15555
		  
		Producer p = new Producer(broker);
		p.declareTopic("MyTopic"); 
		 
		Message msg = new Message();
		msg.setTopic("MyTopic");
		//msg.setTag("oo.account.pp");  //set tag for Consumer's filter
		msg.setBody("hello " + System.currentTimeMillis()); 
		
		Message res = p.publish(msg);
		System.out.println(res);   
		 
		broker.close();
	} 
}
```
ConsumerExample.java （消费者）
```
public class ConsumerExample {

	@SuppressWarnings("resource")
	public static void main(String[] args) throws Exception {   
		Broker broker = new Broker("localhost:15555");   
		
		ConsumerConfig config = new ConsumerConfig(broker);
		config.setTopic("MyTopic");   
		config.setMessageHandler(new MessageHandler() { 
			@Override
			public void handle(Message msg, MqClient client) throws IOException {
				System.out.println(msg);     
			}
		});
		
		Consumer consumer = new Consumer(config);
		consumer.start(); 
	} 
}
```

### 消息队列组件包括:

#### Topic， 全局唯一标识消息队列，消息需指定

生产者中需要指定Topic:
```
Message msg = new Message();

		msg.setTopic("first Topic");
		msg.setTopic("MyTopic");
```

消费者中也需要设置:
```
ConsumerConfig config = new ConsumerConfig(broker);
		
		config.setTopic("MyTopic");
```


### ConsumeGroup，仅消费者使用到的分组通道，用于实现多种通信模型

在消费者中:
```
Broker broker = new Broker("localhost:15555");   
		
		ConsumerConfig config = new ConsumerConfig(broker);
		config.setConsumeGroup("MyTopic");
		config.setTopic("MyTopic");
```

### Producer, 生产消息方

### Consumer, 消费消息方，需指定消费分组通道，带默认值。

### Broker，消息存储服务器（注意：API层面的Broker指的是服务器接入抽象）


1.实验
consumer.java
```
config.setTopic("MyTopic");
```
A.启动这个两个consumer,等生产者生产一个消息时,其中之一会进行消费,另一个则不会消费;
B.启动这个两个consumer,等生产者生产十个消息时，两个消费者都会消费5个消息；

2. ConsumeGroup，仅消费者使用到的分组通道，用于实现多种通信模型
consumer01.java
```
public static void main(String[] args) throws Exception {   
		Broker broker = new Broker("localhost:15555");   
		
		ConsumerConfig config = new ConsumerConfig(broker);
		config.setConsumeGroup("MyTopic");
		config.setTopic("MyTopic");
		config.setMessageHandler(new MessageHandler() { 
			@Override
			public void handle(Message msg, MqClient client) throws IOException {
				System.out.println("接受到的消息:"+msg);
				System.out.println("------------");
				System.out.println("xiaox:"+msg.getTopic());
			}
		});
		
		Consumer consumer = new Consumer(config);
		consumer.start(); 
	} 
```

consumer02.java
```
public static void main(String[] args) throws Exception {   
		Broker broker = new Broker("localhost:15555");   
		
		ConsumerConfig config = new ConsumerConfig(broker);
		config.setConsumeGroup("groupmsg");
		config.setTopic("MyTopic");
		config.setMessageHandler(new MessageHandler() { 
			@Override
			public void handle(Message msg, MqClient client) throws IOException {
				System.out.println("接受到的消息:"+msg);
				System.out.println("------------");
				System.out.println("xiaobai:"+msg.getTopic());
			}
		});
		
		Consumer consumer = new Consumer(config);
		consumer.start(); 
	} 
```

producer.java
```
public static void main(String[] args) throws Exception { 
		Broker broker = new Broker("localhost:15555"); //!!!Share it in Application!!!
		  
		Producer p = new Producer(broker);

		p.declareTopic("MyTopic");
		Message msg = new Message();

		msg.setTopic("MyTopic");

		msg.setBody("this is group msg");

			Message res = p.publish(msg);
			System.out.println("producer:"+res);

		broker.close();
	} 
```
这样生产者生产一个消息,两个消费者都可以消费到.  (类似订阅模式,而且消费者总是可以收到消息)

