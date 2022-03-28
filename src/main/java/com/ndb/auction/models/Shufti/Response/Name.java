package com.ndb.auction.models.Shufti.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Name {

    private String first_name;
    private String middle_name;
    private String last_name;

    public String getFullName() {
        return this.first_name + " " + this.last_name;
    }
}
