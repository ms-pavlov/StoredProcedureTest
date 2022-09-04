package ru.kilai.query;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SimpleJDBCQueryExecutor implements JDBCQueryExecutor {
    private final PreparedStatement statement;

    private SimpleJDBCQueryExecutor(PreparedStatement statement) {
        this.statement = statement;
    }

    public static SimpleJDBCQueryExecutor executor(PreparedStatement statement) {
        return new SimpleJDBCQueryExecutor(statement);
    }

    @Override
    public int executeUpdate() {
        try {
            return statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResultSet executeAndGetResult() {
        try {
            statement.execute();
            return statement.getResultSet();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
