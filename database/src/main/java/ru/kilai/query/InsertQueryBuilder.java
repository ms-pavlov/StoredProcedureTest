package ru.kilai.query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kilai.parameters.QueryParameters;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class InsertQueryBuilder extends AbstractQueryBuilder {
    private static final Logger log = LoggerFactory.getLogger(InsertQueryBuilder.class);
    private static final String QUERY_TEMPLATE = "INSERT INTO %s (%s) VALUES (%s)";

    private final QueryParameters parameters;
    private final String sqlObjectName;

    private InsertQueryBuilder(Connection connection, String sqlObjectName, QueryParameters parameters) {
        super(connection);
        this.parameters = parameters;
        this.sqlObjectName = sqlObjectName;
    }

    public static InsertQueryBuilder builder(Connection connection, String sqlObjectName, QueryParameters parameters) {
        return new InsertQueryBuilder(connection, sqlObjectName, parameters);
    }

    private String getQuerySql() {
        return String.format(QUERY_TEMPLATE, sqlObjectName, parameters.getFieldsNames(), parameters.getParametersMask());
    }

    @Override
    public PreparedStatement build() {
        try {
            var statement = prepareStatement(getQuerySql());
            parameters.setQueryParameters(statement);
            return statement;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
