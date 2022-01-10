package com.ndb.auction.models;

import java.util.List;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Component
@Getter
@Setter
@NoArgsConstructor
public class AvatarProfile extends BaseModel {

	private String fname;
	private String surname;
	private String shortName;
	private List<SkillSet> skillSet;
	private List<AvatarSet> avatarSet;
	private String enemy;
	private String invention;
	private String bio;

	public AvatarProfile(
			String fname,
			String surname,
			String shortName,
			List<SkillSet> skillSet,
			List<AvatarSet> avatarSet,
			String enemy,
			String invention,
			String bio) {
		this.fname = fname;
		this.surname = surname;
		this.shortName = shortName;
		this.skillSet = skillSet;
		this.avatarSet = avatarSet;
		this.enemy = enemy;
		this.invention = invention;
		this.bio = bio;
	}

}
