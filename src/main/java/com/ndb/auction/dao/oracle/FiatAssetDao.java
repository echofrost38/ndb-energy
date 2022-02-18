package com.ndb.auction.dao.oracle;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.ndb.auction.models.FiatAsset;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import lombok.NoArgsConstructor;

@Repository
@NoArgsConstructor
@Table(name="TBL_FIAT_ASSET")
public class FiatAssetDao extends BaseOracleDao {
    
    private static FiatAsset extract(ResultSet rs) throws SQLException {
		FiatAsset model = new FiatAsset();
		model.setId(rs.getInt("ID"));
        model.setName(rs.getString("NAME"));
        model.setSymbol(rs.getString("SYMBOL"));
		return model;
	}

    public int insert(FiatAsset m) {
        String sql = "INSERT INTO TBL_FIAT_ASSET(ID,NAME,SYMBOL)"
            + "VALUES(SEQ_FIAT_ASSET.NEXTVAL,?,?)";
        return jdbcTemplate.update(sql, m.getName(), m.getSymbol());
    }

    public FiatAsset selectById(int id) {
        String sql = "SELECT * FROM TBL_FIAT_ASSET WHERE ID = ?";
        return jdbcTemplate.query(sql, new ResultSetExtractor<FiatAsset>() {
			@Override
			public FiatAsset extractData(ResultSet rs) throws SQLException {
				if (!rs.next())
					return null;
				return extract(rs);
			}
		}, id);
    }

    public FiatAsset selectBySymbol(String name) {
        String sql = "SELECT * FROM TBL_FIAT_ASSET WHERE NAME = ?";
        return jdbcTemplate.query(sql, new ResultSetExtractor<FiatAsset>() {
			@Override
			public FiatAsset extractData(ResultSet rs) throws SQLException {
				if (!rs.next())
					return null;
				return extract(rs);
			}
		}, name);
    }
    
    public List<FiatAsset> selectAll(String orderby) {
		String sql = "SELECT * FROM TBL_FIAT_ASSET";
		if (orderby == null)
			orderby = "ID";
		sql += " ORDER BY " + orderby;
		return jdbcTemplate.query(sql, new RowMapper<FiatAsset>() {
			@Override
			public FiatAsset mapRow(ResultSet rs, int rownumber) throws SQLException {
				return extract(rs);
			}
		});
	}

}
