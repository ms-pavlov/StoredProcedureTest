package ru.kilai;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import ru.kilai.utility.LiquibaseTestMaker;
import ru.kilai.utility.PostgresConnectionPool;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class ProcedureCheckOrderReadyTest {
    @Test
    void changeLogContacts() {
        try (var connection = PostgresConnectionPool.getConnection()) {
            LiquibaseTestMaker liquibaseTestMaker = LiquibaseTestMaker.prepareMaker("classpath:/db/migration/master.xml", connection);
            liquibaseTestMaker.addContext("main");
            liquibaseTestMaker.addContext("test");
            liquibaseTestMaker.makeTestsWithRollback(
                    () -> {
                        try {
                            checkOrderReady(connection, 1);

                            ResultSet result = select(connection, "SELECT * FROM order_position");
                            result.next();
                            assertFalse(result.getBoolean("order_position_is_complete"));

                            result = select(connection, "SELECT * FROM order_list");
                            result.next();
                            assertFalse(result.getBoolean("order_list_is_complete"));
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    },
                    () -> {
                        try {
                            var sql = "INSERT INTO delivery (delivery_count, delivery_order_position_id)\n" +
                                    "VALUES (?, ?);";
                            var statement = connection.prepareStatement(sql);
                            statement.setLong(1, 100);
                            statement.setLong(2, 1);
                            statement.executeUpdate();

                            checkOrderReady(connection, 1);

                            ResultSet result = select(connection, "SELECT * FROM order_position");
                            result.next();
                            assertTrue(result.getBoolean("order_position_is_complete"));

                            result = select(connection, "SELECT * FROM order_list");
                            result.next();
                            assertTrue(result.getBoolean("order_list_is_complete"));
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

    private void checkOrderReady(Connection connection, long orderId) throws SQLException {
        var statement = connection.prepareStatement("CALL check_order_ready(?)");
        statement.setLong(1, orderId);
        statement.execute();
    }
}
