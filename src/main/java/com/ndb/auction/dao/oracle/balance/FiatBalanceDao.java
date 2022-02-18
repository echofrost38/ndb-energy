package com.ndb.auction.dao.oracle.balance;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.ndb.auction.dao.oracle.BaseOracleDao;
import com.ndb.auction.dao.oracle.Table;
import com.ndb.auction.models.balance.FiatBalance;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import lombok.NoArgsConstructor;

@Repository
@NoArgsConstructor
@Table(name = "TBL_FIAT_BALANCE")
public class FiatBalanceDao extends BaseOracleDao {
    
    private static FiatBalance extract(ResultSet rs) throws SQLException {
		FiatBalance model = new FiatBalance();
		model.setUserId(rs.getInt("USER_ID"));
        model.setFiatId(rs.getInt("FIAT_ID"));   
        model.setFree(rs.getDouble("FREE"));
        model.setHold(rs.getDouble("HOLD"));
		return model;
	}

    public int insert(FiatBalance m) {
        String sql = "INSERT INTO TBL_FIAT_BALANCE(USER_ID, FIAT_ID, FREE, HOLD)"
            + "VALUES(?,?,?,?)";
        return jdbcTemplate.update(sql, m.getUserId(), m.getFiatId(), m.getFree(), m.getHold());
    }

    public List<FiatBalance> selectByUserId(int userId, String orderBy) {
        String sql = "SELECT * FROM TBL_FIAT_BALANCE WHERE USER_ID = ?";
        if(orderBy == null) {
            orderBy = "FREE";
        }
        sql += " ORDER BY " + orderBy;
        return jdbcTemplate.query(sql, new RowMapper<FiatBalance>() {
			@Override
			public FiatBalance mapRow(ResultSet rs, int rownumber) throws SQLException {
				return extract(rs);
			}
		}, userId);
    }

    public FiatBalance selectById(int userId, int fiatId) {
        String sql = "SELECT * FROM TBL_FIAT_BALANCE WHERE USER_ID = ? AND FIAT_ID = ?";
        return jdbcTemplate.query(sql, new ResultSetExtractor<FiatBalance>() {
			@Override
			public FiatBalance extractData(ResultSet rs) throws SQLException {
				if (!rs.next())
					return null;
				return extract(rs);
			}
		}, userId, fiatId);
    }

    public int addFreeBalance(int userId, int fiatId, double free) {
        String sql = "MERGE INTO TBL_FIAT_BALANCE USING DUAL ON (USER_ID=? AND FIAT_ID=?)"
				+ "WHEN MATCHED THEN UPDATE SET FREE=FREE+? "
				+ "WHEN NOT MATCHED THEN INSERT(USER_ID,FIAT_ID,FREE,HOLD)"
				+ "VALUES(?,?,?,?)";
		return jdbcTemplate.update(sql, userId, fiatId, free, userId, fiatId, free, 0);
    }

    public int addHoldBalance(int userId, int fiatId, double amount) {
		String sql = "MERGE INTO TBL_FIAT_BALANCE USING DUAL ON (USER_ID=? AND FIAT_ID=?)"
				+ "WHEN MATCHED THEN UPDATE SET HOLD=HOLD+? "
				+ "WHEN NOT MATCHED THEN INSERT(USER_ID,FIAT_ID,FREE,HOLD)"
				+ "VALUES(?,?,?,?)";
		return jdbcTemplate.update(sql, userId, fiatId, amount, userId, fiatId, amount, 0);
	}

    public int makeHoldBalance(int userId, int fiatId, double amount) {
		String sql = "UPDATE TBL_FIAT_BALANCE SET FREE = FREE - ?, HOLD = HOLD + ? WHERE USER_ID = ? AND FIAT_ID = ?";
		return jdbcTemplate.update(sql, amount, amount, userId, fiatId);
	}

    public int releaseHoldBalance(int userId, int fiatId, double amount) {
		String sql = "UPDATE TBL_FIAT_BALANCE SET FREE = FREE + ?, HOLD = HOLD - ? WHERE USER_ID = ? AND FIAT_ID = ?";
		return jdbcTemplate.update(sql, amount, amount, userId, fiatId);
	}

	public int deductFreeBalance(int userId, int fiatId, double amount) {
		String sql = "UPDATE TBL_FIAT_BALANCE SET FREE = FREE - ? WHERE USER_ID = ? AND FIAT_ID = ?";
		return jdbcTemplate.update(sql, amount, userId, fiatId);
	}

	public int deductHoldBalance(int userId, int fiatId, double amount) {
		String sql = "UPDATE TBL_FIAT_BALANCE SET HOLD = HOLD - ? WHERE USER_ID = ? AND FIAT_ID = ?";
		return jdbcTemplate.update(sql, amount, userId, fiatId);
	}

}
