package ru.kilai;

import liquibase.exception.LiquibaseException;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

                            var query = "INSERT INTO formats (formats_name) VALUES (?)";
                            var statement = connection.prepareStatement(query);
                            statement.setString(1, FORMAT_NAME);
                            assertEquals(1, statement.executeUpdate());

                            statement = connection.prepareStatement("SELECT CURRVAL('formats_formats_id_seq')");
                            statement.execute();
                            var result = statement.getResultSet();
                            result.next();
                            long seq_value = Long.parseLong(result.getObject(1).toString());

                            statement = connection.prepareStatement("SELECT formats_id, formats_name FROM formats");
                            statement.execute();
                            result = statement.getResultSet();
                            result.next();
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
}
