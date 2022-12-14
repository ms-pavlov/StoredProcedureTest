package ru.kilai.query.parameters;

import org.junit.jupiter.api.Test;
import ru.kilai.query.parameters.SimpleQueryParameters;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SimpleQueryParametersTest {

    private static final Map<String, Object> TEST_PARAMETERS = Map.of("field", "value");

    @Test
    void getFieldsNames() {
        var parameters = new SimpleQueryParameters(TEST_PARAMETERS, "test");
        assertEquals("field", parameters.getFieldsNames());

        parameters = new SimpleQueryParameters(List.of("field", "field2"), "test");
        assertEquals("field, field2", parameters.getFieldsNames());

    }

    @Test
    void getParametersMask() {
        var parameters = new SimpleQueryParameters(TEST_PARAMETERS, "test");
        assertEquals("?", parameters.getParametersMask());
        parameters = new SimpleQueryParameters(List.of("field", "field2"), "test");
        assertEquals("?, ?", parameters.getParametersMask());
    }

    @Test
    void setQueryParameters() throws SQLException {
        var parameters = new SimpleQueryParameters(TEST_PARAMETERS, "test");

        var statement = mock(PreparedStatement.class);

        parameters.setQueryParameters(statement);

        verify(statement, times(1)).setObject(1, "value");

    }

    @Test
    void getSqlObject() {
        var parameters = new SimpleQueryParameters(TEST_PARAMETERS, "test");
        assertEquals("test", parameters.getSqlObject());

    }
}