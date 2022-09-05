package ru.kilai.query;

import org.junit.jupiter.api.Test;
import ru.kilai.parameters.QueryParameters;
import ru.kilai.parameters.SimpleQueryParameters;
import ru.kilai.utility.PostgresConnectionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;

class InsertQueryBuilderTest {
    private static final QueryParameters FORMAT_INSERT_PARAMS = new SimpleQueryParameters(Map.of("formats_name", "A5"), "formats");

    private static final String SQL = String.format("INSERT INTO %s (%s) VALUES (%s)",
            FORMAT_INSERT_PARAMS.getSqlObject(),
            FORMAT_INSERT_PARAMS.getFieldsNames(),
            FORMAT_INSERT_PARAMS.getParametersMask());

    @Test
    void build() throws SQLException {
        var connection = mock(Connection.class);
        when(connection.prepareStatement(SQL))
                .thenReturn(mock(PreparedStatement.class));

        getPreparedStatement(connection);

        verify(connection, times(1)).prepareStatement(SQL);
    }

    @Test
    void buildIntegration() throws SQLException {
        var statement = getPreparedStatement(PostgresConnectionPool.getConnection());
        assertFalse(statement.isClosed());
    }

    private PreparedStatement getPreparedStatement(Connection connection) {
        return InsertQueryBuilder.builder(connection, FORMAT_INSERT_PARAMS)
                .build();
    }
}