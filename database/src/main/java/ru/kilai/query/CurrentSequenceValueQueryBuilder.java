package ru.kilai.query;

import ru.kilai.parameters.QueryParameters;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CurrentSequenceValueQueryBuilder extends AbstractQueryBuilder {
    private static final String QUERY_TEMPLATE = "SELECT CURRVAL('%s')";

    private CurrentSequenceValueQueryBuilder(Connection connection, String sqlObjectName) {
        super(connection, sqlObjectName);
    }

    public static CurrentSequenceValueQueryBuilder builder(Connection connection, String sequenceName) {
        return new CurrentSequenceValueQueryBuilder(connection, sequenceName);
    }

    @Override
    public PreparedStatement build(QueryParameters parameters) throws SQLException {
        return getConnection().prepareStatement(String.format(QUERY_TEMPLATE, getSqlObjectName()));
    }
}
