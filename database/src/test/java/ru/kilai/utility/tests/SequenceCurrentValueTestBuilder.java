package ru.kilai.utility.tests;

import ru.kilai.query.SequenceCurrentValueQueryBuilder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.kilai.query.SimpleJDBCQueryExecutor.executor;

public class SequenceCurrentValueTestBuilder implements LiquibaseTestBuilder {

    private final Connection connection;
    private String sqlObjectName;

    private SequenceCurrentValueTestBuilder(Connection connection) {
        this.connection = connection;
    }

    public static SequenceCurrentValueTestBuilder builder(Connection connection) {
        return new SequenceCurrentValueTestBuilder(connection);
    }

    public SequenceCurrentValueTestBuilder sqlObjectName(String sqlObjectName) {
        this.sqlObjectName = sqlObjectName;
        return this;
    }

    @Override
    public LiquibaseTest build() {
        try {
            var statement = SequenceCurrentValueQueryBuilder.builder(connection, sqlObjectName).build(null);
            return new SimpleLiquibaseTest(statement, this::makeSequenceTest);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void makeSequenceTest(PreparedStatement statement) {
        try(var result = executor(statement).executeAndGetResult()) {
            result.next();
            assertEquals(1, result.getMetaData().getColumnCount());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
