package com.ndb.auction.models.user;

import com.ndb.auction.models.BaseModel;
import lombok.*;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDetail extends BaseModel {

    private long userId;
    private String firstName;
    private String lastName;
    private String issueDate;
    private String expiryDate;
    private String nationality;
    private String countryCode;
    private String documentType;
    private String placeOfBirth;
    private String documentNumber;
    private String personalNumber;
    private String height;
    private String country;
    private String authority;
    private String dob;
    private int age;
    private String gender;
    private String address;
}
