package com.ndb.auction.dao.oracle.presale;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import com.ndb.auction.dao.oracle.BaseOracleDao;
import com.ndb.auction.dao.oracle.Table;
import com.ndb.auction.models.presale.PreSale;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import lombok.NoArgsConstructor;

@Repository
@NoArgsConstructor
@Table(name="TBL_PRESALE")
public class PreSaleDao extends BaseOracleDao {
    
    private static PreSale extract(ResultSet rs) throws SQLException {
		PreSale m = new PreSale();
		m.setId(rs.getInt("ID"));
        m.setRound(rs.getInt("ROUND"));
        m.setStartedAt(rs.getTimestamp("STARTED_AT").getTime());
        m.setEndedAt(rs.getTimestamp("ENDED_AT").getTime());
        m.setTokenAmount(rs.getDouble("TOKEN_AMOUNT"));
        m.setTokenPrice(rs.getDouble("TOKEN_PRICE"));
        m.setSold(rs.getDouble("SOLD"));
        m.setStatus(rs.getInt("STATUS"));
		m.setKind(2);
		return m;
	}

	public int getNewRound() {
		String sql = "SELECT MAX(ROUND) LAST_ROUND FROM TBL_PRESALE";
		return jdbcTemplate.query(sql, new ResultSetExtractor<Integer>() {
			@Override
			public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
				if(!rs.next())
					return null;
				return rs.getInt("LAST_ROUND") + 1;					
			}
		});
	}

    public PreSale insert(PreSale m) {
        String sql = "INSERT INTO TBL_PRESALE(ID,ROUND,STARTED_AT,ENDED_AT,TOKEN_AMOUNT,TOKEN_PRICE,SOLD,STATUS)"
            + "VALUES(SEQ_PRESALE.NEXTVAL,?,?,?,?,?,?,?)";
			KeyHolder keyHolder = new GeneratedKeyHolder();
			jdbcTemplate.update(
				new PreparedStatementCreator() {
					@Override
					public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
						PreparedStatement ps = connection.prepareStatement(sql.toString(),
								new String[] { "ID" });
						int i = 1;
						ps.setInt(i++, m.getRound());
						ps.setTimestamp(i++, new Timestamp(m.getStartedAt()));
						ps.setTimestamp(i++, new Timestamp(m.getEndedAt()));
						ps.setDouble(i++, m.getTokenAmount());
						ps.setDouble(i++, m.getTokenPrice());
						ps.setDouble(i++, m.getSold());
						ps.setInt(i++, m.getStatus());
						return ps;
					}
				}, keyHolder);
			m.setId(keyHolder.getKey().intValue());
			return m;
    }

    public PreSale selectById(int id) {
		String sql = "SELECT * FROM TBL_PRESALE WHERE ID=?";
		return jdbcTemplate.query(sql, new ResultSetExtractor<PreSale>() {
			@Override
			public PreSale extractData(ResultSet rs) throws SQLException {
				if (!rs.next())
					return null;
				return extract(rs);
			}
		}, id);
	}

    public PreSale selectByRound(int round) {
		String sql = "SELECT * FROM TBL_PRESALE WHERE ROUND=?";
		return jdbcTemplate.query(sql, new ResultSetExtractor<PreSale>() {
			@Override
			public PreSale extractData(ResultSet rs) throws SQLException {
				if (!rs.next())
					return null;
				return extract(rs);
			}
		}, round);
	}

    public List<PreSale> selectAll() {
        String sql = "SELECT * FROM TBL_PRESALE ORDER BY ROUND";
		return jdbcTemplate.query(sql, new RowMapper<PreSale>() {
			@Override
			public PreSale mapRow(ResultSet rs, int rownumber) throws SQLException {
				return extract(rs);
			}
		});
    }

    public List<PreSale> selectByStatus(int status) {
        String sql = "SELECT * FROM TBL_PRESALE WHERE STATUS=? ORDER BY ROUND";
		return jdbcTemplate.query(sql, new RowMapper<PreSale>() {
			@Override
			public PreSale mapRow(ResultSet rs, int rownumber) throws SQLException {
				return extract(rs);
			}
		}, status);
    }

    public int updateSold(int id, double amount) {
        String sql = "UPDATE TBL_PRESALE SET SOLD=SOLD+? WHERE ID=?";
        return jdbcTemplate.update(sql, amount, id);
    }   

	public int updateStatus(int presaleId, int status) {
		String sql = "UPDATE TBL_PRESALE SET STATUS=? WHERE ID = ?";
		return jdbcTemplate.update(sql, status, presaleId);
	}

}
