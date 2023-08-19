package com.fun.mancala.domain.models;

import java.util.UUID;

import static com.fun.mancala.domain.models.Player.ONE;
import static com.fun.mancala.domain.models.Status.PLAYABLE;

public class Game {
  private UUID id;
  private Board board;
  private Player player;
  private Status status;

  public Game(Integer[] initialBoard) {
    this.id = UUID.randomUUID();
    this.board = new Board(initialBoard);
    this.player = ONE;
    this.status = PLAYABLE;
  }

  public Game(UUID id, Integer[] initialBoard, Player player, Status status) {
    this.id = id;
    this.board = new Board(initialBoard);
    this.player = player;
    this.status = status;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public Board getBoard() {
    return board;
  }

  public void setBoard(Board board) {
    this.board = board;
  }

  public Player getPlayer() {
    return player;
  }

  public void setPlayer(Player player) {
    this.player = player;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  static Game fromState(State gameState) {
    return new Game(
      gameState.id,
      gameState.board.pits(),
      gameState.player,
      gameState.status
    );
  }

  public record State(UUID id, Board board, Player player, Status status) {
  }
}
