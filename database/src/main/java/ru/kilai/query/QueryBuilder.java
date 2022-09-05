package ru.kilai.query;

import java.sql.PreparedStatement;

public interface QueryBuilder {

    PreparedStatement buildQuery();

    PreparedStatement prepareStatement(String queryTemplate);
}
