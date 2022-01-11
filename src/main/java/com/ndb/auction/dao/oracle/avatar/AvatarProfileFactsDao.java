package com.ndb.auction.dao.oracle.avatar;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.ndb.auction.dao.oracle.BaseOracleDao;
import com.ndb.auction.dao.oracle.Table;
import com.ndb.auction.models.avatar.Facts;

import org.springframework.stereotype.Repository;

import lombok.NoArgsConstructor;

@Repository
@NoArgsConstructor
@Table(name = "TBL_AVATAR_PROFILE_FACTS")
public class AvatarProfileFactsDao extends BaseOracleDao {
    
    private static Facts extract(ResultSet rs) throws SQLException {
        Facts m = new Facts();
		m.setId(rs.getInt("ID"));
		m.setTopic(rs.getString("TOPIC"));
        m.setDetail(rs.getString("DETAIL"));
		return m;
    }

    public List<AvatarSet> selectById(int id) {
		String sql = "SELECT * FROM TBL_AVATAR_PROFILE_SET WHERE ID=?";
		return jdbcTemplate.query(sql, new RowMapper<AvatarSet>() {
			@Override
			public AvatarSet mapRow(ResultSet rs, int rownumber) throws SQLException {
				return extract(rs);
			}
		});
	}

}
