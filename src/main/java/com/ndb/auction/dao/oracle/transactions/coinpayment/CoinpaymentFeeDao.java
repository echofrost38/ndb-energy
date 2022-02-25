package com.ndb.auction.dao.oracle.transactions.coinpayment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.ndb.auction.dao.oracle.BaseOracleDao;
import com.ndb.auction.dao.oracle.Table;
import com.ndb.auction.models.transactions.coinpayment.CoinpaymentFee;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import lombok.NoArgsConstructor;

@Repository
@NoArgsConstructor
@Table(name = "TBL_COINPAY_FEE")
public class CoinpaymentFeeDao extends BaseOracleDao {
    
    private static CoinpaymentFee extract(ResultSet rs) throws SQLException {
		CoinpaymentFee m = new CoinpaymentFee();
		m.setId(rs.getInt("ID"));
		m.setTierLevel(rs.getInt("TIER_LEVEL"));
        m.setFee(rs.getDouble("FEE"));
		return m;
	}

    public CoinpaymentFee insert(CoinpaymentFee m) {
        String sql = "INSERT INTO TBL_COINPAY_FEE(ID,TIER_LEVEL,FEE)VALUES(SEQ_COINPAY_FEE.NEXTVAL,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(
			new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
					PreparedStatement ps = connection.prepareStatement(sql,
							new String[] { "ID" });
					int i = 1;
					ps.setInt(i++, m.getId());
					ps.setInt(i++, m.getTierLevel());
                    ps.setDouble(i++, m.getFee());
					return ps;
				}
			}, keyHolder);
		m.setId(keyHolder.getKey().intValue());
		return m;
    }

    public List<CoinpaymentFee> selectAll() {
        String sql = "SELECT * FROM TBL_COINPAY_FEE ORDER BY TIER_LEVEL";
        return jdbcTemplate.query(sql, new RowMapper<CoinpaymentFee>() {
			@Override
			public CoinpaymentFee mapRow(ResultSet rs, int rownumber) throws SQLException {
				return extract(rs);
			}
		});
    }

    public int update(CoinpaymentFee m) {
        String sql = "UPDATE TBL_COINPAY_FEE SET TIER_LEVEL=?,FEE=? WHERE ID = ?";
        return jdbcTemplate.update(sql, m.getTierLevel(), m.getFee(), m.getId());
    }

}
