package com.ndb.auction.dao.oracle.verify;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.ndb.auction.dao.oracle.BaseOracleDao;
import com.ndb.auction.dao.oracle.Table;
import com.ndb.auction.models.KYCSetting;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import lombok.NoArgsConstructor;

@Repository
@NoArgsConstructor
@Table(name = "TBL_KYC_SETTING")
public class KycSettingDao extends BaseOracleDao{
    
    private static KYCSetting extract(ResultSet rs) throws SQLException {
		KYCSetting m = new KYCSetting();
		m.setKind(rs.getString("KIND"));   
        m.setBid(rs.getDouble("BID"));
        m.setDeposit(rs.getDouble("DEPOSIT"));
        m.setWithdraw(rs.getDouble("WITHDRAW"));
        m.setDirect(rs.getDouble("DIRECT"));
		return m;
	}

    public int updateKYCSetting(String kind, Double bid, Double direct, Double deposit, Double withdraw) {
        String sql = "UPDATE TBL_KYC_SETTING SET BID=?,DIRECT=?,DEPOSIT=?,WITHDRAW=? WHERE KIND=?";
        return jdbcTemplate.update(sql, bid, direct, deposit, withdraw, kind);
    }

    public List<KYCSetting> getKYCSettings() {
        String sql = "SELECT * FROM TBL_KYC_SETTING ORDER BY KIND DESC";
		return jdbcTemplate.query(sql, new RowMapper<KYCSetting>() {
			@Override
			public KYCSetting mapRow(ResultSet rs, int rownumber) throws SQLException {
				return extract(rs);
			}
		});
    }

}
