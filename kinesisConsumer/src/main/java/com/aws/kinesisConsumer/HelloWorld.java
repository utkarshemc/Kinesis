package com.aws.kinesisConsumer;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorld {
	
	@GetMapping("/hello")
	public String HelloWorldExample()
	{
		return "Hi this is a test code";
	}

}
