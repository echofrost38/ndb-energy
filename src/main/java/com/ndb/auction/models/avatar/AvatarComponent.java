package com.ndb.auction.models.avatar;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Component
@Getter
@Setter
@NoArgsConstructor
public class AvatarComponent {

	private String groupId;
	private int compId;
	private Integer tierLevel;
	private Double price;
	private Integer limited;
	private Integer purchased;
	private String svg;
	private Integer width;
	private Integer top;
	private Integer left;

	public AvatarComponent(String groupId, Integer tierLevel, Double price, Integer limited, String svg, Integer width, Integer top, Integer left) {
		this.groupId = groupId;
		this.tierLevel = tierLevel;
		this.price = price;
		this.limited = limited;
		this.setPurchased(0);
		this.svg = svg;
		this.width = width;
		this.top = top;
		this.left = left;
	}

	public void increasePurchase() {
		this.purchased++;
	}

}
