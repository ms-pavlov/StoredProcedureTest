package ru.kilai.query;

import org.junit.jupiter.api.Test;
import ru.kilai.parameters.SimpleQueryParameters;
import ru.kilai.utility.PostgresConnectionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;

class SimpleSelectQueryBuilderTest {
    private static final List<String> FORMAT_SELECT_PARAMS = List.of("formats_name", "formats_id");
    private static final  String SQL = String.format("SELECT %s FROM %s", new SimpleQueryParameters(FORMAT_SELECT_PARAMS).getFieldsNames(), "formats");


    @Test
    void build() throws SQLException {
        var connection = mock(Connection.class);
        when(connection.prepareStatement(SQL))
                .thenReturn(mock(PreparedStatement.class));

        var statement = prepStatement(connection);

        verify(connection, times(1)).prepareStatement(SQL);
    }

    @Test
    void buildIntegration() throws SQLException {
        var statement = prepStatement(PostgresConnectionPool.getConnection());
        assertFalse(statement.isClosed());
    }

    private PreparedStatement prepStatement(Connection connection) {
        return SimpleSelectQueryBuilder.builder(connection, "formats", new SimpleQueryParameters(FORMAT_SELECT_PARAMS))
                .build();
    }
}