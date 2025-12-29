package com.creditcard.creditcard_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class CreditcardServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CreditcardServiceApplication.class, args);
	}

}
