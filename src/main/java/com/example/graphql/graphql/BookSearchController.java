package com.example.graphql.graphql;

import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;

@RestController
@RequestMapping("/bookstore")
public class BookSearchController {
  @Autowired
  private BookService service;
  // load graphqls file
  @Value("classpath:book.schema")
  private Resource schemaResource;
  @Autowired
  private AllBookDataFetcher allBookDataFetcher;
  @Autowired
  private BookDataFetcher bookDataFetcher;
  private GraphQL graphQL;

  // load schema at application start up
  @PostConstruct
  public void loadSchema() throws IOException {
    // get the schema
    File schemaFile = schemaResource.getFile();
    // parse schema
    TypeDefinitionRegistry typeRegistry = new SchemaParser().parse(schemaFile);
    RuntimeWiring wiring = buildRuntimeWiring();
    GraphQLSchema schema = new SchemaGenerator().makeExecutableSchema(typeRegistry, wiring);
    graphQL = GraphQL.newGraphQL(schema).build();
  }

  private RuntimeWiring buildRuntimeWiring() {
    return RuntimeWiring.newRuntimeWiring()
        .type("Query",
            typeWiring -> typeWiring.dataFetcher("allBooks", allBookDataFetcher).dataFetcher("book", bookDataFetcher))
        .build();
  }

  @GetMapping("/booksList")
  public List<Book> getBooksList() {
    return service.findAllBooks();
  }

  /*
   * In PostMan use Post URL: localhost:8080/bookstore/getAllBooks and Body:
   * query{ allBooks{ bookId, bookName } }
   */
  @PostMapping("/getAllBooks")
  public ResponseEntity<Object> getAllBooks(@RequestBody String query) {
    ExecutionResult result = graphQL.execute(query);
    return new ResponseEntity<Object>(result, HttpStatus.OK);
  }

  @GetMapping("/search/{bookId}")
  public Book getBookInfo(@PathVariable String movieId) {
    return service.findBookById(movieId);
  }

  @PostMapping("/getBookById")
  public ResponseEntity<Object> getBookById(@RequestBody String query) {
    ExecutionResult result = graphQL.execute(query);
    return new ResponseEntity<Object>(result, HttpStatus.OK);
  }
}