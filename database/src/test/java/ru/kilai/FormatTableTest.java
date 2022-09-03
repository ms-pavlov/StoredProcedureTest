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

public class FormatTableTest {
    private static final Logger log = LoggerFactory.getLogger(FormatTableTest.class);

    private static final String FORMAT_NAME = "A5";

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

                            statement = CurrentSequenceValueQueryBuilder
                                    .builder(connection, "formats_id_seq")
                                    .build();
                            var result = executeAndGetFirstResultSet(statement);
                            long seq_value = Long.parseLong(result.getObject(1).toString());

                            statement = SelectQueryBuilder.builder(connection, "formats")
                                    .column("formats_id", null)
                                    .column("formats_name", null)
                                    .build();
                            result = executeAndGetFirstResultSet(statement);
                            assertEquals(FORMAT_NAME, result.getObject("formats_name"));
                            assertEquals(seq_value, result.getObject("formats_id"));

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
