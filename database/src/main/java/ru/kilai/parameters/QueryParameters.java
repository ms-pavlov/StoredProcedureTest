package ru.kilai.parameters;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface QueryParameters {
    void put(String field, Object value);

    void put(String fields);

    String getFieldsNames();

    String getParametersMask();

    void setQueryParameters(PreparedStatement statement) throws SQLException;
}
