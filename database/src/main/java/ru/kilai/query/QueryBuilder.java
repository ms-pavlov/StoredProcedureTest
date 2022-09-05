package ru.kilai.query;

import java.sql.PreparedStatement;

public interface QueryBuilder {

    PreparedStatement build();

    PreparedStatement prepareStatement(String queryTemplate);
}
