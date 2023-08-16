package com.fun.mancala.infra.adapters.controllers;

import com.fun.mancala.IntegrationTestsBase;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

class GetGameStatusIT extends IntegrationTestsBase {
  @Test
  void given_an_uninitialized_board_response_is_problem() {
    delete().andReturn();
    var body = get("/status")
      .then()
      .assertThat()
      .statusCode(BAD_REQUEST.value())
      .extract()
      .body()
      .jsonPath();

    assertThat(body.getString("title")).isEqualTo(BAD_REQUEST.getReasonPhrase());
    assertThat(body.getString("type")).isEqualTo("http://localhost/errors/BoardInitializationException");
    assertThat(body.getString("detail")).isEqualTo("The board has not been initialized yet.");
  }

  @Test
  void given_initialized_board_response_is_200_and_game_status() {
    delete().andReturn();
    given().body(new Integer[]{1, 1, 0, 1, 1, 0}).post("/initialize").andReturn();
    var body = get("/status").then()
      .assertThat()
      .statusCode(OK.value())
      .extract()
      .body()
      .asString();

    assertThat(body).isEqualTo("""
      Current Board:
        Player ONE: | 1 | 1 || 0 |
        Player TWO: | 1 | 1 || 0 |
      Current Score:
        Player ONE: 0
        Player TWO: 0
      Current Player: ONE
      Game: PLAYABLE
      """);
  }
}
