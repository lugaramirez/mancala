package com.fun.mancala.application;

import com.fun.mancala.domain.models.Board;
import com.fun.mancala.domain.models.Player;

public class BoardManager {
  private Board board;
  private Player player;

  public Board initialize(Integer[] initialBoard) throws BoardInitializationException {
    verifyInitialization(initialBoard);
    this.board = new Board(initialBoard);
    this.player = Player.ONE;
    return this.board;
  }

  private void verifyInitialization(Integer[] board) throws BoardInitializationException {
    if (this.board != null)
      throw new BoardInitializationException("The board is already initialized.");
    if (board.length % 2 != 0)
      throw new BoardInitializationException("The amount of pits on board should be even.");
    if (board.length < 6)
      throw new BoardInitializationException("The board should have at least three pits per player.");
    var emptyPits = 0;
    final var playerOneBase = Integer.valueOf(board.length / 2 - 1);
    final var playerTwoBase = Integer.valueOf(board.length - 1);
    for (Integer pit = 0; pit < board.length; pit++) {
      if (board[pit].equals(0) && (++emptyPits > 2 || (!pit.equals(playerOneBase) && !pit.equals(playerTwoBase))))
        throw new BoardInitializationException("The board should have only two empty pits at the right of each player.");
    }
  }

  public Board moveStonesFrom(Integer pit) throws BoardMoveException {
    final var playerOneBase = Integer.valueOf(this.board.pits().length / 2 - 1);
    final var playerTwoBase = Integer.valueOf(this.board.pits().length - 1);
    validateMoveFrom(pit, playerOneBase, playerTwoBase);
    var stones = this.board.pits()[pit];
    Integer lastModifiedPitStoneCount = null;
    this.board.pits()[pit] = 0;
    while (stones > 0) {
      ++pit;
      if ((this.player.equals(Player.ONE) && pit.equals(playerTwoBase)) || (this.player.equals(Player.TWO) && pit.equals(playerOneBase)))
        ++pit;
      if (pit >= this.board.pits().length) pit = 0;
      stones--;
      lastModifiedPitStoneCount = this.board.pits()[pit];
      this.board.pits()[pit]++;
    }
    if (!pit.equals(playerOneBase) && !pit.equals(playerTwoBase) &&
      lastModifiedPitStoneCount != null && lastModifiedPitStoneCount.equals(0) &&
      ((this.player.equals(Player.ONE) && pit < playerOneBase) || (this.player.equals(Player.TWO) && pit > playerOneBase))
    ) {
      var capturedPit = switch (this.player) {
        case ONE -> pit + playerOneBase + 1;
        case TWO -> pit - playerOneBase - 1;
      };
      var capturedStones = this.board.pits()[capturedPit];
      this.board.pits()[capturedPit] = 0;
      switch (this.player) {
        case ONE -> this.board.pits()[playerOneBase] += capturedStones;
        case TWO -> this.board.pits()[playerTwoBase] += capturedStones;
      }
    }
    this.player = switch (this.player) {
      case ONE -> Player.TWO;
      case TWO -> Player.ONE;
    };
    return board;
  }

  private void validateMoveFrom(Integer pit, Integer playerOneBase, Integer playerTwoBase) throws BoardMoveException {
    if ((this.player.equals(Player.ONE) && pit.equals(playerOneBase)) || (this.player.equals(Player.TWO) && pit.equals(playerTwoBase)))
      throw new BoardMoveException("The stones at the base should not be moved.");
    if ((this.player.equals(Player.ONE) && pit > playerOneBase) || (this.player.equals(Player.TWO) && pit <= playerOneBase))
      throw new BoardMoveException("Those stones are not yours to move.");
  }
}
