package com.schedular;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ArrApplication {

	public static void main(String[] args) {
		SpringApplication.run(ArrApplication.class, args);
	}

}
