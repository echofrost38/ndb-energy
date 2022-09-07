package com.ndb.auction.service.payment.wallet;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ndb.auction.dao.oracle.wallet.NyyuWalletTransactionDao;
import com.ndb.auction.models.transactions.wallet.NyyuWalletTransaction;
import com.ndb.auction.utils.ThirdAPIUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NyyuWalletTransactionService {
    private final NyyuWalletTransactionDao nyyuWalletTxnDao;
    private final ThirdAPIUtils apiUtils;

    public int insertNewTransaction(int userId, int orderId, int orderType, double assetAmount, String assetType) {
        var cryptoPrice = apiUtils.getCryptoPriceBySymbol(assetType);
        var m = NyyuWalletTransaction.builder()
            .userId(userId)
            .amount(assetAmount)
            .usdAmount(assetAmount * cryptoPrice)
            .assetType(assetType)
            .orderId(orderId)
            .orderType(orderType)
            .build();
        return nyyuWalletTxnDao.insert(m);
    }

    public List<NyyuWalletTransaction> getNyyuWalletTxnsByUserId(int userId) {
        return nyyuWalletTxnDao.selectByUserId(userId);
    }

    public List<NyyuWalletTransaction> getNyyuWalletTxnsByOrderId(int orderId, int orderType) {
        return nyyuWalletTxnDao.selectByOrderId(orderId, orderType);
    }
}
