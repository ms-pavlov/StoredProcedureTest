package ru.kilai;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kilai.utility.LiquibaseTestMaker;
import ru.kilai.utility.PostgresConnectionPool;
import ru.kilai.utility.tests.InsertAndSelectLiquibaseTestBuilder;
import ru.kilai.utility.tests.SequenceCurrentValueTestBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DepartmentsTableTest {
    private static final Logger log = LoggerFactory.getLogger(DepartmentsTableTest.class);

    private static final Map<String, Object> ORDER_INSERT_PARAMS = Map.of("delivery_count", 1000L);
    private static final List<String> ORDER_SELECT_PARAMS = List.of("delivery_id", "delivery_create_date",
            "delivery_count", "delivery_order_position_id");

    @Test
    void changeLogContacts() {
        try (var connection = PostgresConnectionPool.getConnection()) {
            LiquibaseTestMaker.builder("classpath:/db/migration/master.xml", connection)
                    .makeTestsAndRollback(
                            InsertAndSelectLiquibaseTestBuilder.builder(connection)
                                    .sqlObjectName("delivery")
                                    .insertParameters(ORDER_INSERT_PARAMS)
                                    .selectParameters(ORDER_SELECT_PARAMS)
                                    .test(this::checks)
                                    .build(),
                            SequenceCurrentValueTestBuilder.builder(connection)
                                    .sqlObjectName("delivery_id_seq")
                                    .build()
                    );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void checks(ResultSet result) {
        try {
            assertNotNull(result.getObject("delivery_id"));
            assertNotNull(result.getObject("delivery_create_date"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
