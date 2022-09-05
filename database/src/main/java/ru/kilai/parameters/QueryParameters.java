package ru.kilai.parameters;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

public interface QueryParameters {

    String getSqlObject();

    String getFieldsNames();

    String getParametersMask();

    void setQueryParameters(PreparedStatement statement) throws SQLException;
}
