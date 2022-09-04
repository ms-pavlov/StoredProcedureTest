package ru.kilai.utility.tests;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kilai.parameters.QueryParameters;
import ru.kilai.query.QueryBuilder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class SimpleLiquibaseTestBuilder implements LiquibaseTestBuilder {
    private static final Logger log = LoggerFactory.getLogger(SimpleLiquibaseTestBuilder.class);
    private final Connection connection;
    private String sqlObjectName;
    private BiFunction<Connection, String, QueryBuilder> queryBuilderLink;
    private QueryParameters parameters;
    private Consumer<PreparedStatement> test;

    private SimpleLiquibaseTestBuilder(Connection connection) {
        this.connection = connection;
    }

    public static SimpleLiquibaseTestBuilder builder(Connection connection) {
        return new SimpleLiquibaseTestBuilder(connection);
    }

    public SimpleLiquibaseTestBuilder queryBuilderLink(BiFunction<Connection, String, QueryBuilder> queryBuilderLink) {
        this.queryBuilderLink = queryBuilderLink;
        return this;
    }

    public SimpleLiquibaseTestBuilder parameters(QueryParameters parameters) {
        this.parameters = parameters;
        return this;
    }

    public SimpleLiquibaseTestBuilder test(Consumer<PreparedStatement> test) {
        this.test = test;
        return this;
    }
    public SimpleLiquibaseTestBuilder sqlObjectName(String sqlObjectName) {
        this.sqlObjectName = sqlObjectName;
        return this;
    }

    @Override
    public LiquibaseTest build() {
        try {
            var statement = queryBuilderLink.apply(connection, sqlObjectName).build(parameters);
            return new SimpleLiquibaseTest(statement, test);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
