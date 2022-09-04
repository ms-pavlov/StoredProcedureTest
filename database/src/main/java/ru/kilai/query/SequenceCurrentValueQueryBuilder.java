package ru.kilai.query;

import ru.kilai.parameters.QueryParameters;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SequenceCurrentValueQueryBuilder extends AbstractQueryBuilder {
    private static final String QUERY_TEMPLATE = "SELECT CURRVAL('%s')";

    private SequenceCurrentValueQueryBuilder(Connection connection, String sqlObjectName) {
        super(connection, sqlObjectName);
    }

    public static SequenceCurrentValueQueryBuilder builder(Connection connection, String sequenceName) {
        return new SequenceCurrentValueQueryBuilder(connection, sequenceName);
    }

    @Override
    public PreparedStatement build(QueryParameters parameters) {
        try (var statement = getConnection().prepareStatement(String.format(QUERY_TEMPLATE, getSqlObjectName()))){
            return statement;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
