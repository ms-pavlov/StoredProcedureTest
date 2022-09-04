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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BlanksTableTest {
    private static final Map<String, Object> BLANK_INSERT_PARAMS = Map.of("blank_name", "Опросный лист",
            "blank_two_side", true);
    private static final List<String> BLANK_SELECT_PARAMS = List.of("blank_id", "blank_name", "blank_two_side",
            "blank_format_id", "blank_orders_count");


    @Test
    void changeLogBlanks() {
        try (var connection = PostgresConnectionPool.getConnection()) {
            LiquibaseTestMaker.prepareMaker("classpath:/db/migration/master.xml", connection)
                    .addContext("main")
                    .makeTestsWithRollback(
                            InsertAndSelectLiquibaseTestBuilder.builder(connection, "blank")
                                    .insertParameters(BLANK_INSERT_PARAMS)
                                    .selectParameters(BLANK_SELECT_PARAMS)
                                    .test(this::checks)
                                    .build(),
                            SequenceCurrentValueTestBuilder.builder(connection)
                                    .sqlObjectName("blank_id_seq")
                                    .build()
                    );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void checks(ResultSet result) {
        try {
            assertNotNull(result.getObject("blank_id"));
            assertEquals(0L, result.getObject("blank_orders_count"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
