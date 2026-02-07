package com.learn.mapper;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import javax.sql.DataSource;
import org.springframework.context.annotation.Primary;

import liquibase.integration.spring.SpringLiquibase;

/**
 * Test configuration for MapperTest.
 * Provides a Jdbi bean wired to the test DataSource.
 */
@TestConfiguration
@ComponentScan(basePackages = {"com.learn"})
public class MapperTestConfig {

    // Keep-alive connection for in-memory SQLite
    private static java.sql.Connection keepAliveConnection;

    @Bean
    @Primary
    public DataSource dataSource() throws java.sql.SQLException {
        org.sqlite.SQLiteDataSource ds = new org.sqlite.SQLiteDataSource();
        ds.setUrl("jdbc:sqlite:file:testdb?mode=memory&cache=shared");
        // Keep one connection open for the JVM lifetime
        if (keepAliveConnection == null || keepAliveConnection.isClosed()) {
            keepAliveConnection = ds.getConnection();
            System.out.println("[INFO] SQLite keep-alive connection opened: " + keepAliveConnection.getMetaData().getURL());
        }
        return ds;
    }

    @Bean
    public SpringLiquibase liquibase(DataSource dataSource) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog("classpath:db/changelog/db.changelog-master.xml");
        liquibase.setShouldRun(true);
        return liquibase;
    }

    @Bean
    public Jdbi jdbi(DataSource dataSource) {
        Jdbi jdbi = Jdbi.create(dataSource);
        jdbi.installPlugin(new SqlObjectPlugin());
        return jdbi;
    }
}
