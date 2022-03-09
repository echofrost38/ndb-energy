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
        String targetCurrency,
        double withdrawAmount,
        double fee,
        String sourceToken,
        double tokenPrice,
        double tokenAmount,
        String senderBatchId,
        String senderItemId,
        String receiver
    ) {
        this.userId = userId;
        
        this.targetCurrency = targetCurrency;
        this.withdrawAmount = withdrawAmount;
        this.fee = fee;
        
        this.sourceToken = sourceToken;
        this.tokenPrice = tokenPrice;
        this.tokenAmount = tokenAmount;

        this.senderItemId = senderItemId;
        this.senderBatchId = senderBatchId;
        this.receiver = receiver;
        this.status = BaseWithdraw.PENDING;
    }
    
    private String payoutBatchId;
    private String senderBatchId;
    private String senderItemId;
    private String receiver; // paypal email address I think
}
