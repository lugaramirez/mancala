package com.fun.mancala;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.http.ContentType.JSON;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@TestInstance(PER_CLASS)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class IntegrationTestsBase {
  @LocalServerPort
  protected int port;

  @BeforeAll
  void init() {
    RestAssured.port = port;
    RestAssured.requestSpecification = new RequestSpecBuilder().setContentType(JSON).build();
  }

  @AfterAll
  void teardown() {
    RestAssured.reset();
  }
}
