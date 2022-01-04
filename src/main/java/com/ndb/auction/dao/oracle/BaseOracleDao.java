package com.ndb.auction.dao.oracle;

import org.springframework.stereotype.Repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig.SaveBehavior;

@Repository
public class BaseOracleDao {
	
	private static DriverManagerDataSource dataSource = null;
	private static JdbcTemplate jdbcTemplate = null;

	private static final DriverManagerDataSource getDataSource() {
		if (dataSource == null) {
			try {
				Properties jdbcProperties = PropertiesLoaderUtils.loadProperties(new ClassPathResource(Config.JDBC_PROPERTIES));
				dataSource = new DriverManagerDataSource();
				dataSource.setDriverClassName(jdbcProperties.getProperty("driverClassName"));
				dataSource.setUrl(jdbcProperties.getProperty("url"));
				dataSource.setUsername(jdbcProperties.getProperty("username"));
				dataSource.setPassword(jdbcProperties.getProperty("password"));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return dataSource;
	}

	public static final JdbcTemplate getJdbcTemplate() {
		if (jdbcTemplate == null) {
			jdbcTemplate = new JdbcTemplate(getDataSource());
			LogService.printConsole(LogService.getTimeString() + "<jdbcTemplate has created>", LogService.LEVEL_DEBUG);
		}
		return jdbcTemplate;
	}

	public static final SimpleJdbcCall getSimpleJdbcCall() {
		return new SimpleJdbcCall(getDataSource());
	}

}
