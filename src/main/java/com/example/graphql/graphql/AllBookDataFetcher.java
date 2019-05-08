package com.example.graphql.graphql;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

@Component
public class AllBookDataFetcher implements DataFetcher<List<Book>> {
    @Autowired
    private BookRepository repository;

    @Override
    public List<Book> get(DataFetchingEnvironment environment) {
        return repository.findAll();
    }
}