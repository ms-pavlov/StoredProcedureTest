package ru.kilai;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CurrentSequenceValueQueryBuilder {
    private static final String QUERY_TEMPLATE = "SELECT CURRVAL('%s')";

    private final Connection connection;
    private final String sequenceName;

    private CurrentSequenceValueQueryBuilder(Connection connection, String sequenceName) {
        this.connection = connection;
        this.sequenceName = sequenceName;
    }

    public static CurrentSequenceValueQueryBuilder builder(Connection connection, String sequenceName) {
        return new CurrentSequenceValueQueryBuilder(connection, sequenceName);
    }

    public PreparedStatement build() throws SQLException {
        return connection.prepareStatement(String.format(QUERY_TEMPLATE, sequenceName));
    }
}
