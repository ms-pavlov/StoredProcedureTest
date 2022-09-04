package ru.kilai.utility;

import liquibase.Liquibase;
import liquibase.Scope;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kilai.utility.tests.LiquibaseTest;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class LiquibaseTestMaker {
    private static final Logger log = LoggerFactory.getLogger(LiquibaseTestMaker.class);
    private final String changeLofFile;
    private final Connection connection;

    private LiquibaseTestMaker(String changeLofFile, Connection connection) {
        this.changeLofFile = changeLofFile;
        this.connection = connection;
    }

    public static LiquibaseTestMaker builder(String changeLofFile, Connection connection) {
        return new LiquibaseTestMaker(changeLofFile, connection);
    }

    public void makeTestsAndRollback(LiquibaseTest... tests) {
        try {
            Scope.child(getDefaultConfig(), () -> {
                try (Liquibase liquibase = prepLiquibase(new JdbcConnection(connection))) {
                    liquibase.tag("beginTest");
                    liquibase.update("test");
                    log.debug("Start tests ...");
                    Stream.of(tests).forEach(LiquibaseTest::makeTest);
                    liquibase.rollback("beginTest", "test");
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
