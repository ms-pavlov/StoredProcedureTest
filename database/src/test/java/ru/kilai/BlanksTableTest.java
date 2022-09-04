package ru.kilai;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kilai.parameters.SimpleQueryParameters;
import ru.kilai.query.InsertQueryBuilder;
import ru.kilai.query.SimpleSelectQueryBuilder;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static ru.kilai.query.SimpleJDBCQueryExecutor.executor;

public class BlanksTableTest {
    private static final Logger log = LoggerFactory.getLogger(BlanksTableTest.class);

    private static final Map<String, Object> FORMAT_INSERT_PARAMS = Map.of("formats_name", "A5");
    private static final List<String> FORMAT_SELECT_PARAMS = List.of("formats_name", "formats_id");
    private static final Map<String, Object> BLANK_INSERT_PARAMS = Map.of("blank_name", "Опросный лист",
            "blank_two_side", true);
    private static final List<String> BLANK_SELECT_PARAMS = List.of("blank_id", "blank_name", "blank_two_side",
            "blank_format_id", "blank_surplus");


    @Test
    void changeLogFormats() {
        try (var connection = PostgresConnectionPool.getConnection()) {
            LiquibaseTest.builder("classpath:/db/migration/master.xml", connection)
                    .makeTestAndRollback(liquibase -> {
                        long id_value;

                        try (var statement = InsertQueryBuilder.builder(connection, "format")
                                .build(new SimpleQueryParameters(FORMAT_INSERT_PARAMS))) {
                            assertEquals(1, executor(statement).executeUpdate());
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }

                        try (var result = executor(SimpleSelectQueryBuilder.builder(connection, "format")
                                        .build(new SimpleQueryParameters(FORMAT_SELECT_PARAMS))).executeAndGetResult()) {
                            result.next();
                            assertEquals(FORMAT_INSERT_PARAMS.get("formats_name"), result.getObject("formats_name"));
                            assertNotNull(result.getObject("formats_id"));
                            id_value = (long) result.getObject("formats_id");
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }

                        var params = new SimpleQueryParameters(BLANK_INSERT_PARAMS);
                        params.put("blank_format_id", id_value);
                        try (var statement = InsertQueryBuilder.builder(connection, "blank")
                                .build(params)) {
                            assertEquals(1, statement.executeUpdate());
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }

                        try (var result = executor(SimpleSelectQueryBuilder.builder(connection, "blank")
                                .build(new SimpleQueryParameters(BLANK_SELECT_PARAMS))).executeAndGetResult()) {
                            result.next();
                            assertNotNull(result.getObject("blank_id"));
                            assertEquals(BLANK_INSERT_PARAMS.get("blank_name"), result.getObject("blank_name"));
                            assertEquals(BLANK_INSERT_PARAMS.get("blank_two_side"), result.getObject("blank_two_side"));
                            assertEquals(id_value, result.getObject("blank_format_id"));
                            assertNotNull(result.getObject("blank_surplus"));
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    });
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
