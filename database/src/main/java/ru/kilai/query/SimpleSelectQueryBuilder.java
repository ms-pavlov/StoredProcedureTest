package ru.kilai.query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kilai.parameters.QueryParameters;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class SimpleSelectQueryBuilder extends AbstractQueryBuilder {
    private static final Logger log = LoggerFactory.getLogger(SimpleSelectQueryBuilder.class);

    private static final String QUERY_TEMPLATE = "SELECT %s FROM %s";

    private final QueryParameters parameters;
    private final String sqlObjectName;
    public SimpleSelectQueryBuilder(Connection connection, String sqlObjectName, QueryParameters parameters) {
        super(connection);
        this.parameters = parameters;
        this.sqlObjectName = sqlObjectName;
    }

    public static SimpleSelectQueryBuilder builder(Connection connection, String sqlObjectName, QueryParameters parameters) {
        return new SimpleSelectQueryBuilder(connection, sqlObjectName, parameters);
    }

    private String getQuerySql() {
        return String.format(QUERY_TEMPLATE, parameters.getFieldsNames(), sqlObjectName);
    }

    @Override
    public PreparedStatement build() {
        return prepareStatement(getQuerySql());
    }
}
