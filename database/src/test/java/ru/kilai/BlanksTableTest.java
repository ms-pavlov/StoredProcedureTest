package ru.kilai;

import liquibase.exception.LiquibaseException;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BlanksTableTest {
    private static final Logger log = LoggerFactory.getLogger(BlanksTableTest.class);

    private static final String FORMAT_NAME = "A5";
    private static final String BLANK_NAME = "Опросный лист";

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
                                    .column("formats_name", FORMAT_NAME)
                                    .build();
                            assertEquals(1, statement.executeUpdate());

                            statement = SelectQueryBuilder.builder(connection, "formats")
                                    .column("formats_id", null)
                                    .column("formats_name", null)
                                    .build();
                            var result = executeAndGetFirstResultSet(statement);
                            assertEquals(FORMAT_NAME, result.getObject("formats_name"));
                            assertNotNull(result.getObject("formats_id"));
                            var seq_value = result.getObject("formats_id");

                            statement = InsertQueryBuilder.builder(connection, "blanks")
                                    .column("blank_name", BLANK_NAME)
                                    .column("blank_two_side", true)
                                    .column("blank_format_id", seq_value)
                                    .build();
                            assertEquals(1, statement.executeUpdate());

                            statement = SelectQueryBuilder.builder(connection, "blanks")
                                    .column("blank_id", null)
                                    .column("blank_name", null)
                                    .column("blank_two_side", null)
                                    .column("blank_format_id", null)
                                    .column("blank_surplus", null)
                                    .build();
                            result = executeAndGetFirstResultSet(statement);
                            assertNotNull(result.getObject("blank_id"));
                            assertEquals(BLANK_NAME, result.getObject("blank_name"));
                            assertEquals(true, result.getObject("blank_two_side"));
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
