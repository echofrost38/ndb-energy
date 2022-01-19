package com.ndb.auction.dao.oracle.other;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.ndb.auction.dao.oracle.BaseOracleDao;
import com.ndb.auction.dao.oracle.Table;
import com.ndb.auction.models.Notification;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import lombok.NoArgsConstructor;

@Repository
@NoArgsConstructor
@Table(name = "TBL_NOTIFICATION")
public class NotificationDao extends BaseOracleDao {

	private static Notification extract(ResultSet rs) throws SQLException {
		Notification m = new Notification();
		m.setId(rs.getInt("ID"));
		m.setUserId(rs.getInt("USER_ID"));
		m.setTimeStamp(rs.getTimestamp("TIMESTAMP").getTime());
		m.setNType(rs.getInt("N_TYPE"));
		m.setRead(rs.getBoolean("READ"));
		m.setTitle(rs.getString("TITLE"));
		m.setMsg(rs.getString("MSG"));
		return m;
	}

	public List<Notification> getNotificationsByUser(int userId) {
		String sql = "SELECT * FROM TBL_NOTIFICATION WHERE USER_ID=?";
		return jdbcTemplate.query(sql, new RowMapper<Notification>() {
			@Override
			public Notification mapRow(ResultSet rs, int rownumber) throws SQLException {
				return extract(rs);
			}
		}, userId);
	}

	public void pushNewNotifications(List<Notification> list) {
		for (Notification m : list) {
			addNewNotification(m);
		}
	}

	public Notification addNewNotification(Notification m) {
		String sql = "INSERT INTO TBL_NOTIFICATION(ID, USER_ID, TIMESTAMP, N_TYPE, READ, TITLE, MSG)"
				+ "VALUES(SEQ_NOTIFICATION.NEXTVAL, ?, SYSDATE, ?, ?, ?, ?)";
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(
				new PreparedStatementCreator() {
					@Override
					public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
						PreparedStatement ps = connection.prepareStatement(sql,
								new String[] { "ID" });
						int i = 1;
						ps.setInt(i++, m.getUserId());
						ps.setInt(i++, m.getNType());
						ps.setBoolean(i++, m.isRead());
						ps.setString(i++, m.getTitle());
						ps.setString(i++, m.getMsg());
						return ps;
					}
				}, keyHolder);
		m.setId(keyHolder.getKey().intValue());
		return m;
	}

	public Notification setReadFlag(Notification m) {
		m.setRead(true);
		String sql = "UPDATE TBL_NOTIFICATION SET READ=? WHERE ID=?";
		jdbcTemplate.update(sql, m.isRead(), m.getId());
		return m;
	}

	public Notification setReadFlag(int id, int userId) {
		String sql = "UPDATE TBL_NOTIFICATION SET READ=1 WHERE ID=? AND USER_ID=?";
		if (jdbcTemplate.update(sql, id, userId) > 0)
		return getNotification(id);
		return null;
	}

	public String setReadFlagAll(int userId) {
		String sql = "UPDATE TBL_NOTIFICATION SET READ=1 WHERE USER_ID=? ORDER BY ID DESC";
		jdbcTemplate.update(sql, userId);
		return "Success";
	}

	public Notification getNotification(int id) {
		String sql = "SELECT * FROM TBL_NOTIFICATION WHERE ID=?";
		return jdbcTemplate.query(sql, new ResultSetExtractor<Notification>() {
			@Override
			public Notification extractData(ResultSet rs) throws SQLException {
				if (!rs.next())
					return null;
				return extract(rs);
			}
		}, id);
	}

	public List<Notification> getPaginatedNotifications(int userId, Integer offset, Integer limit) {
		String sql = "SELECT * FROM TBL_NOTIFICATION WHERE USER_ID=? ORDER BY ID DESC";
		if (offset != null)
			sql += " OFFSET ? ROWS";
		if (limit != null)
			sql += " FETCH NEXT ? ROWS ONLY";
		return jdbcTemplate.query(sql, new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				int i = 1;
				ps.setInt(i++, userId);
				if (offset != null)
					ps.setInt(i++, offset);
				if (limit != null)
					ps.setInt(i++, limit);
			}
		}, new RowMapper<Notification>() {
			@Override
			public Notification mapRow(ResultSet rs, int rownumber) throws SQLException {
				return extract(rs);
			}
		});
	}

	public List<Notification> getUnreadNotifications(int userId) {
		String sql = "SELECT * FROM TBL_NOTIFICATION WHERE USER_ID=? AND READ=0 ORDER BY ID DESC";
		return jdbcTemplate.query(sql, new RowMapper<Notification>() {
			@Override
			public Notification mapRow(ResultSet rs, int rownumber) throws SQLException {
				return extract(rs);
			}
		}, userId);
	}
}
