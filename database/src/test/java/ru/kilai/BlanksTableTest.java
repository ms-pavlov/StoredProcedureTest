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
import static org.junit.jupiter.api.Assertions.assertFalse;

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

                            statement = CurrentSequenceValueQueryBuilder
                                    .builder(connection, "formats_id_seq")
                                    .build();
                            var result = executeAndGetFirstResultSet(statement);
                            long seq_value = Long.parseLong(result.getObject(1).toString());

                            InsertQueryBuilder.builder(connection, "blanks")
                                    .column("blank_name", BLANK_NAME)
                                    .column("blank_two_side", false)
                                    .column("blank_format_id", seq_value)
                                    .column("blank_surplus", 0)
                                    .build();
                            assertEquals(1, statement.executeUpdate());

                            statement = SelectQueryBuilder.builder(connection, "blanks").build();
                            result = executeAndGetFirstResultSet(statement);
                            assertEquals(BLANK_NAME, result.getObject("blank_name"));
                            assertFalse(result.getBoolean("blank_two_side"));
                            assertEquals(seq_value, result.getLong("blank_format_id"));
                            assertEquals(0, result.getLong("blank_surplus"));

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
