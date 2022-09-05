package ru.kilai.query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kilai.query.parameters.QueryParameters;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class InsertQueryBuilder extends AbstractQueryBuilder {
    private static final Logger log = LoggerFactory.getLogger(InsertQueryBuilder.class);
    private static final String QUERY_TEMPLATE = "INSERT INTO %s (%s) VALUES (%s)";

    private final QueryParameters parameters;

    private InsertQueryBuilder(Connection connection, QueryParameters parameters) {
        super(connection);
        this.parameters = parameters;
    }

    public static InsertQueryBuilder builder(Connection connection, QueryParameters parameters) {
        return new InsertQueryBuilder(connection, parameters);
    }

    private String getQuerySql() {
        var sql = String.format(QUERY_TEMPLATE, parameters.getSqlObject(), parameters.getFieldsNames(), parameters.getParametersMask());
        log.debug(sql);
        return String.format(sql);
    }

    @Override
    public PreparedStatement buildQuery() {
        try {
            var statement = prepareStatement(getQuerySql());
            parameters.setQueryParameters(statement);
            return statement;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
