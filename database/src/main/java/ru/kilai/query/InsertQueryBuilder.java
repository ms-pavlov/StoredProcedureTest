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

    private InsertQueryBuilder(Connection connection, String tableName) {
        super(connection, tableName);
    }

    public static InsertQueryBuilder builder(Connection connection, String tableName) {
        return new InsertQueryBuilder(connection, tableName);
    }

    @Override
    public PreparedStatement build(QueryParameters parameters) throws SQLException {
        var sql = String.format(QUERY_TEMPLATE, getSqlObjectName(), parameters.getFieldsNames(), parameters.getParametersMask());
        log.debug(sql);
        var statement = getConnection().prepareStatement(sql);
        parameters.setQueryParameters(statement);
        return statement;
    }
}
