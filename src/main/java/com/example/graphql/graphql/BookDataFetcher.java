package com.example.graphql.graphql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

@Component
public class BookDataFetcher implements DataFetcher<Book> {
    @Autowired
    private BookRepository repository;

    @Override
    public Book get(DataFetchingEnvironment environment) {
        String movieId = environment.getArgument("id");
        return repository.findOne(movieId);
    }
}