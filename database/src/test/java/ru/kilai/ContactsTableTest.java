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

public class ContactsTableTest {
    private static final Map<String, Object> CONTACTS_INSERT_PARAMS = Map.of("contact_name", "Менеджер",
            "contact_phone", "5223232");
    private static final List<String> CONTACTS_SELECT_PARAMS = List.of("contact_id", "contact_name", "contact_phone",
            "contact_department_id", "contact_orders_count");

    @Test
    void changeLogContacts() {
        try (var connection = PostgresConnectionPool.getConnection()) {
            LiquibaseTestMaker.builder("classpath:/db/migration/master.xml", connection)
                    .makeTestsAndRollback(
                            InsertAndSelectLiquibaseTestBuilder.builder(connection)
                                    .sqlObjectName("contact")
                                    .insertParameters(CONTACTS_INSERT_PARAMS)
                                    .selectParameters(CONTACTS_SELECT_PARAMS)
                                    .test(this::checks)
                                    .build(),
                            SequenceCurrentValueTestBuilder.builder(connection)
                                    .sqlObjectName("contact_id_seq")
                                    .build()
                    );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void checks(ResultSet result) {
        try {
            assertNotNull(result.getObject("contact_id"));
            assertEquals(0L, result.getObject("contact_orders_count"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
