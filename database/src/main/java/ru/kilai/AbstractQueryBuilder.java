package ru.kilai;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class AbstractQueryBuilder {
    private final Connection connection;
    private final String tableName;
    private final Map<String, Object> columns;

    public AbstractQueryBuilder(Connection connection, String tableName) {
        this.connection = connection;
        this.tableName = tableName;
        this.columns = new HashMap<>();
    }

    public AbstractQueryBuilder column(String columnName, Object columnValue) {
        columns.put(columnName, columnValue);
        return this;
    }

    public void putColumn(String columnName, Object columnValue) {
        columns.put(columnName, columnValue);
    }

    public Connection getConnection() {
        return connection;
    }

    public String getTableName() {
        return tableName;
    }

    public Map<String, Object> getColumns() {
        return columns;
    }

    public abstract PreparedStatement build() throws SQLException;

    public String getParameterTemplate() {
        return columns.values().stream()
                .map(s -> "?")
                .collect(Collectors.joining(", "));
    }

    public String getColumnsNames() {
        if (columns.isEmpty()) {
            return "*";
        }
        return String.join(", ", columns.keySet());
    }
}
