package com.springkafka.app5;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.springkafka.Foo;
import com.springkafka.ConfigProperties;
import com.springkafka.JsonConfiguration;

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

/**
 * @author lilaizhen
 */
@SpringBootApplication
@Import({ JsonConfiguration.class, ConfigProperties.class })
@EnableKafka
public class S1pKafkaApplication {

	public static void main(String[] args) throws Exception {
		ConfigurableApplicationContext context = new SpringApplicationBuilder(S1pKafkaApplication.class)
			.web(WebApplicationType.NONE)
			.run(args);
		TestBean testBean = context.getBean(TestBean.class);
		testBean.send(new Foo("foo", "bar"));
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
		private KafkaTemplate<String, Foo> template;

		public void send(Foo foo) {
			this.template.send(this.configProperties.getFooTopic(), foo);
		}

	}

	public static class Listener {

		private final CountDownLatch latch = new CountDownLatch(1);

		@KafkaListener(topics = "${kafka.fooTopic}")
		public void listen(Foo foo) {
			System.out.println("Received: " + foo);
			this.latch.countDown();
		}

	}

}
