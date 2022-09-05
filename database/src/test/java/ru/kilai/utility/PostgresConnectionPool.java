package ru.kilai.utility;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class PostgresConnectionPool {
    private static final String POSTGRES_IMAGE = "postgres:13";

    @Container
    private static final PostgreSQLContainer<?> postgresSQLContainer = new PostgreSQLContainer<>(POSTGRES_IMAGE);
    private static final DataSource dataSourcePool = new HikariDataSource(getDefaultConfig());
    public static PostgreSQLContainer<?> getPostgresSQLContainer() {
        if (!postgresSQLContainer.isRunning()) {
            postgresSQLContainer.withReuse(true);
            postgresSQLContainer.start();
        }
        return postgresSQLContainer;
    }

    public static Connection getConnection() {
        try {
            return dataSourcePool.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static HikariConfig getDefaultConfig() {
        var config = new HikariConfig();
        config.setJdbcUrl(getPostgresSQLContainer().getJdbcUrl());
        config.setUsername(getPostgresSQLContainer().getUsername());
        config.setPassword(getPostgresSQLContainer().getPassword());

        config.setConnectionTimeout(3000); //ms
        config.setIdleTimeout(60000); //ms
        config.setMaxLifetime(600000);//ms
        config.setAutoCommit(false);
        config.setMinimumIdle(5);
        config.setMaximumPoolSize(10);
        config.setPoolName("DemoHiPool");
        config.setRegisterMbeans(true);

        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        return config;
    }


}
