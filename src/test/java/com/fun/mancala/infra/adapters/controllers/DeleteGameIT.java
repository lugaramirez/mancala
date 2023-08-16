package com.fun.mancala.infra.adapters.controllers;

import com.fun.mancala.IntegrationTestsBase;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.*;

class DeleteGameIT extends IntegrationTestsBase {
  @Test
  void given_an_uninitialized_board_response_is_problem() {
    var body = delete()
      .then()
      .assertThat()
      .statusCode(NO_CONTENT.value())
      .extract()
      .body()
      .asString();

    assertThat(body).isEmpty();
  }
}
