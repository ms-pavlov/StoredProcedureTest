package ru.kilai.query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kilai.parameters.QueryParameters;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SimpleSelectQueryBuilder extends AbstractQueryBuilder {
    private static final Logger log = LoggerFactory.getLogger(SimpleSelectQueryBuilder.class);

    private static final String QUERY_TEMPLATE = "SELECT %s FROM %s";

    public SimpleSelectQueryBuilder(Connection connection, String tableName, String queryTemplate) {
        super(connection, tableName);
    }

    public static SimpleSelectQueryBuilder builder(Connection connection, String tableName) {
        return new SimpleSelectQueryBuilder(connection, tableName, QUERY_TEMPLATE);
    }

    @Override
    public PreparedStatement build(QueryParameters parameters) throws SQLException {
        var sql = String.format(QUERY_TEMPLATE, parameters.getFieldsNames(), getSqlObjectName());
        return getConnection().prepareStatement(sql);
    }
}
