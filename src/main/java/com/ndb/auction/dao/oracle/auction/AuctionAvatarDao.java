package com.ndb.auction.dao.oracle.auction;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.ndb.auction.dao.oracle.BaseOracleDao;
import com.ndb.auction.dao.oracle.Table;
import com.ndb.auction.models.AvatarSet;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import lombok.NoArgsConstructor;

@Repository
@NoArgsConstructor
@Table(name = "TBL_AUCTION_AVATAR")
public class AuctionAvatarDao extends BaseOracleDao {

	private static AvatarSet extract(ResultSet rs) throws SQLException {
		AvatarSet model = new AvatarSet();
		model.setId(rs.getInt("ID"));
		model.setGroupId(rs.getInt("GROUP_ID"));
		model.setCompId(rs.getInt("COMP_ID"));
		return model;
	}

	public List<AvatarSet> selectById(int id) {
		String sql = "SELECT * FROM TBL_AUCTION_AVATAR WHERE ID=?";
		return jdbcTemplate.query(sql, new RowMapper<AvatarSet>() {
			@Override
			public AvatarSet mapRow(ResultSet rs, int rownumber) throws SQLException {
				return extract(rs);
			}
		});
	}

	public int insert(AvatarSet m) {
		String sql = "INSERT INTO TBL_AUCTION_AVATAR(ID,GROUP_ID,COMP_ID)"
				+ "VALUES(?,?,?)";
		return jdbcTemplate.update(sql, m.getId(), m.getGroupId(), m.getCompId());
	}

	@Override
	public int deleteById(int id) {
		String sql = "DELETE FROM TBL_AUCTION_AVATAR WHERE ID=?";
		return jdbcTemplate.update(sql, id);
	}

	public int update(int id, List<AvatarSet> list) {
		deleteById(id);
		int result = 0;
		for (AvatarSet m : list) {
			m.setId(id);
			result += insert(m);
		}
		return result;
	}

}
