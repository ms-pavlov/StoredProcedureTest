package ru.kilai.parameters;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SimpleQueryParameters implements QueryParameters {
    private final List<String> fields;
    private final List<Object> values;
    private final String getSqlObject;

    public SimpleQueryParameters(String getSqlObject) {
        this.getSqlObject = getSqlObject;
        this.fields = new ArrayList<>();
        this.values = new ArrayList<>();
    }

    public SimpleQueryParameters(Map<String, Object> columns, String getSqlObject) {
        this(getSqlObject);
        columns.keySet().forEach(key -> {
            fields.add(key);
            values.add(columns.get(key));
        });
    }

    public SimpleQueryParameters(List<String> columns, String getSqlObject) {
        this(getSqlObject);
        columns.forEach(name -> {
            fields.add(name);
            values.add(null);
        });
    }

    @Override
    public String getSqlObject() {
        return getSqlObject;
    }

    @Override
    public String getFieldsNames() {
        return String.join(", ", fields);
    }

    @Override
    public String getParametersMask() {
        return fields.stream()
                .map(s -> "?")
                .collect(Collectors.joining(", "));
    }

    @Override
    public void setQueryParameters(PreparedStatement statement) throws SQLException {
        for (var idx = 0; idx < fields.size(); idx++) {
            statement.setObject(idx + 1, values.get(idx));
        }
    }
}
