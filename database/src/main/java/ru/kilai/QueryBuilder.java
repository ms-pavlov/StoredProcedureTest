package ru.kilai;

import java.sql.Connection;
import java.sql.PreparedStatement;

public interface QueryBuilder {

    QueryBuilder builder(Connection connection);

    QueryBuilder column(String columnName, Object columnValue);

    PreparedStatement build();
}
