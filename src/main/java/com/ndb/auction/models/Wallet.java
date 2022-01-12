package com.ndb.auction.models;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Component
@Getter
@Setter
@NoArgsConstructor
public class Wallet {

	private String key;
	private Long total;
	private Long free;
	private Long holding;

	public Wallet(String key, Long free, Long hold) {
		this.key = key;
		this.free = free;
		this.holding = hold;
		this.total = free + hold;
	}

}
