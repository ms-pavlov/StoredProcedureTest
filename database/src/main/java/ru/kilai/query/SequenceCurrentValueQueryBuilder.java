package ru.kilai.query;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class SequenceCurrentValueQueryBuilder extends AbstractQueryBuilder {
    private static final String QUERY_TEMPLATE = "SELECT CURRVAL('%s')";

    private final String sqlObjectName;

    private SequenceCurrentValueQueryBuilder(Connection connection, String sqlObjectName) {
        super(connection);
        this.sqlObjectName = sqlObjectName;
    }

    public static SequenceCurrentValueQueryBuilder builder(Connection connection, String sequenceName) {
        return new SequenceCurrentValueQueryBuilder(connection, sequenceName);
    }

    private String getQuerySql() {
        return String.format(QUERY_TEMPLATE, sqlObjectName);
    }

    @Override
    public PreparedStatement build() {
        return prepareStatement(getQuerySql());
    }
}
