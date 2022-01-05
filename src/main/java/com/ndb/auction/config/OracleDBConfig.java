package com.ndb.auction.config;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class OracleDBConfig {

    @Value("${oracle.url}")
    private String url;

    @Value("${oracle.username}")
    private String username;

    @Value("${oracle.password}")
    private String password;

    @Bean
    public DataSource dataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("oracle.jdbc.OracleDriver");
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }

    // @Bean
    // public DataSourceInitializer dataSourceInitializer(DataSource dataSource) {
    //     DataSourceInitializer dataSourceInitializer = new DataSourceInitializer();
    //     dataSourceInitializer.setDataSource(dataSource);
    //     ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator();
    //     databasePopulator.addScript(new ClassPathResource("data.sql"));
    //     dataSourceInitializer.setDatabasePopulator(databasePopulator);
    //     dataSourceInitializer.setEnabled(true);
    //     return dataSourceInitializer;
    // }

    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

}