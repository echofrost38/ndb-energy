package com.ndb.auction.dao.oracle;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.ndb.auction.models.TokenAsset;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class TokenAssetDao extends BaseOracleDao {
    
    private static final String TABLE_NAME = "NDB.TBL_TOKEN_ASSET";

    private static TokenAsset extract(ResultSet rs) throws SQLException {
		TokenAsset model = new TokenAsset();
		model.setId(rs.getInt("ID"));
		model.setTokenName(rs.getString("TOKEN_NAME"));
        model.setTokenSymbol(rs.getString("TOKEN_SYMBOL"));
        model.setNetwork(rs.getString("NETWORK"));
        model.setAddress(rs.getString("ADDRESS"));
        model.setSymbol(rs.getString("SYMBOL"));
		return model;
	}

    public TokenAssetDao() {
		super(TABLE_NAME);
	}

    public TokenAsset selectById(int id) {
        String sql = "SELECT * FROM NDB.TBL_TOKEN_ASSET WHERE ID=? AND DELETED=0";
		return jdbcTemplate.query(sql, new ResultSetExtractor<TokenAsset>() {
			@Override
			public TokenAsset extractData(ResultSet rs) throws SQLException {
				if (!rs.next())
					return null;
				return extract(rs);
			}
		}, id);
    }

    public List<TokenAsset> selectAll(String orderby) {
		String sql = "SELECT * FROM NDB.TBL_TOKEN_ASSET";
		if (orderby == null)
			orderby = "ID";
		sql += " ORDER BY " + orderby;
		return jdbcTemplate.query(sql, new RowMapper<TokenAsset>() {
			@Override
			public TokenAsset mapRow(ResultSet rs, int rownumber) throws SQLException {
				return extract(rs);
			}
		});
	}

    public int insert(TokenAsset m) {
		String sql = "INSERT INTO TBL_TOKEN_ASSET(ID,TOKEN_NAME,TOKEN_SYMBOL,NETWORK,ADDRESS,SYMBOL)"
				+ "VALUES(SEQ_TOKEN_ASSET.NEXTVAL,?,?,?,?,?)";
		return jdbcTemplate.update(sql, m.getTokenName(), m.getTokenSymbol(), m.getNetwork(), m.getAddress(), m.getSymbol());
	}

    public int updateDeleted(int id) {
		String sql = "DELETE FROM TBL_TOKEN_ASSET WHERE ID=?";
		return jdbcTemplate.update(sql, id);
	}

}
