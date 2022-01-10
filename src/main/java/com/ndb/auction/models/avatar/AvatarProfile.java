package com.ndb.auction.models.avatar;

import java.util.List;

import com.ndb.auction.models.BaseModel;
import com.ndb.auction.models.SkillSet;

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
	private String hairColor;
	private List<Facts> factsSet;
	private String details;

	public AvatarProfile(
			String fname,
			String surname,
			String shortName,
			List<SkillSet> skillSet,
			List<AvatarSet> avatarSet,
			String hairColor,
			List<Facts> factsSet,
			String details) {
		this.fname = fname;
		this.surname = surname;
		this.shortName = shortName;
		this.skillSet = skillSet;
		this.avatarSet = avatarSet;
		this.hairColor = hairColor;
		this.factsSet = factsSet;
		this.details = details;
	}

}
