package com.ndb.auction.models.avatar;

import com.ndb.auction.models.BaseModel;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Component
@Getter
@Setter
@NoArgsConstructor
public class AvatarSet extends BaseModel {

	private String groupId;
	private int compId;

	public AvatarSet(int id, String groupId, int compId) {
		this.id = id;
		this.groupId = groupId;
		this.compId = compId;
	}

	public boolean equals(AvatarSet a) {
		if(a.groupId == this.groupId && a.compId == this.compId) {
			return true;
		} else {
			return false;
		}
	}

}
