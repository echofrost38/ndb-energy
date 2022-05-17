package com.ndb.auction.payload;

import com.ndb.auction.models.user.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecoveryRequest {
    private User user;
    private String coin;
    private String receiverAddr;
    private String txId;
    private Double depositAmount;
}
