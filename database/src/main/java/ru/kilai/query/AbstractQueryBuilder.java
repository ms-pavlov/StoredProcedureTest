package ru.kilai.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class AbstractQueryBuilder implements QueryBuilder{
    private final Connection connection;

    public AbstractQueryBuilder(Connection connection) {
        this.connection = connection;
    }

    @Override
    public PreparedStatement prepareStatement(String sql) {
        try {
            return connection.prepareStatement(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
