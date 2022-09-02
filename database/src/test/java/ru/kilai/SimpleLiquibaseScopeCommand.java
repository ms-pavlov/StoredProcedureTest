package ru.kilai;

import liquibase.Liquibase;
import liquibase.Scope;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class SimpleLiquibaseScopeCommand implements LiquibaseScopeCommand{
    private final String changeLofFile;
    private final Consumer<Liquibase> consumer;

    private final Connection connection;

    public SimpleLiquibaseScopeCommand(String changeLofFile, Connection connection, Consumer<Liquibase> consumer) {
        this.changeLofFile = changeLofFile;
        this.consumer = consumer;
        this.connection = connection;
    }

    @Override
    public void accept() {
        try {
            Scope.child(getDefaultConfig(), () -> {
                try (Liquibase liquibase = prepLiquibase(new JdbcConnection(connection))) {
                    consumer.accept(liquibase);
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
