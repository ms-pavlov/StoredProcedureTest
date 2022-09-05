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

public class PersonTableTest {
    private static final Map<String, Object> PERSON_INSERT_PARAMS = Map.of("person_name", "Менеджер",
            "person_phone", "5223232");
    private static final List<String> PERSON_SELECT_PARAMS = List.of("person_id", "person_name", "person_phone",
            "person_department_id", "person_orders_count");

    @Test
    void changeLogContacts() {
        try (var connection = PostgresConnectionPool.getConnection()) {
            LiquibaseTestMaker.prepareMaker("classpath:/db/migration/master.xml", connection)
                    .addContext("main")
                    .makeTestsWithRollback(
                            InsertAndSelectLiquibaseTestBuilder.builder(connection, "person")
                                    .insertParameters(PERSON_INSERT_PARAMS)
                                    .selectParameters(PERSON_SELECT_PARAMS)
                                    .test(this::checks)
                                    .build(),
                            SequenceCurrentValueTestBuilder.builder(connection)
                                    .sqlObjectName("person_id_seq")
                                    .build()
                    );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void checks(ResultSet result) {
        try {
            assertNotNull(result.getObject("person_id"));
            assertEquals(0L, result.getObject("person_orders_count"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
