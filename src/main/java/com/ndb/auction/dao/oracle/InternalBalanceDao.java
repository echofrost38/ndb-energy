package com.ndb.auction.dao.oracle;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.ndb.auction.models.InternalBalance;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class InternalBalanceDao extends BaseOracleDao {
    
    private static final String TABLE_NAME = "NDB.TBL_INTERNAL_BALANCE";

    private static InternalBalance extract(ResultSet rs) throws SQLException {
		InternalBalance model = new InternalBalance();
		model.setUserId(rs.getInt("USER_ID"));
        model.setTokenId(rs.getInt("TOKEN_ID"));   
        model.setFree(rs.getDouble("FREE"));
        model.setHold(rs.getDouble("HOLD"));
		return model;
	}

    public InternalBalanceDao() {
		super(TABLE_NAME);
	}

    public List<InternalBalance> selectByUserId(int userId, String orderby) {
        String sql = "SELECT * FROM TBL_INTERNAL_BALANCE WHERE USER_ID = ?";
		if (orderby == null)
			orderby = "FREE";
		sql += " ORDER BY " + orderby;
		return jdbcTemplate.query(sql, new RowMapper<InternalBalance>() {
			@Override
			public InternalBalance mapRow(ResultSet rs, int rownumber) throws SQLException {
				return extract(rs);
			}
		}, userId);
    }

	public InternalBalance selectById(int userId, int tokenId) {
		String sql = "SELECT * FROM TBL_INTERNAL_BALANCE WHERE USER_ID = ? AND TOKEN_ID = ?";
		return jdbcTemplate.query(sql, new ResultSetExtractor<InternalBalance>() {
			@Override
			public InternalBalance extractData(ResultSet rs) throws SQLException {
				if (!rs.next())
					return null;
				return extract(rs);
			}
		}, userId, tokenId);
	}

    public int insert(InternalBalance m) {
        String sql = "INSERT INTO TBL_INTERNAL_BALANCE(USER_ID,TOKEN_ID,FREE,HOLD)"
				+ "VALUES(?,?,?,?)";
		return jdbcTemplate.update(sql, m.getUserId(), m.getTokenId(), m.getFree(), m.getHold());
    }

    public int update(InternalBalance m) {
        String sql = "UPDATE TBL_INTERNAL_BALANCE SET FREE = ?, HOLD = ? WHERE USER_ID = ? AND TOKEN_ID = ?";
        return jdbcTemplate.update(sql, m.getFree(), m.getHold(), m.getUserId(), m.getTokenId());
    }

	public int addFreeBalance(int userId, int tokenId, double amount) {
		String sql = "MERGE INTO TBL_INTERNAL_BALANCE USING DUAL ON (USER_ID=? AND TOKEN_ID=?)"
				+ "WHEN MATCHED THEN UPDATE SET FREE=FREE+? "
				+ "WHEN NOT MATCHED THEN INSERT(USER_ID,TOKEN_ID,FREE,HOLD)"
				+ "VALUES(?,?,?,?)";
		return jdbcTemplate.update(sql, userId, tokenId, amount, userId, tokenId, amount, 0);
	}

}
