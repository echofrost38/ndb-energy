package com.ndb.auction.dao.oracle;

import javax.sql.DataSource;

import com.google.gson.Gson;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public abstract class BaseOracleDao {

	protected static Gson gson = new Gson();

	protected JdbcTemplate jdbcTemplate;

	@Autowired
	public void setJdbcTemplate(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
	}

	protected String tableName;

	protected BaseOracleDao() {
		var table = this.getClass().getAnnotation(Table.class);
		if (table != null)
			tableName = table.name();
	}

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
