package com.ndb.auction.dao.oracle;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.ndb.auction.models.Shufti.ShuftiReference;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

import lombok.NoArgsConstructor;

@Repository
@NoArgsConstructor
@Table(name="TBL_SHUFTI_REF")
public class ShuftiDao extends BaseOracleDao {
    
    private static ShuftiReference extract(ResultSet rs) throws SQLException {
		ShuftiReference model = new ShuftiReference();
		model.setUserId(rs.getInt("USER_ID"));
        model.setReference(rs.getString("REFERENCE"));
		model.setVerificationType(rs.getString("VERIFY_TYPE"));
		model.setDocStatus(rs.getBoolean("DOC_STATUS"));
		model.setAddrStatus(rs.getBoolean("ADDR_STATUS"));
		model.setConStatus(rs.getBoolean("CON_STATUS"));
		model.setSelfieStatus(rs.getBoolean("SEL_STATUS"));
		return model;
	}

    public ShuftiReference selectById(int userId) {
        String sql = "SELECT * FROM TBL_SHUFTI_REF WHERE USER_ID=?";
		return jdbcTemplate.query(sql, new ResultSetExtractor<ShuftiReference>() {
			@Override
			public ShuftiReference extractData(ResultSet rs) throws SQLException {
				if (!rs.next())
					return null;
				return extract(rs);
			}
		}, userId);
    }

	public ShuftiReference selectByReference(String reference) {
		String sql = "SELECT * FROM TBL_SHUFTI_REF WHERE REFERENCE = ?";
		return jdbcTemplate.query(sql, new ResultSetExtractor<ShuftiReference>() {
			@Override
			public ShuftiReference extractData(ResultSet rs) throws SQLException {
				if (!rs.next())
					return null;
				return extract(rs);
			}
		}, reference);
	}

    public int insert(ShuftiReference m) {
        String sql = "INSERT INTO TBL_SHUFTI_REF(USER_ID, REFERENCE, VERIFY_TYPE, DOC_STATUS, ADDR_STATUS, CON_STATUS)"
				+ "VALUES(?,?,?,?,?,?)";
		return jdbcTemplate.update(sql, m.getUserId(), m.getReference(), m.getVerificationType(), m.getDocStatus(), m.getAddrStatus(), m.getConStatus());
    }

	public int updateReference (int userId, String reference) {
        String sql = "UPDATE TBL_SHUFTI_REF SET REFERENCE = ? WHERE USER_ID = ?";
		return jdbcTemplate.update(sql, reference, userId);
    }

	public int passed(int userId) {
		String sql = "UPDATE TBL_SHUFTI_REF SET DOC_STATUS = 1, ADDR_STATUS = 1, CON_STATUS = 1, SEL_STATUS = 1 WHERE USER_ID = ?";
		return jdbcTemplate.update(sql, userId);
	}

	public int updateDocStatus(int userId, Boolean status) {
		String sql = "UPDATE TBL_SHUFTI_REF SET DOC_STATUS = ? WHERE USER_ID = ?";
		return jdbcTemplate.update(sql, status, userId);
	}

	public int updateAddrStatus(int userId, Boolean status) {
		String sql = "UPDATE TBL_SHUFTI_REF SET ADDR_STATUS = ? WHERE USER_ID = ?";
		return jdbcTemplate.update(sql, status, userId);
	}

	public int updateConStatus(int userId, Boolean status) {
		String sql = "UPDATE TBL_SHUFTI_REF SET CON_STATUS = ? WHERE USER_ID = ?";
		return jdbcTemplate.update(sql, status, userId);
	}

	public int updateSelfieStatus(int userId, Boolean status) {
		String sql = "UPDATE TBL_SHUFTI_REF SET CON_STATUS = ? WHERE USER_ID = ?";
		return jdbcTemplate.update(sql, status, userId);
	}

}
