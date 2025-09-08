package com.example.yozora;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class YozoraApplication {

	public static void main(String[] args) {
		SpringApplication.run(YozoraApplication.class, args);
	}

}
