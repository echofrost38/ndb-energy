package com.ndb.auction.models.user;

import java.util.List;
import java.util.Map;

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

	private Map<String, List<String>> purchased;
	private String prefix;
	private String title;

}
