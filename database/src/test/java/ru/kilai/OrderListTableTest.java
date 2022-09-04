package ru.kilai;

import org.junit.jupiter.api.Test;
import ru.kilai.utility.LiquibaseTestMaker;
import ru.kilai.utility.PostgresConnectionPool;
import ru.kilai.utility.tests.InsertAndSelectLiquibaseTestBuilder;
import ru.kilai.utility.tests.SequenceCurrentValueTestBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class OrderListTableTest {
    private static final List<String> ORDER_LIST_SELECT_PARAMS = List.of("order_list_id", "order_list_create_date",
            "order_list_contact_id", "order_list_is_complete");

    @Test
    void changeLogContacts() {
        try (var connection = PostgresConnectionPool.getConnection()) {
            LiquibaseTestMaker.prepareMaker("classpath:/db/migration/master.xml", connection)
                    .addContext("main")
                    .makeTestsWithRollback(
                            InsertAndSelectLiquibaseTestBuilder.builder(connection, "order_list")
                                    .insertParameters(Map.of("order_list_create_date", new Timestamp(new Date().getTime())))
                                    .selectParameters(ORDER_LIST_SELECT_PARAMS)
                                    .test(this::checks)
                                    .build(),
                            SequenceCurrentValueTestBuilder.builder(connection)
                                    .sqlObjectName("order_list_id_seq")
                                    .build()
                    );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void checks(ResultSet result) {
        try {
            assertNotNull(result.getObject("order_list_id"));
            assertFalse(result.getBoolean("order_list_is_complete"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
