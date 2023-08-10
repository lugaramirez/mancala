package com.fun.mancala.application.exceptions;

public class BoardMoveException extends RuntimeException {
  public BoardMoveException(String message) {
    super(message);
  }
}
