package ru.kilai.query;

import org.junit.jupiter.api.Test;
import ru.kilai.parameters.SimpleQueryParameters;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SimpleSelectQueryBuilderTest {
    private static final List<String> FORMAT_SELECT_PARAMS = List.of("formats_name", "formats_id");

    @Test
    void build() throws SQLException {
        var connection = mock(Connection.class);
        var sql = String.format("SELECT %s FROM %s", new SimpleQueryParameters(FORMAT_SELECT_PARAMS).getFieldsNames(), "formats");
        when(connection.prepareStatement(sql))
                .thenReturn(mock(PreparedStatement.class));

        var statement = SimpleSelectQueryBuilder.builder(connection, "formats")
                .build(new SimpleQueryParameters(FORMAT_SELECT_PARAMS));

        verify(connection, times(1)).prepareStatement(sql);
    }
}