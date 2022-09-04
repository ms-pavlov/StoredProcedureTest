package ru.kilai.utility.tests;

import org.postgresql.util.PSQLException;
import ru.kilai.parameters.SimpleQueryParameters;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class InsertAndSelectLiquibaseTestBuilder implements LiquibaseTestBuilder{
    private final Connection connection;
    private String sqlObjectName;
    private List<String> selectParameters;
    private Map<String, Object> insertParameters;
    private Consumer<ResultSet> test;

    private InsertAndSelectLiquibaseTestBuilder(Connection connection) {
        this.connection = connection;
    }

    public static InsertAndSelectLiquibaseTestBuilder builder(Connection connection) {
        return new InsertAndSelectLiquibaseTestBuilder(connection);
    }

    public InsertAndSelectLiquibaseTestBuilder selectParameters(List<String> selectParameters) {
        this.selectParameters = selectParameters;
        return this;
    }

    public InsertAndSelectLiquibaseTestBuilder insertParameters(Map<String, Object> insertParameters) {
        this.insertParameters = insertParameters;
        return this;
    }

    public InsertAndSelectLiquibaseTestBuilder sqlObjectName(String sqlObjectName) {
        this.sqlObjectName = sqlObjectName;
        return this;
    }

    public InsertAndSelectLiquibaseTestBuilder test(Consumer<ResultSet> test) {
        this.test = test;
        return this;
    }

    @Override
    public LiquibaseTest build() {
        return () -> {
            InsertLiquibaseTestBuilder.builder(connection)
                    .sqlObjectName(sqlObjectName)
                    .parameters(new SimpleQueryParameters(insertParameters))
                    .build()
                    .makeTest();
            SelectLiquibaseTestBuilder.builder(connection)
                    .sqlObjectName(sqlObjectName)
                    .parameters(new SimpleQueryParameters(selectParameters))
                    .expectParameters(insertParameters)
                    .test(resultSet -> {
                        try {
                            test.accept(resultSet);
                            resultSet.next();
                            assertThrows(PSQLException.class, () -> resultSet.getObject(1));
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .build()
                    .makeTest();
        };
    }
}
