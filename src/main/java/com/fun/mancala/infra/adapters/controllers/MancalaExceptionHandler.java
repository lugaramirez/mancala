package com.fun.mancala.infra.adapters.controllers;

import com.fun.mancala.application.exceptions.BoardInitializationException;
import com.fun.mancala.application.exceptions.BoardMoveException;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

// RFC-7807 not fully implemented, but at least returned
@RestControllerAdvice
public class MancalaExceptionHandler {
  private static final String BASE_PROBLEM_URL = "http://localhost/errors/";

  @ExceptionHandler(BoardInitializationException.class)
  public ProblemDetail gameExceptionHandler(BoardInitializationException e) {
    var problem = ProblemDetail.forStatusAndDetail(BAD_REQUEST, e.getMessage());
    problem.setType(URI.create(BASE_PROBLEM_URL + e.getClass().getSimpleName()));
    return problem;
  }

  @ExceptionHandler(BoardMoveException.class)
  public ProblemDetail gameExceptionHandler(BoardMoveException e) {
    var problem = ProblemDetail.forStatusAndDetail(BAD_REQUEST, e.getMessage());
    problem.setType(URI.create(BASE_PROBLEM_URL + e.getClass().getSimpleName()));
    return problem;
  }

  @ExceptionHandler(RuntimeException.class)
  public ProblemDetail gameExceptionHandler(RuntimeException e) {
    var problem = ProblemDetail.forStatusAndDetail(INTERNAL_SERVER_ERROR, "Something went wrong, please retry.");
    problem.setType(URI.create(BASE_PROBLEM_URL + e.getClass().getSimpleName()));
    return problem;
  }
}
