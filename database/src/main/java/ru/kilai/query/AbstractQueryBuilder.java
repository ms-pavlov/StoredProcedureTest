package ru.kilai.query;

import java.sql.Connection;

public abstract class AbstractQueryBuilder implements QueryBuilder {

    private final Connection connection;
    private final String sqlObjectName;

    public AbstractQueryBuilder(Connection connection, String sqlObjectName) {
        this.connection = connection;
        this.sqlObjectName = sqlObjectName;
    }

    @Override
    public Connection getConnection() {
        return connection;
    }

    @Override
    public String getSqlObjectName() {
        return sqlObjectName;
    }

}
