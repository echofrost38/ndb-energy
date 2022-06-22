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
        m.setNdbAmount(rs.getDouble("NDB_AMOUNT"));
        m.setNdbPrice(rs.getDouble("NDB_PRICE"));
        m.setStatus(rs.getInt("STATUS"));
        m.setPrefix(rs.getString("PREFIX"));
        m.setName(rs.getString("NAME"));
        m.setCreatedAt(rs.getTimestamp("STARTED_AT").getTime());
        m.setUpdatedAt(rs.getTimestamp("UPDATED_AT").getTime());

        m.setPaymentId(rs.getInt("PAYMENT_ID"));
        m.setPaymentType(rs.getString("PAYMENT_TYPE"));
        m.setPaidAmount(rs.getDouble("PAID_AMOUNT"));
        return m;
	}

    public PreSaleOrder insert(PreSaleOrder m) {
        String sql = "INSERT INTO TBL_PRESALE_ORDER(ID,PRESALE_ID,USER_ID,DESTINATION, EXT_ADDR, NDB_AMOUNT,NDB_PRICE,STATUS,STARTED_AT,UPDATED_AT)"
            + "VALUES(SEQ_PRESALE_ORDER.NEXTVAL,?,?,?,?,?,?,?,SYSDATE,SYSDATE)";
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
                        ps.setDouble(i++, m.getNdbAmount());
                        ps.setDouble(i++, m.getNdbPrice());
                        ps.setInt(i++, m.getStatus());
                        return ps;
                    }
                }, keyHolder);
        m.setId(keyHolder.getKey().intValue());
        return m;
    }

    public List<PreSaleOrder> selectByPresaleId(int presaleId) {
        String sql = "SELECT TBL_PRESALE_ORDER.*, TBL_USER_AVATAR.PREFIX, TBL_USER_AVATAR.NAME FROM TBL_PRESALE_ORDER LEFT JOIN TBL_USER_AVATAR ON TBL_PRESALE_ORDER.USER_ID=TBL_USER_AVATAR.ID WHERE TBL_PRESALE_ORDER.PRESALE_ID = ? AND TBL_PRESALE_ORDER.STATUS = 1 ORDER BY TBL_PRESALE_ORDER.NDB_AMOUNT DESC";
        return jdbcTemplate.query(sql, new RowMapper<PreSaleOrder>() {
			@Override
			public PreSaleOrder mapRow(ResultSet rs, int rownumber) throws SQLException {
				return extract(rs);
			}
		}, presaleId);
    }

    public List<PreSaleOrder> selectByUserId(int presaleId, int userId) {
        String sql = "SELECT TBL_PRESALE_ORDER.*, TBL_USER_AVATAR.PREFIX, TBL_USER_AVATAR.NAME FROM TBL_PRESALE_ORDER LEFT JOIN TBL_USER_AVATAR ON TBL_PRESALE_ORDER.USER_ID=TBL_USER_AVATAR.ID WHERE TBL_PRESALE_ORDER.USER_ID = ? AND TBL_PRESALE_ORDER.PRESALE_ID = ? ORDER BY TBL_PRESALE_ORDER.NDB_AMOUNT DESC";
        return jdbcTemplate.query(sql, new RowMapper<PreSaleOrder>() {
			@Override
			public PreSaleOrder mapRow(ResultSet rs, int rownumber) throws SQLException {
				return extract(rs);
			}
		}, userId, presaleId);
    }

    public List<PreSaleOrder> selectAllByUserId (int userId) {
        String sql = "SELECT TBL_PRESALE_ORDER.*, TBL_USER_AVATAR.PREFIX, TBL_USER_AVATAR.NAME FROM TBL_PRESALE_ORDER LEFT JOIN TBL_USER_AVATAR ON TBL_PRESALE_ORDER.USER_ID=TBL_USER_AVATAR.ID WHERE TBL_PRESALE_ORDER.USER_ID = ?ORDER BY TBL_PRESALE_ORDER.NDB_AMOUNT DESC";
        return jdbcTemplate.query(sql, new RowMapper<PreSaleOrder>() {
			@Override
			public PreSaleOrder mapRow(ResultSet rs, int rownumber) throws SQLException {
				return extract(rs);
			}
		}, userId);
    }

    public PreSaleOrder selectById(int orderId) {
        String sql = "SELECT TBL_PRESALE_ORDER.*, TBL_USER_AVATAR.PREFIX, TBL_USER_AVATAR.NAME FROM TBL_PRESALE_ORDER LEFT JOIN TBL_USER_AVATAR ON TBL_PRESALE_ORDER.USER_ID=TBL_USER_AVATAR.ID WHERE TBL_PRESALE_ORDER.ID = ?";
        return jdbcTemplate.query(sql, new ResultSetExtractor<PreSaleOrder>() {
			@Override
			public PreSaleOrder extractData(ResultSet rs) throws SQLException {
				if (!rs.next())
					return null;
				return extract(rs);
			}
		}, orderId);
    }

    public int updateStatus(int orderId, int paymentId, double paidAmount, String paymentType) {
        String sql = "UPDATE TBL_PRESALE_ORDER SET STATUS = 1,PAYMENT_ID=?,PAYMENT_TYPE=?,PAID_AMOUNT=?, UPDATED_AT=SYSDATE WHERE ID=?";
        return jdbcTemplate.update(sql, paymentId, paymentType, paidAmount, orderId);
    }

}
