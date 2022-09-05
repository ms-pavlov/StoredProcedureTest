package ru.kilai.query.executor;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SimpleJDBCQueryExecutorTest {

    @Test
    void executeUpdate() throws SQLException {
        var statement = mock(PreparedStatement.class);

        SimpleJDBCQueryExecutor.executor(statement).executeUpdate();

        verify(statement, times(1)).executeUpdate();
    }

    @Test
    void executeAndGetResult() throws SQLException {
        var statement = mock(PreparedStatement.class);

        SimpleJDBCQueryExecutor.executor(statement).executeAndGetResult();

        verify(statement, times(1)).execute();
        verify(statement, times(1)).getResultSet();
    }
}