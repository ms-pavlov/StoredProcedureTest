package ru.kilai;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import ru.kilai.utility.LiquibaseTestMaker;
import ru.kilai.utility.PostgresConnectionPool;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class ProcedureMakeDeliveryTest {

    @Test
    void changeLogContacts() {
        try (var connection = PostgresConnectionPool.getConnection()) {
            LiquibaseTestMaker liquibaseTestMaker = LiquibaseTestMaker.prepareMaker("classpath:/db/migration/master.xml", connection);
            liquibaseTestMaker.addContext("main");
            liquibaseTestMaker.addContext("test");
            liquibaseTestMaker.makeTestsWithRollback(
                    () -> {
                        try {
                            makeDelivery(connection, 70, 1);

                            ResultSet result = select(connection, "SELECT * FROM order_position");
                            result.next();
                            assertEquals(0L, result.getLong("order_position_ready"));
                            assertFalse( result.getBoolean("order_position_is_complete"));

                            result = select(connection, "SELECT * FROM delivery");
                            result.next();
                            assertEquals(70L, result.getLong("delivery_count"));
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    },
                    () -> {
                        try {
                            makeDelivery(connection, 100, 1);

                            ResultSet result = select(connection, "SELECT * FROM order_position");
                            result.next();
                            assertEquals(0L, result.getLong("order_position_ready"));
                            assertTrue( result.getBoolean("order_position_is_complete"));

                            result = select(connection, "SELECT * FROM order_list");
                            result.next();
                            assertTrue( result.getBoolean("order_list_is_complete"));
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    private ResultSet select(Connection connection, String sql) throws SQLException {
        var statement = connection.prepareStatement(sql);
        statement.execute();

        return statement.getResultSet();
    }

    private void makeDelivery(Connection connection, long count, long orderId) throws SQLException {
        var statement = connection.prepareStatement("CALL make_delivery(?, ?)");
        statement.setLong(1, count);
        statement.setLong(2, orderId);
        statement.execute();
    }
}
