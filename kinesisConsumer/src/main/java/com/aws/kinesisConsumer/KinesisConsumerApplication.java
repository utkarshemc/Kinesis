package com.aws.kinesisConsumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
public class KinesisConsumerApplication {

	public static void main(String[] args) {
		SpringApplication.run(KinesisConsumerApplication.class, args);
	}

}
