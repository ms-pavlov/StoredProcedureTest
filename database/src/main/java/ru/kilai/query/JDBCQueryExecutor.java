package ru.kilai.query;

import java.sql.ResultSet;

public interface JDBCQueryExecutor {

    int executeUpdate();

    ResultSet executeAndGetResult();
}
