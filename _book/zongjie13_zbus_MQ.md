## zbus的消息
ProducerExample.java
```
Broker broker = new Broker("localhost:15555"); //!!!Share it in Application!!!
		Producer p = new Producer(broker);

		//p.declareTopic("ScondTopic");

        String topic="ScondTopic";
        ConsumeGroup group = new ConsumeGroup();
        group.setGroupName("mygroups01");
        p.declareGroup(topic,group); //通道

		Message msg = new Message();

		msg.setTopic("ScondTopic");
		msg.setTag("ScondTopic");
		msg.setBody("this is all group msg");

			Message res = p.publish(msg);
        	//p.publishAsync(msg,null); //异步发送消息

			System.out.println("producer:"+res);

		broker.close();
        //每次运行会想不同的通道发送消息；
```

两个消费者分别在不同的通道中，可以分别消费消息；
## 消费者1
该消费者消费mygroups01 通道中的消息
```
Broker broker = new Broker("localhost:15555");   
		
		ConsumerConfig config = new ConsumerConfig(broker);
		config.setTopic("ScondTopic");
		config.setConsumeGroup("mygroups01");

		ConsumeGroup group = new ConsumeGroup();
		group.setGroupName("mygroups01");
		//group.setFilter("ScondTopic");
		config.setConsumeGroup(group);

		System.out.println("开始...");
		config.setMessageHandler(new MessageHandler() {
			@Override
			public void handle(Message msg, MqClient client) throws IOException {
				System.out.println("接受到的消息:"+msg.setConsumeGroup("mygroups01"));
				System.out.println("------------");
				System.out.println("xiaox:"+msg.getTopic());
			}
		});
		
		Consumer consumer = new Consumer(config);
		consumer.start();
		System.out.println("结束...");
	
```
## 消费者2
该消费者消费ScondTopic 通道的消息；
```
Broker broker = new Broker("localhost:15555");

		ConsumerConfig config = new ConsumerConfig(broker);
		config.setTopic("ScondTopic");
		config.setConsumeGroup("ScondTopic");

		ConsumeGroup group = new ConsumeGroup();
		group.setGroupName("ScondTopic");
		//group.setFilter("ScondTopic");
		config.setConsumeGroup(group);

		System.out.println("开始...");
		config.setMessageHandler(new MessageHandler() {
			@Override
			public void handle(Message msg, MqClient client) throws IOException {
				System.out.println("接受到的消息:"+msg.setConsumeGroup("mygroups01"));
				System.out.println("------------");
				System.out.println("xiaox:"+msg.getTopic());
			}
		});

		Consumer consumer = new Consumer(config);
		consumer.start();
		System.out.println("结束...");
```

## RPC
```
 public void configPlugin(Plugins plugins) {
	 //增加zbus插件
	  plugins.add(new ZbusRpcServerPlugin());
 }
```

zbus rpc 插件
```
public class ZbusRpcServerPlugin implements IPlugin {

    ServiceBootstrap serviceBootstrap;

    @Override
    public boolean start() {
        Prop prop = PropKit.use("zbus.properties");
        String brokerAddress = prop.get("serverAddress", "");
        String serviceName = prop.get("serviceName", "");
        String module = prop.get("module", "");
        String token = prop.get("token", "");
        if (StringUtil.isEmpty(brokerAddress) || StringUtil.isEmpty(serviceName)) {
            System.out.println("zbus RPC Service 启动失败;配置错误");
            return false;
        }
        try {
            serviceBootstrap = new ServiceBootstrap();
            serviceBootstrap.serviceAddress(brokerAddress).serviceName(serviceName).methodPage(true);
            if (StringUtil.isNotEmpty(module)) {
                serviceBootstrap.addModule(module, MemberController.class);
            }
            if (StringUtil.isNotEmpty(token)) {
                serviceBootstrap.serviceToken(token);
            }

            serviceBootstrap.start();

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean stop() {
        try {
            if (serviceBootstrap != null) {
                serviceBootstrap.close();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
```