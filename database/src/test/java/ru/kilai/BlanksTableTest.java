package ru.kilai;

import liquibase.exception.LiquibaseException;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kilai.parameters.SimpleQueryParameters;
import ru.kilai.query.InsertQueryBuilder;
import ru.kilai.query.SelectQueryBuilder;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
            new SimpleLiquibaseScopeCommand(
                    "classpath:/db/migration/master.xml",
                    connection,
                    liquibase -> {
                        log.info("insert test");
                        try {
                            liquibase.update("test");

                            var statement = InsertQueryBuilder.builder(connection, "formats")
                                    .build(new SimpleQueryParameters(FORMAT_INSERT_PARAMS));
                            assertEquals(1, statement.executeUpdate());

                            statement = SelectQueryBuilder.builder(connection, "formats")
                                    .build(new SimpleQueryParameters(FORMAT_SELECT_PARAMS));
                            var result = executeAndGetFirstResultSet(statement);
                            assertEquals(FORMAT_INSERT_PARAMS.get("formats_name"), result.getObject("formats_name"));
                            assertNotNull(result.getObject("formats_id"));
                            var seq_value = result.getObject("formats_id");

                            var params = new SimpleQueryParameters(BLANK_INSERT_PARAMS);
                            params.put("blank_format_id", seq_value);
                            statement = InsertQueryBuilder.builder(connection, "blanks")
                                    .build(params);
                            assertEquals(1, statement.executeUpdate());

                            statement = SelectQueryBuilder.builder(connection, "blanks")
                                    .build(new SimpleQueryParameters(BLANK_SELECT_PARAMS));
                            result = executeAndGetFirstResultSet(statement);
                            assertNotNull(result.getObject("blank_id"));
                            assertEquals(BLANK_INSERT_PARAMS.get("blank_name"), result.getObject("blank_name"));
                            assertEquals(BLANK_INSERT_PARAMS.get("blank_two_side"), result.getObject("blank_two_side"));
                            assertEquals(seq_value, result.getObject("blank_format_id"));
                            assertNotNull(result.getObject("blank_surplus"));

                            connection.rollback();
                        } catch (LiquibaseException | SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }).accept();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    private ResultSet executeAndGetFirstResultSet(PreparedStatement statement) throws SQLException {
        statement.execute();
        var result = statement.getResultSet();
        result.next();
        return result;
    }
}
