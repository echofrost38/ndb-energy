package com.ndb.auction.models.user;

import com.ndb.auction.models.BaseModel;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Component
@Getter
@Setter
@NoArgsConstructor
public class UserAvatar extends BaseModel {

	private String purchased;
	private String selected;
	private String prefix;
	private String name;

}
