package com.ndb.auction.models.withdraw;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PaypalWithdraw extends BaseWithdraw {
    
    public PaypalWithdraw(
        int userId, 
        String sourceToken,
        double withdrawAmount,
        String senderBatchId,
        String senderItemId,
        String receiver
    ) {
        this.userId = userId;
        this.sourceToken = sourceToken;
        this.withdrawAmount = withdrawAmount;
        this.senderBatchId = senderBatchId;
        this.senderItemId = senderItemId;
        this.receiver = receiver;
        this.status = BaseWithdraw.PENDING;
    }
    
    private String senderBatchId;
    private String senderItemId;
    private String receiver; // paypal email address I think
}
