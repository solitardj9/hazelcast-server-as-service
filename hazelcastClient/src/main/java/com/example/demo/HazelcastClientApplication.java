package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class HazelcastClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(HazelcastClientApplication.class, args);
	}

}
