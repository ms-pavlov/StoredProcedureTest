package ru.kilai;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class InsertQueryBuilder extends AbstractQueryBuilder {
    private static final String QUERY_TEMPLATE = "INSERT INTO %s (%s) VALUES (%s)";

    private InsertQueryBuilder(Connection connection, String tableName) {
        super(connection, tableName);
    }

    public static InsertQueryBuilder builder(Connection connection, String tableName) {
        return new InsertQueryBuilder(connection, tableName);
    }

    public PreparedStatement build() throws SQLException {
        var statement = getConnection().prepareStatement(String.format(QUERY_TEMPLATE, getTableName(), getColumnsNames(), getParameterTemplate()));
        var i = 1;
        for (var item : getColumns().values()) {
            statement.setObject(i++, item);
        }
        return statement;
    }
}
