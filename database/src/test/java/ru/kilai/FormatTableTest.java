package ru.kilai;

import org.junit.jupiter.api.Test;
import ru.kilai.utility.LiquibaseTestMaker;
import ru.kilai.utility.PostgresConnectionPool;
import ru.kilai.utility.tests.InsertAndSelectLiquibaseTestBuilder;
import ru.kilai.utility.tests.SequenceCurrentValueTestBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class FormatTableTest {
    private static final Map<String, Object> FORMAT_INSERT_PARAMS = Map.of("formats_name", "A5");
    private static final List<String> FORMAT_SELECT_PARAMS = List.of("formats_name", "formats_id");

    @Test
    void changeLogFormats() {
        try (var connection = PostgresConnectionPool.getConnection()) {
            LiquibaseTestMaker.builder("classpath:/db/migration/master.xml", connection)
                    .makeTestsAndRollback(
                            InsertAndSelectLiquibaseTestBuilder.builder(connection)
                                    .sqlObjectName("format")
                                    .insertParameters(FORMAT_INSERT_PARAMS)
                                    .selectParameters(FORMAT_SELECT_PARAMS)
                                    .test(this::checkID)
                                    .build(),
                            SequenceCurrentValueTestBuilder.builder(connection)
                                    .sqlObjectName("formats_id_seq")
                                    .build()
                    );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void checkID(ResultSet result) {
        try {
            assertNotNull(result.getObject("formats_id"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
