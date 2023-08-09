package com.fun.mancala.application;

public class BoardMoveException extends RuntimeException {
  public BoardMoveException(String message) {
    super(message);
  }
}
