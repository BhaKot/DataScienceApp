package com.learn.mapper;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.argument.AbstractArgumentFactory;
import org.jdbi.v3.core.argument.Argument;
import org.jdbi.v3.core.argument.ObjectArgument;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import javax.sql.DataSource;
import org.springframework.context.annotation.Primary;

import liquibase.integration.spring.SpringLiquibase;

import java.sql.Types;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Test configuration for MapperTest.
 * Provides a Jdbi bean wired to the test DataSource.
 */
@TestConfiguration
@ComponentScan(basePackages = {"com.learn"})
public class MapperTestConfig {

    private static final DateTimeFormatter SQLITE_TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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

        // SQLite TEXT timestamps can be written by Liquibase as "yyyy-MM-dd HH:mm:ss"
        // while application code may use ISO_LOCAL_DATE_TIME. Support both for mapper tests.
        jdbi.registerColumnMapper(LocalDateTime.class, (rs, columnNumber, ctx) -> parseLocalDateTime(rs.getString(columnNumber)));
        jdbi.registerArgument(new AbstractArgumentFactory<LocalDateTime>(Types.VARCHAR) {
            @Override
            protected Argument build(LocalDateTime value, org.jdbi.v3.core.config.ConfigRegistry config) {
                return new ObjectArgument(value.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), Types.VARCHAR);
            }
        });

        return jdbi;
    }

    private static LocalDateTime parseLocalDateTime(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return LocalDateTime.parse(value, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (DateTimeParseException ignored) {
            return LocalDateTime.parse(value, SQLITE_TIMESTAMP_FORMATTER);
        }
    }
}
