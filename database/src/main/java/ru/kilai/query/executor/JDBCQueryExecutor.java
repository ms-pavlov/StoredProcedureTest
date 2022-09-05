package ru.kilai.query.executor;

import java.sql.ResultSet;

public interface JDBCQueryExecutor {

    int executeUpdate();

    ResultSet executeAndGetResult();
}
