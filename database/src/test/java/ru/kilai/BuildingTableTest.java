package ru.kilai;

import org.junit.jupiter.api.Test;
import org.postgresql.util.PSQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kilai.parameters.SimpleQueryParameters;
import ru.kilai.query.InsertQueryBuilder;
import ru.kilai.query.SequenceCurrentValueQueryBuilder;
import ru.kilai.query.SimpleSelectQueryBuilder;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.kilai.query.SimpleJDBCQueryExecutor.executor;

public class BuildingTableTest {
    private static final Logger log = LoggerFactory.getLogger(BuildingTableTest.class);

    private static final Map<String, Object> BUILDING_INSERT_PARAMS = Map.of("building_name", "Anywhere", "building_description", "Same description");
    private static final List<String> BUILDING_SELECT_PARAMS = List.of("building_id", "building_name", "building_description");


    @Test
    void changeLogBuilding() {
        try (var connection = PostgresConnectionPool.getConnection()) {
            LiquibaseTest.builder("classpath:/db/migration/master.xml", connection)
                    .makeTestAndRollback(liquibase -> {
                        long seq_value;

                        try (var statement = InsertQueryBuilder.builder(connection, "building")
                                .build(new SimpleQueryParameters(BUILDING_INSERT_PARAMS))) {
                            assertEquals(1, executor(statement).executeUpdate());
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }

                        try (var result = executor(SequenceCurrentValueQueryBuilder
                                .builder(connection, "building_id_seq")
                                .build(null))
                                .executeAndGetResult()) {
                            result.next();
                            assertEquals(1, result.getMetaData().getColumnCount());
                            seq_value = result.getLong(1);
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }

                        try (var result = executor(
                                SimpleSelectQueryBuilder.builder(connection, "building")
                                        .build(new SimpleQueryParameters(BUILDING_SELECT_PARAMS))
                        ).executeAndGetResult()) {
                            result.next();
                            for (String key : BUILDING_INSERT_PARAMS.keySet()) {
                                assertEquals(BUILDING_INSERT_PARAMS.get(key), result.getObject(key));
                            }
                            assertEquals(seq_value, result.getObject("building_id"));

                            result.next();
                            assertThrows(PSQLException.class, () -> result.getObject("building_id"));
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    });
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
