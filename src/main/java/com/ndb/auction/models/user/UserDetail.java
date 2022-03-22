package com.ndb.auction.models.user;

import com.ndb.auction.models.BaseModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.sql.Date;

@Component
@Getter
@Setter
@NoArgsConstructor
public class UserDetail extends BaseModel {

    private long userId;
    private String firstName;
    private String lastName;
    private Date issueDate;
    private Date expiryDate;
    private String nationality;
    private String countryCode;
    private String documentType;
    private String placeOfBirth;
    private String documentNumber;
    private String personalNumber;
    private String height;
    private String country;
    private String authority;
    private Date dob;
    private int age;
    private String gender;
    private String address;
}
