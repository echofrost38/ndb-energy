package com.ndb.auction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
public class AuctionApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuctionApplication.class, args);
	}

}
