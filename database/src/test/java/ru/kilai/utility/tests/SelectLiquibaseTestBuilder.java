package ru.kilai.utility.tests;

import ru.kilai.parameters.QueryParameters;
import ru.kilai.query.SimpleSelectQueryBuilder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.kilai.query.SimpleJDBCQueryExecutor.executor;

public class SelectLiquibaseTestBuilder implements LiquibaseTestBuilder{
    private final Connection connection;
    private String sqlObjectName;
    private QueryParameters parameters;

    private Map<String, Object> expectParameters;
    private Consumer<ResultSet> test;

    private SelectLiquibaseTestBuilder(Connection connection) {
        this.connection = connection;
    }

    public static SelectLiquibaseTestBuilder builder(Connection connection) {
        return new SelectLiquibaseTestBuilder(connection);
    }

    public SelectLiquibaseTestBuilder parameters(QueryParameters parameters) {
        this.parameters = parameters;
        return this;
    }

    public SelectLiquibaseTestBuilder sqlObjectName(String sqlObjectName) {
        this.sqlObjectName = sqlObjectName;
        return this;
    }

    public SelectLiquibaseTestBuilder expectParameters(Map<String, Object> expectParameters) {
        this.expectParameters = expectParameters;
        return this;
    }

    public SelectLiquibaseTestBuilder test(Consumer<ResultSet> test) {
        this.test = test;
        return this;
    }

    @Override
    public LiquibaseTest build() {
        var statement = SimpleSelectQueryBuilder.builder(connection, sqlObjectName, parameters).build();
        return new SimpleLiquibaseTest(statement, this::makeSelectTest);
    }

    private void makeSelectTest(PreparedStatement statement) {
        try(var result = executor(statement).executeAndGetResult()) {
            result.next();
            for (String key : expectParameters.keySet()) {
                assertEquals(expectParameters.get(key), result.getObject(key));
            }

            test.accept(result);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
