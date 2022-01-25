package com.ndb.auction.dao.oracle.presale;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.ndb.auction.dao.oracle.BaseOracleDao;
import com.ndb.auction.dao.oracle.Table;
import com.ndb.auction.models.presale.PreSaleOrder;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import lombok.NoArgsConstructor;

@Repository
@NoArgsConstructor
@Table(name="TBL_PRESALE_ORDER")
public class PreSaleOrderDao extends BaseOracleDao {

    private static PreSaleOrder extract(ResultSet rs) throws SQLException {
		PreSaleOrder m = new PreSaleOrder();
		m.setId(rs.getInt("ID"));
		m.setRoundId(rs.getInt("PRESALE_ID"));
        m.setUserId(rs.getInt("USER_ID"));
        m.setNdbAmount(rs.getLong("NDB_AMOUNT"));
        m.setNdbPrice(rs.getLong("NDB_PRICE"));
        m.setCreatedAt(rs.getTimestamp("STARTED_AT").getTime());
        m.setUpdatedAt(rs.getTimestamp("UPDATED_AT").getTime());
        return m;
	}

    public int insert(PreSaleOrder m) {
        String sql = "INSERT INTO TBL_PRESALE_ORDER(ID,ROUND_ID,USER_ID,NDB_AMOUNT,NDB_PRICE,STARTED_AT,ENDED_AT)"
            + "VALUES(SEQ_PRESALE_ORDER.NEXTVAL,?,?,?,?,SYSDATE,SYSDATE)";
        return jdbcTemplate.update(sql, m.getRoundId(), m.getUserId(), m.getNdbAmount(), m.getNdbPrice());
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

}
