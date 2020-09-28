package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.example.demo.application.testManager.service.TestManager;

@EnableScheduling
@SpringBootApplication
public class HazelcastClientApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(HazelcastClientApplication.class, args);
		
		//TestManager testManager = (TestManager)context.getBean("testManager");
		//testManager.doTest();
	}
}