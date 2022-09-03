package ru.kilai.query;

import ru.kilai.parameters.QueryParameters;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface QueryBuilder {

    Connection getConnection();

    String getSqlObjectName();

    PreparedStatement build(QueryParameters parameters) throws SQLException;
}
