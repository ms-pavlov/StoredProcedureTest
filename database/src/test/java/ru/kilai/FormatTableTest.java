package ru.kilai;

import liquibase.exception.LiquibaseException;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kilai.parameters.SimpleQueryParameters;
import ru.kilai.query.CurrentSequenceValueQueryBuilder;
import ru.kilai.query.InsertQueryBuilder;
import ru.kilai.query.SelectQueryBuilder;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FormatTableTest {
    private static final Logger log = LoggerFactory.getLogger(FormatTableTest.class);

    private static final Map<String, Object> FORMAT_INSERT_PARAMS = Map.of("formats_name", "A5");
    private static final List<String> FORMAT_SELECT_PARAMS = List.of("formats_name", "formats_id");

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

                            statement = CurrentSequenceValueQueryBuilder
                                    .builder(connection, "formats_id_seq")
                                    .build(null);
                            var result = executeAndGetFirstResultSet(statement);
                            long seq_value = Long.parseLong(result.getObject(1).toString());

                            statement = SelectQueryBuilder.builder(connection, "formats")
                                        .build(new SimpleQueryParameters(FORMAT_SELECT_PARAMS));
                            result = executeAndGetFirstResultSet(statement);
                            assertEquals(FORMAT_INSERT_PARAMS.get("formats_name"), result.getObject("formats_name"));
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
