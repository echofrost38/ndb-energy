package com.ndb.auction.dao.oracle.other;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.ndb.auction.dao.oracle.BaseOracleDao;
import com.ndb.auction.dao.oracle.Table;
import com.ndb.auction.models.tier.WalletTask;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import lombok.NoArgsConstructor;

@Repository
@NoArgsConstructor
@Table(name = "TBL_TASK_SETTING_WALLET")
public class TaskSettingWalletDao extends BaseOracleDao {
    
    private static WalletTask extract(ResultSet rs) throws SQLException {
		WalletTask model = new WalletTask();
		model.setAmount(rs.getInt("AMOUNT"));
		model.setPoint(rs.getDouble("POINT"));
		return model;
	}

    public List<WalletTask> selectAll() {
		String sql = "SELECT * FROM TBL_TASK_SETTING_WALLET ORDER BY AMOUNT DESC";
		return jdbcTemplate.query(sql, new RowMapper<WalletTask>() {
			@Override
			public WalletTask mapRow(ResultSet rs, int rownumber) throws SQLException {
				return extract(rs);
			}
		});
	}

    public int insert(WalletTask m) {
		String sql = "INSERT INTO TBL_TASK_SETTING_WALLET(AMOUNT, POINT)"
				+ "VALUES(?, ?)";
		return jdbcTemplate.update(sql, m.getAmount(), m.getPoint());
	}

    public int updateAll(List<WalletTask> list) {
		deleteAll();
		int result = 0;
		for (WalletTask m : list) {
			result += insert(m);
		}
		return result;
	}

}
