package com.ndb.auction.dao.oracle.wallet;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.ndb.auction.dao.oracle.BaseOracleDao;
import com.ndb.auction.dao.oracle.Table;
import com.ndb.auction.models.transactions.wallet.NyyuWalletTransaction;

import lombok.NoArgsConstructor;

@Repository
@NoArgsConstructor
@Table(name = "TBL_NYYU_WALLET_TXN")
public class NyyuWalletTransactionDao extends BaseOracleDao {
    private static NyyuWalletTransaction extract(ResultSet rs) throws SQLException {
        return NyyuWalletTransaction.builder()
            .id(rs.getInt("ID"))
            .userId(rs.getInt("USER_ID"))
            .orderId(rs.getInt("ORDER_ID"))
            .orderType(rs.getInt("ORDER_TYPE"))
            .amount(rs.getDouble("AMOUNT"))
            .usdAmount(rs.getDouble("USD_AMOUNT"))
            .assetType(rs.getString("ASSET_TYPE"))
            .createdAt(rs.getLong("CREATED_AT"))
            .build();
    }

    public int insert(NyyuWalletTransaction m) {
        var sql = "INSERT INTO TBL_NYYU_WALLET_TXN(ID, USER_ID, ORDER_ID, ORDER_TYPE, AMOUNT, USD_AMOUNT, ASSET_TYPE, CREATED_AT)" +
            "VALUES(SEQ_WALLET_TXN.NEXTVAL,?,?,?,?,?,?,SYSDATE)";
        return jdbcTemplate.update(sql, m.getUserId(), m.getOrderId(), m.getOrderType(), m.getAmount(), m.getUsdAmount(), m.getAssetType());
    }

    public List<NyyuWalletTransaction> selectByUserId(int userId) {
        var sql = "SELECT * FROM TBL_NYYU_WALLET_TXN WHERE USER_ID = ?";
        return jdbcTemplate.query(sql, (rs, rownumber) -> extract(rs), userId);
    }

    public List<NyyuWalletTransaction> selectByOrderId(int orderId, int orderType) {
        var sql = "SELECT * FROM TBL_NYYU_WALLET_TXN WHERE ORDER_ID = ? AND ORDER_TYPE = ?";
        return jdbcTemplate.query(sql, (rs, rownumber) -> extract(rs), orderId, orderType);
    }
}
