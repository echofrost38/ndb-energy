package com.ndb.auction.dao.oracle.avatar;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.ndb.auction.dao.oracle.BaseOracleDao;
import com.ndb.auction.dao.oracle.Table;
import com.ndb.auction.models.avatar.AvatarFacts;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import lombok.NoArgsConstructor;

@Repository
@NoArgsConstructor
@Table(name = "TBL_AVATAR_PROFILE_FACTS")
public class AvatarProfileFactsDao extends BaseOracleDao {
    
    private static AvatarFacts extract(ResultSet rs) throws SQLException {
        AvatarFacts m = new AvatarFacts();
		m.setId(rs.getInt("ID"));
		m.setProfileId(rs.getInt("PROFILE_ID"));	
		m.setTopic(rs.getString("TOPIC"));
        m.setDetail(rs.getString("DETAIL"));
		return m;
    }

    public List<AvatarFacts> selectByProfileId(int profileId) {
		String sql = "SELECT * FROM TBL_AVATAR_PROFILE_FACTS WHERE PROFILE_ID=? ORDER BY ID";
		return jdbcTemplate.query(sql, new RowMapper<AvatarFacts>() {
			@Override
			public AvatarFacts mapRow(ResultSet rs, int rownumber) throws SQLException {
				return extract(rs);
			}
		}, profileId);
	}

	public AvatarFacts insert(AvatarFacts m) {
		String sql = "INSERT INTO TBL_AVATAR_PROFILE_FACTS(ID,PROFILE_ID,TOPIC,DETAIL)"
				+ "VALUES(SEQ_AVATAR_PROFILE_FACTS.NEXTVAL,?,?,?)";

		KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                new PreparedStatementCreator() {
                    @Override
                    public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                        PreparedStatement ps = connection.prepareStatement(sql,
                                new String[] { "ID" });
                        int i = 1;
						ps.setInt(i++, m.getProfileId());
                        ps.setString(i++, m.getTopic());
                        ps.setString(i++, m.getDetail());
                        return ps;
                    }
                }, keyHolder);
        m.setId(keyHolder.getKey().intValue());
        return m;
	}

    @Override
    public int deleteById(int id) {
        String sql = "DELETE FROM TBL_AVATAR_PROFILE_FACTS WHERE PROFILE_ID=?";
		return jdbcTemplate.update(sql, id);
    }

    public int update(int id, List<AvatarFacts> list) {
        deleteById(id);
        int result = 0;
        for (AvatarFacts avatarFacts : list) {
            avatarFacts.setProfileId(id);
            insert(avatarFacts);
            result++;
        }
        return result;
    }
	
}
