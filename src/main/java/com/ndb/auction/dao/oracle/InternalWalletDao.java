package com.ndb.auction.dao.oracle;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.ndb.auction.models.InternalWallet;

@Repository
public class InternalWalletDao extends BaseOracleDao {
    
    private static final String TABLE_NAME = "NDB.TBL_INTERNAL_WALLET";

    private static InternalWallet extract(ResultSet rs) throws SQLException {
		InternalWallet model = new InternalWallet();
		model.setUserId(rs.getInt("USER_ID"));
        model.setBitcoin(rs.getString("BITCOIN")); 
        model.setBep20(rs.getString("BEP20"));  
        model.setErc20(rs.getString("ERC20"));
        model.setFavouriteTokens(rs.getString("FAVOURITE_TOKEN"));
		return model;
	}

    public InternalWalletDao() {
		super(TABLE_NAME);
	}

    public InternalWallet selectByUserId(int userId) {
        String sql = "SELECT * TBL_INTERNAL_WALLET WHERE USER_ID = ?";
		return jdbcTemplate.query(sql, new ResultSetExtractor<InternalWallet>() {
			@Override
			public InternalWallet extractData(ResultSet rs) throws SQLException {
				if (!rs.next())
					return null;
				return extract(rs);
			}
		}, userId);
    }

    public int insert(InternalWallet m) {
        String sql = "INSERT INTO TBL_INTERNAL_WALLET(USER_ID, BITCOIN, BEP20, ERC20, FAVOURITE_TOKEN)"
            + " VALUES(?,?,?,?,?)";
        return jdbcTemplate.update(sql, m.getUserId(), m.getBitcoin(), m.getBep20(), m.getErc20(), m.getFavouriteTokens());
    }

    public int update(InternalWallet m) {
        String sql = "UPDATE TBL_INTERNAL_BALANCE SET BITCOIN = ?, ERC20 = ?, BEP20 = ?, FAVOURITE_TOKEN = ? WHERE USER_ID = ?";
        return jdbcTemplate.update(sql, m.getBitcoin(), m.getErc20(), m.getBep20(), m.getFavouriteTokens(), m.getUserId());
    }

}
