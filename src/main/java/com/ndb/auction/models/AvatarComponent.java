package com.ndb.auction.models;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Component
@Getter
@Setter
@NoArgsConstructor
public class AvatarComponent {

	private int groupId;
	private int compId;
	private Integer tierLevel;
	private Long price;
	private Integer limited;
	private Integer purchased;
	private String base64Image;

	public AvatarComponent(int groupId, Integer tierLevel, Long price, Integer limited) {
		this.groupId = groupId;
		this.tierLevel = tierLevel;
		this.price = price;
		this.limited = limited;
		this.setPurchased(0);
	}

}
