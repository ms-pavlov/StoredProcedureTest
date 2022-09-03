package ru.kilai;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SelectQueryBuilder extends AbstractQueryBuilder {
    private static final Logger log = LoggerFactory.getLogger(SelectQueryBuilder.class);

    private static final String QUERY_TEMPLATE = "SELECT %s FROM %s";

    public SelectQueryBuilder(Connection connection, String tableName, String queryTemplate) {
        super(connection, tableName);
    }

    public static SelectQueryBuilder builder(Connection connection, String tableName) {
        return new SelectQueryBuilder(connection, tableName, QUERY_TEMPLATE);
    }

    public PreparedStatement build() throws SQLException {
        return getConnection().prepareStatement(String.format(QUERY_TEMPLATE, getColumnsNames(), getTableName()));
    }
}
