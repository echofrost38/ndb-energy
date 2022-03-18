package com.ndb.auction.dao.oracle;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

import com.ndb.auction.models.FavorAsset;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

import lombok.NoArgsConstructor;

@Repository
@NoArgsConstructor
@Table(name = "TBL_FAVOR_TOKEN")
public class FavorAssetDao extends BaseOracleDao {
    
    private static FavorAsset extract(ResultSet rs) throws SQLException {
		FavorAsset model = new FavorAsset();
        model.setUserId(rs.getInt("USER_ID"));
        String rawString = rs.getString("ASSETS");
		String[] assetArray = rawString.split(",");
        model.setAssets(Arrays.asList(assetArray));
		return model;
	}

    public int insertOrUpdate(FavorAsset m) {
        String sql = "MERGE INTO TBL_FAVOR_TOKEN USING DUAL ON (USER_ID=?)"
				+ "WHEN MATCHED THEN UPDATE SET ASSETS=?"
				+ "WHEN NOT MATCHED THEN INSERT(USER_ID, ASSETS)"
				+ "VALUES(?,?)";
        String rawString = String.join(",", m.getAssets());
		return jdbcTemplate.update(sql, m.getUserId(), rawString, m.getUserId(), rawString);
    }

    public FavorAsset selectByUserId(int userId) {
        String sql = "SELECT * FROM TBL_FAVOR_TOKEN WHERE USER_ID=?";
		return jdbcTemplate.query(sql, new ResultSetExtractor<FavorAsset>() {
			@Override
			public FavorAsset extractData(ResultSet rs) throws SQLException {
				if (!rs.next())
					return null;
				return extract(rs);
			}
		}, userId);
    }

}
