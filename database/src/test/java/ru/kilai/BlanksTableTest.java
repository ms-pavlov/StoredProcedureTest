package ru.kilai;

import liquibase.exception.LiquibaseException;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

                            var statement = connection.prepareStatement("INSERT INTO formats (formats_name) VALUES (?)");
                            statement.setString(1, FORMAT_NAME);
                            assertEquals(1, statement.executeUpdate());

                            statement = connection.prepareStatement("SELECT CURRVAL('formats_formats_id_seq')");
                            statement.execute();
                            var result = statement.getResultSet();
                            result.next();
                            long seq_value = Long.parseLong(result.getObject(1).toString());

                            statement = connection.prepareStatement("INSERT INTO blanks (blank_name, blank_two_side, blank_format_id, blank_surplus) VALUES (?, ?, ?, ?)");
                            statement.setString(1, BLANK_NAME);
                            statement.setBoolean(2, false);
                            statement.setLong(3, seq_value);
                            statement.setLong(4, 0);
                            assertEquals(1, statement.executeUpdate());

                            statement = connection.prepareStatement("SELECT * FROM blanks");
                            statement.execute();
                            result = statement.getResultSet();
                            result.next();

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
}
