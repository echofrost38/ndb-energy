package com.ndb.auction.dao.oracle;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.ndb.auction.models.Shufti.ShuftiReference;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

@Repository
public class ShuftiDao extends BaseOracleDao {
    
    private static final String TABLE_NAME = "NDB.TBL_SHUFTI_REF";

    private static ShuftiReference extract(ResultSet rs) throws SQLException {
		ShuftiReference model = new ShuftiReference();
		model.setUserId(rs.getInt("USER_ID"));
        model.setReference(rs.getString("REFERENCE"));
		model.setVerificationType(rs.getString("VERIFY_TYPE"));
		return model;
	}

    public ShuftiDao() {
		super(TABLE_NAME);
	}

    public ShuftiReference selectById(int userId) {
        String sql = "SELECT * FROM NDB.TBL_SHUFTI_REF WHERE USER_ID=?";
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
		String sql = "SELECT * FROM NDB.TBL_SHUFTI_REF WHERE REFERENCE = ?";
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
        String sql = "INSERT INTO TBL_TOKEN_ASSET(USER_ID, REFERENCE, VERIFY_TYPE)"
				+ "VALUES(?,?,?)";
		return jdbcTemplate.update(sql, m.getUserId(), m.getReference(), m.getVerificationType());
    }

}
