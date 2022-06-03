package com.ndb.auction.models.auth;

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
public class GAuthResetRequest {
    // primary keys
    private int userId;
    private String token;

    private String secret;
    private long requestedAt;
    private long updatedAt;
    private int status;
}
