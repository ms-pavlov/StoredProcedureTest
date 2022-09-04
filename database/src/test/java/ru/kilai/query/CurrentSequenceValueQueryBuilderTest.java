package ru.kilai.query;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.mockito.Mockito.*;

class CurrentSequenceValueQueryBuilderTest {

    @Test
    void build() throws SQLException {
        var connection = mock(Connection.class);
        try (var ignored = SequenceCurrentValueQueryBuilder
                .builder(connection, "formats_id_seq")
                .build(null)) {
            verify(connection, times(1)).prepareStatement("SELECT CURRVAL('formats_id_seq')");
        }
    }
}