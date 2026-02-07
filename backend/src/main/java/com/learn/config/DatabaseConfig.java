
package com.learn.config;

import org.jdbi.v3.core.argument.Argument;
import org.jdbi.v3.core.argument.ArgumentFactory;
import org.jdbi.v3.core.config.ConfigRegistry;
import org.sqlite.SQLiteDataSource;
import java.util.Optional;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
@Configuration
public class DatabaseConfig {

    @Bean
    public DataSource dataSource() {
        SQLiteDataSource ds = new SQLiteDataSource();
        ds.setUrl("jdbc:sqlite:data/app.db");
        return ds;
    }

    @Bean
    public Jdbi jdbi(DataSource dataSource) {
        Jdbi jdbi = Jdbi.create(dataSource);
        jdbi.installPlugin(new SqlObjectPlugin());
        // Register LocalDateTime argument factory and column mapper for SQLite compatibility
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        jdbi.registerArgument(new ArgumentFactory() {
            @Override
            public Optional<Argument> build(java.lang.reflect.Type type, Object value, ConfigRegistry config) {
                if (value instanceof LocalDateTime) {
                    return Optional.of((pos, stmt, ctx) -> stmt.setString(pos, ((LocalDateTime) value).format(fmt)));
                }
                return Optional.empty();
            }
        });
        jdbi.registerColumnMapper(LocalDateTime.class, (rs, col, ctx) -> {
            String val = rs.getString(col);
            return val == null ? null : LocalDateTime.parse(val, fmt);
        });
        return jdbi;
    }
}
