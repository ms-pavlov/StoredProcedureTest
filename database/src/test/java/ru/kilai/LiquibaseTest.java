package ru.kilai;

import liquibase.Liquibase;
import liquibase.Scope;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class LiquibaseTest {
    private final String changeLofFile;

    private final Connection connection;

    private LiquibaseTest(String changeLofFile, Connection connection) {
        this.changeLofFile = changeLofFile;
        this.connection = connection;
    }

    public static LiquibaseTest builder(String changeLofFile, Connection connection) {
        return new LiquibaseTest(changeLofFile, connection);
    }

    public void makeTestAndRollback(Consumer<Liquibase> consumer) {
        try {
            Scope.child(getDefaultConfig(), () -> {
                try (Liquibase liquibase = prepLiquibase(new JdbcConnection(connection))) {
                    liquibase.update("test");
                    consumer.accept(liquibase);
                    connection.rollback();
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    private Map<String, Object> getDefaultConfig() {
        return new HashMap<>();
    }

    @NotNull
    private Liquibase prepLiquibase(JdbcConnection jdbcConnection) throws DatabaseException {
        return new Liquibase(
                changeLofFile,
                new ClassLoaderResourceAccessor(),
                DatabaseFactory.getInstance().findCorrectDatabaseImplementation(jdbcConnection));
    }
}
