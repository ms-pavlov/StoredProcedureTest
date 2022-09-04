package ru.kilai;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.postgresql.util.PSQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kilai.parameters.SimpleQueryParameters;
import ru.kilai.query.InsertQueryBuilder;
import ru.kilai.query.SequenceCurrentValueQueryBuilder;
import ru.kilai.query.SimpleSelectQueryBuilder;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.kilai.query.SimpleJDBCQueryExecutor.executor;

public class FormatTableTest {
    private static final Logger log = LoggerFactory.getLogger(FormatTableTest.class);

    private static final Map<String, Object> FORMAT_INSERT_PARAMS = Map.of("formats_name", "A5");
    private static final List<String> FORMAT_SELECT_PARAMS = List.of("formats_name", "formats_id");

    @Test
    void changeLogFormats() {
        try (var connection = PostgresConnectionPool.getConnection()) {
            LiquibaseTest.builder("classpath:/db/migration/master.xml", connection)
                    .makeTestAndRollback(liquibase -> {
                        long seq_value;

                        try (var statement = InsertQueryBuilder.builder(connection, "format")
                                .build(new SimpleQueryParameters(FORMAT_INSERT_PARAMS))) {
                            assertEquals(1, executor(statement).executeUpdate());
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }

                        try (var result = executor(SequenceCurrentValueQueryBuilder
                                .builder(connection, "formats_id_seq")
                                .build(null))
                                .executeAndGetResult()) {
                            result.next();
                            assertEquals(1, result.getMetaData().getColumnCount());
                            seq_value = result.getLong(1);
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }

                        try (var result = executor(
                                SimpleSelectQueryBuilder.builder(connection, "format")
                                        .build(new SimpleQueryParameters(FORMAT_SELECT_PARAMS))
                        ).executeAndGetResult()) {
                            result.next();
                            assertEquals(FORMAT_INSERT_PARAMS.get("formats_name"), result.getObject("formats_name"));
                            assertEquals(seq_value, result.getObject("formats_id"));

                            result.next();
                            assertThrows(PSQLException.class, () -> result.getObject("formats_id"));
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    });
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
