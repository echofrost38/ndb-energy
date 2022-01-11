package com.ndb.auction.dao.oracle.other;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.ndb.auction.dao.oracle.BaseOracleDao;
import com.ndb.auction.dao.oracle.Table;
import com.ndb.auction.models.NotificationType;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import lombok.NoArgsConstructor;

@Repository
@NoArgsConstructor
@Table(name = "TBL_NOTIFICATION_TYPE")
public class NotificationTypeDao extends BaseOracleDao {

	private static NotificationType extract(ResultSet rs) throws SQLException {
		NotificationType m = new NotificationType();
		m.setId(rs.getInt("ID"));
		m.setNType(rs.getInt("N_TYPE"));
		m.setTName(rs.getString("T_NAME"));
		m.setBroadcast(rs.getBoolean("BROADCAST"));
		return m;
	}

	public String create(String name) {
		String sql = "INSERT INTO TBL_NOTIFICATION_TYPE(ID, T_NAME)"
				+ "VALUES((SELECT NVL(MAX(ID)+1,1) FROM TBL_NOTIFICATION_TYPE),?)";
		jdbcTemplate.update(sql, name);
		return "Notification Type created successfully!";
	}

	public NotificationType getNotificationTypeById(String id) {
		String sql = "SELECT * FROM TBL_NOTIFICATION_TYPE WHERE ID=?";
		return jdbcTemplate.query(sql, new ResultSetExtractor<NotificationType>() {
			@Override
			public NotificationType extractData(ResultSet rs) throws SQLException {
				if (!rs.next())
					return null;
				return extract(rs);
			}
		}, id);
	}

	public List<NotificationType> getAllNotificationTypes() {
		String sql = "SELECT * FROM TBL_NOTIFICATION_TYPE ORDER BY ID";
		return jdbcTemplate.query(sql, new RowMapper<NotificationType>() {
			@Override
			public NotificationType mapRow(ResultSet rs, int rownumber) throws SQLException {
				return extract(rs);
			}
		});
	}

	public NotificationType updateNotificationType(NotificationType m) {
		String sql = "UPDATE TBL_NOTIFICATION_TYPE SET N_TYPE=?, T_NAME=?, BROADCAST=? WHERE ID=?";
		jdbcTemplate.update(sql, m.getNType(), m.getTName(), m.isBroadcast(), m.getId());
		return m;
	}

	public NotificationType addNewNotificationType(NotificationType m) {
		String sql = "INSERT INTO TBL_NOTIFICATION_TYPE(ID, N_TYPE, T_NAME, BROADCAST)"
				+ "VALUES((SELECT NVL(MAX(ID)+1,1) FROM TBL_NOTIFICATION_TYPE),?,?,?)";
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(
				new PreparedStatementCreator() {
					@Override
					public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
						PreparedStatement ps = connection.prepareStatement(sql.toString(),
								new String[] { "ID" });
						int i = 1;
						ps.setInt(i++, m.getNType());
						ps.setString(i++, m.getTName());
						ps.setBoolean(i++, m.isBroadcast());
						return ps;
					}
				}, keyHolder);
		m.setId(keyHolder.getKey().intValue());
		return m;
	}

}
