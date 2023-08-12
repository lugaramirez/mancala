package com.fun.mancala.infra.adapters.controllers;

import com.fun.mancala.IntegrationTestsBase;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.post;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

class PostGameInitializationIT extends IntegrationTestsBase {
  @Test
  void given_wrong_initialization_board_response_is_problem() {
    post("/clear").andReturn();
    given().body(new Integer[]{1, 1, 0, 1, 1, 0}).post("/initialize").andReturn();
    var body = given()
      .body(3)
      .post("/move")
      .then()
      .assertThat()
      .statusCode(BAD_REQUEST.value())
      .extract()
      .body()
      .jsonPath();

    assertThat(body.getString("title")).isEqualTo(BAD_REQUEST.getReasonPhrase());
    assertThat(body.getString("type")).isEqualTo("http://localhost/errors/BoardMoveException");
    assertThat(body.getString("detail")).isEqualTo("Those stones are not yours to move.");
  }

  @Test
  void given_proper_initialization_board_response_is_200_and_game_status() {
    post("/clear").andReturn();
    given().body(new Integer[]{1, 1, 0, 1, 1, 0}).post("/initialize").andReturn();
    var body = given()
      .body(0)
      .post("/move")
      .then()
      .assertThat()
      .statusCode(OK.value())
      .extract()
      .body()
      .asString();

    assertThat(body).isEqualTo("""
      Current Board:
        Player ONE: | 0 | 2 || 0 |
        Player TWO: | 1 | 1 || 0 |
      Current Score:
        Player ONE: 0
        Player TWO: 0
      Current Player: TWO
      Game: PLAYABLE
      """);
  }
}
