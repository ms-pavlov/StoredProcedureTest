package ru.kilai.utility.tests;

import ru.kilai.query.parameters.QueryParameters;
import ru.kilai.query.InsertQueryBuilder;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.kilai.query.executor.SimpleJDBCQueryExecutor.executor;

public class InsertLiquibaseTestBuilder implements LiquibaseTestBuilder {
    private final Connection connection;
    private QueryParameters parameters;

    private InsertLiquibaseTestBuilder(Connection connection) {
        this.connection = connection;
    }

    public static InsertLiquibaseTestBuilder builder(Connection connection) {
        return new InsertLiquibaseTestBuilder(connection);
    }

    public InsertLiquibaseTestBuilder parameters(QueryParameters parameters) {
        this.parameters = parameters;
        return this;
    }

    public InsertLiquibaseTestBuilder sqlObjectName(String sqlObjectName) {
        return this;
    }

    @Override
    public LiquibaseTest build() {
        var statement = InsertQueryBuilder.builder(connection, parameters).buildQuery();
        return new SimpleLiquibaseTest(statement, preparedStatement -> assertEquals(1, executor(statement).executeUpdate()));
    }
}
