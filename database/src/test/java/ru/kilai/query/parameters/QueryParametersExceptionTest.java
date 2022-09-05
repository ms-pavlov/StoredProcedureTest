package ru.kilai.query.parameters;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QueryParametersExceptionTest {

    @Test
    void check() {
        assertDoesNotThrow(QueryParametersException::new);
    }

}