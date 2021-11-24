package com.ndb.auction.models.sumsub;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DocSet {
	
	private String idDocSetType;
	private List<String> types;

	public String getIdDocSetType() {
		return idDocSetType;
	}

	public void setIdDocSetType(String idDocSetType) {
		this.idDocSetType = idDocSetType;
	}

	public List<String> getTypes() {
		return types;
	}

	public void setTypes(List<String> types) {
		this.types = types;
	}

}
