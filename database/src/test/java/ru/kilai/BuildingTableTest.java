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

public class BuildingTableTest {
    private static final Map<String, Object> BUILDING_INSERT_PARAMS = Map.of("building_name", "Anywhere", "building_description", "Same description");
    private static final List<String> BUILDING_SELECT_PARAMS = List.of("building_id", "building_name", "building_description");


    @Test
    void changeLogBuilding() {
        try (var connection = PostgresConnectionPool.getConnection()) {
            LiquibaseTestMaker.prepareMaker("classpath:/db/migration/master.xml", connection)
                    .addContext("main")
                    .makeTestsWithRollback(
                            InsertAndSelectLiquibaseTestBuilder.builder(connection)
                                    .sqlObjectName("building")
                                    .insertParameters(BUILDING_INSERT_PARAMS)
                                    .selectParameters(BUILDING_SELECT_PARAMS)
                                    .test(this::checkID)
                                    .build(),
                            SequenceCurrentValueTestBuilder.builder(connection)
                                    .sqlObjectName("building_id_seq")
                                    .build()
                    );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void checkID(ResultSet result) {
        try {
            assertNotNull(result.getObject("building_id"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
