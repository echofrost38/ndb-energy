package com.ndb.auction.dao.oracle.auth;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.ndb.auction.dao.oracle.BaseOracleDao;
import com.ndb.auction.dao.oracle.Table;
import com.ndb.auction.models.auth.PhoneResetRequest;

import org.springframework.stereotype.Repository;

import lombok.NoArgsConstructor;

@Repository
@NoArgsConstructor
@Table(name = "TBL_PHONE_RESET")
public class PhoneResetRequestDao extends BaseOracleDao {
    private static PhoneResetRequest extract(ResultSet rs) throws SQLException {
		PhoneResetRequest m = PhoneResetRequest.builder()
            .userId(rs.getInt("USER_ID"))
            .phone(rs.getString("PHONE"))
            .token(rs.getString("TOKEN"))
            .requestedAt(rs.getTimestamp("REQUESTED_AT").getTime())
            .updatedAt(rs.getTimestamp("UPDATED_AT").getTime())
            .status(rs.getInt("STATUS"))
            .build();
		return m;
	}

    public int save(PhoneResetRequest m) {
        var sql = "INSERT INTO TBL_PHONE_RESET(USER_ID, PHONE, TOKEN, REQUESTED_AT, UPDATED_AT, STATUS)" 
            + "VALUES(?,?,?,SYSDATE,SYSDATE,0)";
        return jdbcTemplate.update(sql, m.getUserId(), m.getPhone(), m.getToken());
    }

    public PhoneResetRequest selectById(int userId, String token) {
        var sql = "SELECT * FROM TBL_PHONE_RESET WHERE USER_ID = ? AND TOKEN = ? AND STATUS = 0";
        return jdbcTemplate.query(sql, rs -> {
			if (!rs.next())
				return null;
			return extract(rs);
		}, userId, token);
    }

    public List<PhoneResetRequest> selectByUser(int userId) {
        var sql = "SELECT * FROM TBL_PHONE_RESET WHERE USER_ID = ?";
        return jdbcTemplate.query(sql, (rs, rownumber) -> extract(rs), userId);
    }

    public int updateRequest(int userId, String token, int status) {
        var sql = "UPDATE TBL_PHONE_RESET SET STATUS = ?, UPDATED_AT = SYSDATE WHERE USER_ID = ? AND TOKEN = ?";
        return jdbcTemplate.update(sql, status, userId, token);
    }
}
