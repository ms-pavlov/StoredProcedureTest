package ru.kilai.query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kilai.query.parameters.QueryParameters;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class SimpleSelectQueryBuilder extends AbstractQueryBuilder {
    private static final Logger log = LoggerFactory.getLogger(SimpleSelectQueryBuilder.class);

    private static final String QUERY_TEMPLATE = "SELECT %s FROM %s";

    private final QueryParameters parameters;

    public SimpleSelectQueryBuilder(Connection connection, QueryParameters parameters) {
        super(connection);
        this.parameters = parameters;
    }

    public static SimpleSelectQueryBuilder builder(Connection connection, QueryParameters parameters) {
        return new SimpleSelectQueryBuilder(connection, parameters);
    }

    private String getQuerySql() {
        return String.format(QUERY_TEMPLATE, parameters.getFieldsNames(), parameters.getSqlObject());
    }

    @Override
    public PreparedStatement buildQuery() {
        return prepareStatement(getQuerySql());
    }
}
