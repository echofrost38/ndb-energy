package com.ndb.auction.models.Shufti.Response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.*;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Document {
    private String document_number;
    private Name name;
    private String dob;
    private int age;
    private String issue_date;
    private String expiry_date;
    private String gender;
    private String[] selected_type;
    private String[] supported_types;
}
