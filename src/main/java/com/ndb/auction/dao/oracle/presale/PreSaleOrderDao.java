package com.ndb.auction.dao.oracle.presale;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.ndb.auction.dao.oracle.BaseOracleDao;
import com.ndb.auction.dao.oracle.Table;
import com.ndb.auction.models.presale.PreSaleOrder;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import lombok.NoArgsConstructor;

@Repository
@NoArgsConstructor
@Table(name="TBL_PRESALE_ORDER")
public class PreSaleOrderDao extends BaseOracleDao {

    private static PreSaleOrder extract(ResultSet rs) throws SQLException {
		PreSaleOrder m = new PreSaleOrder();
		m.setId(rs.getInt("ID"));
		m.setPresaleId(rs.getInt("PRESALE_ID"));
        m.setUserId(rs.getInt("USER_ID"));
        m.setDestination(rs.getInt("DESTINATION"));
        m.setExtAddr(rs.getString("EXT_ADDR"));
        m.setNdbAmount(rs.getLong("NDB_AMOUNT"));
        m.setNdbPrice(rs.getLong("NDB_PRICE"));
        m.setCreatedAt(rs.getTimestamp("STARTED_AT").getTime());
        m.setUpdatedAt(rs.getTimestamp("UPDATED_AT").getTime());
        return m;
	}

    public PreSaleOrder insert(PreSaleOrder m) {
        String sql = "INSERT INTO TBL_PRESALE_ORDER(ID,PRESALE_ID,USER_ID,DESTINATION, EXT_ADDR, NDB_AMOUNT,NDB_PRICE,STARTED_AT,ENDED_AT)"
            + "VALUES(SEQ_PRESALE_ORDER.NEXTVAL,?,?,?,?,?,?,SYSDATE,SYSDATE)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                new PreparedStatementCreator() {
                    @Override
                    public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                        PreparedStatement ps = connection.prepareStatement(sql, new String[] { "ID" });
                        int i = 1;
                        ps.setInt(i++, m.getPresaleId());
                        ps.setInt(i++, m.getUserId());
                        ps.setInt(i++, m.getDestination());
                        ps.setString(i++, m.getExtAddr());
                        ps.setLong(i++, m.getNdbAmount());
                        ps.setLong(i++, m.getNdbPrice());
                        return ps;
                    }
                }, keyHolder);
        m.setId(keyHolder.getKey().intValue());
        return m;
    }

    public List<PreSaleOrder> selectByPresaleId(int presaleId) {
        String sql = "SELECT * FROM TBL_PRESALE_ORDER WHERE PRESALE_ID = ?";
        return jdbcTemplate.query(sql, new RowMapper<PreSaleOrder>() {
			@Override
			public PreSaleOrder mapRow(ResultSet rs, int rownumber) throws SQLException {
				return extract(rs);
			}
		}, presaleId);
    }

    public List<PreSaleOrder> selectByUserId(int presaleId, int userId) {
        String sql = "SELECT * FROM TBL_PRESALE_ORDER WHERE PRESALE_ID = ? AND USER_ID = ?";
        return jdbcTemplate.query(sql, new RowMapper<PreSaleOrder>() {
			@Override
			public PreSaleOrder mapRow(ResultSet rs, int rownumber) throws SQLException {
				return extract(rs);
			}
		}, presaleId, userId);
    }

    public List<PreSaleOrder> selectAllByUserId (int userId) {
        String sql = "SELECT * FROM TBL_PRESALE_ORDER WHERE USER_ID = ?";
        return jdbcTemplate.query(sql, new RowMapper<PreSaleOrder>() {
			@Override
			public PreSaleOrder mapRow(ResultSet rs, int rownumber) throws SQLException {
				return extract(rs);
			}
		}, userId);
    }

    public PreSaleOrder selectById(int orderId) {
        String sql = "SELECT * FROM TBL_PRESALE_ORDER WHERE ID=?";
        return jdbcTemplate.query(sql, new ResultSetExtractor<PreSaleOrder>() {
			@Override
			public PreSaleOrder extractData(ResultSet rs) throws SQLException {
				if (!rs.next())
					return null;
				return extract(rs);
			}
		}, orderId);
    }

    public int updateStatus(int orderId) {
        String sql = "UPDATE TBL_PRESALE_ORDER SET STATUS = 1, UPDATED_AT=SYSDATE WHERE ID=?";
        return jdbcTemplate.update(sql, orderId);
    }

}
