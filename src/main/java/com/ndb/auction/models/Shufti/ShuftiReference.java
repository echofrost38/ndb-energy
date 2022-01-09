package com.ndb.auction.models.Shufti;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShuftiReference {
    private int userId;
    private String reference;
    private String verificationType;
}
