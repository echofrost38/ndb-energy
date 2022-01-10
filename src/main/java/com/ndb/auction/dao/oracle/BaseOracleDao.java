package com.ndb.auction.dao.oracle;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.NoArgsConstructor;

@Repository
@NoArgsConstructor
public abstract class BaseOracleDao {

	protected JdbcTemplate jdbcTemplate;

	@Autowired
	public void setJdbcTemplate(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
	}

	protected String tableName;

	protected BaseOracleDao(String tableName) {
		this.tableName = tableName;
	}

	public int countAll() {
		String sql = "SELECT COUNT(*) FROM " + tableName;
		return jdbcTemplate.queryForObject(sql, Integer.class);
	}

	public int deleteById(int id) {
		String sql = "DELETE FROM " + tableName + " WHERE ID=?";
		return jdbcTemplate.queryForObject(sql, Integer.class, id);
	}

}
