# spring-boot-kafka-demo
基于spring boot的spring kafka组件的使用demo，包含几个demo讲解。
#这个demo需要提前创建两个topic,或者自己创建两个,然后在配置文件中修改成自己的就可以了
===
afka-topics --zookeeper localhost:2181 --create --topic s1p.topic --partitions 5 --replication-factor 1

kafka-topics --zookeeper localhost:2181 --create --topic s1p.fooTopic --partitions 5 --replication-factor 1

#总共有9个例子
    1.第一个例子介绍的是简单的向topic(s1p.topic)发送简单数据
    2.第二个例子介绍的是通过注解 @KafkaListener 接受来自topic (s1p.topic)的消息
    3.第三个例子介绍的是通过@KafkaListener 指定使用那个partition处理某个topic的数据
    4.第四个例子介绍的是指定偏移量(offset)的注解接受数据实例
    5.第五个例子介绍的是将value数据 json序列化和反序列化操作,在这个例子中将CommonConfiguration替换为了JsonConfiguration
    (类Foo)硬编码到反序列化器——这是必需的,因为JSON不包含任何类型的信息
    6.第六个例子介绍的是使用更灵活地StringJsonMessageConverter 代替json的序列化和反序列化,使用这个的好处是
    可做类型转换。只要filed是相对应的就可以转化为一个对象。
    7.第七个例子是对第二个例子的拓展,在监听那儿添加了重试和过滤
    8.第八个例子介绍的是使用更低级的KafkaMessageListenerContainer 替代 @KafkaListener
    9.第9个例子是手动通过代码Acknowledgment提交offset的例子 