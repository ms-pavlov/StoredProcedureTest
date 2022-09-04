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

import static org.junit.jupiter.api.Assertions.*;

public class OrderTableTest {
    private static final Logger log = LoggerFactory.getLogger(OrderTableTest.class);

    private static final Map<String, Object> ORDER_INSERT_PARAMS = Map.of("order_position_count", 1000L);
    private static final List<String> ORDER_SELECT_PARAMS = List.of("order_position_id", "order_position_plan_date",
            "order_position_count", "order_position_ready", "order_position_is_complete", "order_position_blank_id",
            "order_position_order_list_id");

    @Test
    void changeLogContacts() {
        try (var connection = PostgresConnectionPool.getConnection()) {
            LiquibaseTestMaker.builder("classpath:/db/migration/master.xml", connection)
                    .makeTestsAndRollback(
                            InsertAndSelectLiquibaseTestBuilder.builder(connection)
                                    .sqlObjectName("order_position")
                                    .insertParameters(ORDER_INSERT_PARAMS)
                                    .selectParameters(ORDER_SELECT_PARAMS)
                                    .test(this::checks)
                                    .build(),
                            SequenceCurrentValueTestBuilder.builder(connection)
                                    .sqlObjectName("order_id_seq")
                                    .build()
                    );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void checks(ResultSet result) {
        try {
            assertNotNull(result.getObject("order_position_id"));
            assertNotNull(result.getObject("order_position_plan_date"));
            assertFalse(result.getBoolean("order_position_is_complete"));
            assertEquals(0L, result.getObject("order_position_ready"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
