package com.ndb.auction.dao.oracle.avatar;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.ndb.auction.dao.oracle.BaseOracleDao;
import com.ndb.auction.dao.oracle.Table;
import com.ndb.auction.models.avatar.AvatarComponent;
import com.ndb.auction.models.avatar.AvatarSet;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import lombok.NoArgsConstructor;

@Repository
@NoArgsConstructor
@Table(name = "TBL_AVATAR_COMPONENT")
public class AvatarComponentDao extends BaseOracleDao {

	private static AvatarComponent extract(ResultSet rs) throws SQLException {
		AvatarComponent m = new AvatarComponent();
		m.setGroupId(rs.getString("GROUP_ID"));
		m.setCompId(rs.getInt("COMP_ID"));
		m.setTierLevel(rs.getInt("TIER_LEVEL"));
		m.setPrice(rs.getDouble("PRICE"));
		m.setLimited(rs.getInt("LIMITED"));
		m.setPurchased(rs.getInt("PURCHASED"));
		m.setSvg(rs.getString("SVG"));
		m.setWidth(rs.getInt("WIDTH"));
		m.setTop(rs.getInt("TOP"));
		m.setLeft(rs.getInt("LEFT"));
		return m;
	}

	public AvatarComponent createAvatarComponent(AvatarComponent m) {
		String sql = "INSERT INTO TBL_AVATAR_COMPONENT(GROUP_ID,COMP_ID,TIER_LEVEL,PRICE,LIMITED,PURCHASED,SVG,WIDTH,TOP,LEFT)"
				+ "VALUES(?,SEQ_AVATAR_COMPONENT.NEXTVAL,?,?,?,?,?,?,?,?)";

		KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                new PreparedStatementCreator() {
                    @Override
                    public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                        PreparedStatement ps = connection.prepareStatement(sql,
                                new String[] { "COMP_ID" });
                        int i = 1;
                        ps.setString(i++, m.getGroupId());
						ps.setInt(i++, m.getTierLevel());
						ps.setDouble(i++, m.getPrice());
						ps.setInt(i++, m.getLimited());
						ps.setInt(i++, m.getPurchased());
						ps.setString(i++, m.getSvg());
						ps.setInt(i++, m.getWidth());
						ps.setInt(i++, m.getTop());
						ps.setInt(i++, m.getLeft());
                        return ps;
                    }
                }, keyHolder);
        m.setCompId(keyHolder.getKey().intValue());
		return m;
	}

	public List<AvatarComponent> getAvatarComponents() {
		String sql = "SELECT * FROM TBL_AVATAR_COMPONENT ORDER BY GROUP_ID";
		return jdbcTemplate.query(sql, new RowMapper<AvatarComponent>() {
			@Override
			public AvatarComponent mapRow(ResultSet rs, int rownumber) throws SQLException {
				return extract(rs);
			}
		});
	}

	public List<AvatarComponent> getAvatarComponentsByGid(String groupId) {
		String sql = "SELECT * FROM TBL_AVATAR_COMPONENT WHERE GROUP_ID=? ORDER BY COMP_ID";
		return jdbcTemplate.query(sql, new RowMapper<AvatarComponent>() {
			@Override
			public AvatarComponent mapRow(ResultSet rs, int rownumber) throws SQLException {
				return extract(rs);
			}
		}, groupId);
	}

	public AvatarComponent getAvatarComponent(String groupId, int sKey) {
		String sql = "SELECT * FROM TBL_AVATAR_COMPONENT WHERE GROUP_ID=? AND COMP_ID=?";
		return jdbcTemplate.query(sql, new ResultSetExtractor<AvatarComponent>() {
			@Override
			public AvatarComponent extractData(ResultSet rs) throws SQLException {
				if (!rs.next())
					return null;
				return extract(rs);
			}
		}, groupId, sKey);
	}

	public AvatarComponent updateAvatarComponent(AvatarComponent m) {
		String sql = "UPDATE TBL_AVATAR_COMPONENT SET TIER_LEVEL=?, PRICE=?, LIMITED=?, PURCHASED=?, SVG=TO_CLOB(?),WIDTH=?,TOP=?,LEFT=? WHERE GROUP_ID=? AND COMP_ID=?";
		jdbcTemplate.update(sql, m.getTierLevel(), m.getPrice(), m.getLimited(), m.getPurchased(), m.getSvg(),
				 m.getWidth(), m.getTop(), m.getLeft(), m.getGroupId(), m.getCompId());
		return m;
	}

	public List<AvatarComponent> getAvatarComponentsBySet(List<AvatarSet> set) {
		List<AvatarComponent> list = new ArrayList<>();

		for (AvatarSet field : set) {
			String groupId = field.getGroupId();
			int compId = field.getCompId();
			list.add(getAvatarComponent(groupId, compId));
		}

		return list;
	}

}
