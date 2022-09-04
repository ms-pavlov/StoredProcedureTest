package ru.kilai.query;

import org.junit.jupiter.api.Test;
import ru.kilai.parameters.SimpleQueryParameters;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InsertQueryBuilderTest {
    private static final Map<String, Object> FORMAT_INSERT_PARAMS = Map.of("formats_name", "A5");

    @Test
    void build() throws SQLException {
        var connection = mock(Connection.class);
        var sql = String.format("INSERT INTO %s (%s) VALUES (%s)",
                "formats",
                new SimpleQueryParameters(FORMAT_INSERT_PARAMS).getFieldsNames(),
                new SimpleQueryParameters(FORMAT_INSERT_PARAMS).getParametersMask());
        when(connection.prepareStatement(sql))
                .thenReturn(mock(PreparedStatement.class));
        var statement = InsertQueryBuilder.builder(connection, "formats")
                .build(new SimpleQueryParameters(FORMAT_INSERT_PARAMS));

        verify(connection, times(1)).prepareStatement(sql);
    }
}