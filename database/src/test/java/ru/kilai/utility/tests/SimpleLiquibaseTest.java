package ru.kilai.utility.tests;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.util.function.Consumer;

public class SimpleLiquibaseTest implements LiquibaseTest {
    private static final Logger log = LoggerFactory.getLogger(SimpleLiquibaseTest.class);
    private final Consumer<PreparedStatement> test;
    private final PreparedStatement statement;


    public SimpleLiquibaseTest(PreparedStatement statement, Consumer<PreparedStatement> test) {
        this.statement = statement;
        this.test = test;
    }

    public void makeTest() {
        test.accept(statement);
    }
}
