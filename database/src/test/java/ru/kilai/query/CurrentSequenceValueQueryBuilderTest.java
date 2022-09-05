package ru.kilai.query;

import org.junit.jupiter.api.Test;
import ru.kilai.utility.PostgresConnectionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;

class CurrentSequenceValueQueryBuilderTest {

    @Test
    void build() throws SQLException {
        var connection = mock(Connection.class);
        var statement = prepStatement(connection);
        verify(connection, times(1)).prepareStatement("SELECT CURRVAL('formats_id_seq')");

    }

    @Test
    void buildIntegration() throws SQLException {
        var connection = PostgresConnectionPool.getConnection();
        var statement = prepStatement(connection);
        assertFalse(statement.isClosed());
    }

    private PreparedStatement prepStatement(Connection connection) {
        return SequenceCurrentValueQueryBuilder
                .builder(connection, "formats_id_seq")
                .build();
    }

}