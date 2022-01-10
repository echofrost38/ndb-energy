package com.ndb.auction.models;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Component
@Getter
@Setter
@NoArgsConstructor
public class AvatarSet extends BaseModel {

	private int groupId;
	private int compId;

	public AvatarSet(int id, int groupId, int compId) {
		this.id = id;
		this.groupId = groupId;
		this.compId = compId;
	}

}
