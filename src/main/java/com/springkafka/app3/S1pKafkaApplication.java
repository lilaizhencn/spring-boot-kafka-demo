package com.springkafka.app3;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.springkafka.CommonConfiguration;
import com.springkafka.ConfigProperties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;

@SpringBootApplication
@Import({ CommonConfiguration.class, ConfigProperties.class })
@EnableKafka
public class S1pKafkaApplication {

	public static void main(String[] args) throws Exception {
		ConfigurableApplicationContext context = new SpringApplicationBuilder(S1pKafkaApplication.class)
			.web(WebApplicationType.SERVLET)
			.run(args);
		TestBean testBean = context.getBean(TestBean.class);
		for (int i = 0; i < 10; i++) {
			testBean.send("foo");
		}
		context.getBean(Listener.class).latch.await(60, TimeUnit.SECONDS);
		context.close();
	}

	@Bean
	public TestBean test() {
		return new TestBean();
	}

	@Bean
	public Listener listener() {
		return new Listener();
	}

	public static class TestBean {

		@Autowired
		private ConfigProperties configProperties;

		@Autowired
		private KafkaTemplate<String, String> template;

		public void send(String foo) {
			this.template.send(this.configProperties.getTopic(), foo);
			this.template.flush();
		}

	}

	public static class Listener {

		private final CountDownLatch latch = new CountDownLatch(10);

		@KafkaListener(topics = "${kafka.topic}")
		public void listen(@Payload String foo, @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition) {
			System.out.println("Received: " + foo + " (partition: " + partition + ")");
			this.latch.countDown();
		}

	}

}
