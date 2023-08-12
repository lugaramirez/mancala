package com.fun.mancala.infra.adapters.controllers;

import com.fun.mancala.application.exceptions.BoardInitializationException;
import com.fun.mancala.application.exceptions.BoardMoveException;
import org.junit.jupiter.api.Test;
import org.springframework.http.ProblemDetail;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

class MancalaExceptionHandlerTest {
  private final MancalaExceptionHandler sut = new MancalaExceptionHandler();

  @Test
  void with_board_initialization_exception_status_is_401_and_problem_is_thrown() {
    final var testMessage = "a test message";
    final var testException = new BoardInitializationException(testMessage);
    var expectedProblem = ProblemDetail.forStatusAndDetail(BAD_REQUEST, testMessage);
    expectedProblem.setType(URI.create("http://localhost/errors/" + testException.getClass().getSimpleName()));

    var result = sut.gameExceptionHandler(testException);

    assertThat(result).isEqualTo(expectedProblem);
  }

  @Test
  void with_board_move_exception_status_is_401_and_problem_is_thrown() {
    final var testMessage = "a test message";
    final var testException = new BoardMoveException(testMessage);
    var expectedProblem = ProblemDetail.forStatusAndDetail(BAD_REQUEST, testMessage);
    expectedProblem.setType(URI.create("http://localhost/errors/" + testException.getClass().getSimpleName()));

    var result = sut.gameExceptionHandler(testException);

    assertThat(result).isEqualTo(expectedProblem);
  }

  @Test
  void with_runtime_exception_status_is_500_and_problem_is_thrown() {
    final var testMessage = "a test message";
    final var testException = new RuntimeException(testMessage);
    var expectedProblem = ProblemDetail.forStatusAndDetail(INTERNAL_SERVER_ERROR, "Something went wrong, please retry.");
    expectedProblem.setType(URI.create("http://localhost/errors/" + testException.getClass().getSimpleName()));

    var result = sut.gameExceptionHandler(testException);

    assertThat(result).isEqualTo(expectedProblem);
  }
}
