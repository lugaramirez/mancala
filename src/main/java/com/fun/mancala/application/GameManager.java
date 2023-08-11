package com.fun.mancala.application;

import com.fun.mancala.application.exceptions.BoardInitializationException;
import com.fun.mancala.application.exceptions.BoardMoveException;
import com.fun.mancala.domain.models.Board;
import com.fun.mancala.domain.models.Player;
import com.fun.mancala.domain.models.Status;
import org.springframework.stereotype.Service;

import static com.fun.mancala.domain.models.Player.ONE;
import static com.fun.mancala.domain.models.Player.TWO;
import static com.fun.mancala.domain.models.Status.DONE;
import static com.fun.mancala.domain.models.Status.PLAYABLE;

@Service
public class GameManager {
  private Board board;
  private Player player;
  private Status status;

  public Board initialize(Integer[] initialBoard) throws BoardInitializationException {
    verifyInitialization(initialBoard);
    this.board = new Board(initialBoard);
    this.player = ONE;
    this.status = PLAYABLE;
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
      if ((this.player.equals(ONE) && pit.equals(playerTwoBase)) || (this.player.equals(TWO) && pit.equals(playerOneBase)))
        ++pit;
      if (pit >= this.board.pits().length) pit = 0;
      stones--;
      lastModifiedPitStoneCount = this.board.pits()[pit];
      this.board.pits()[pit]++;
    }

    captureStonesIfApplicable(pit, playerOneBase, playerTwoBase, lastModifiedPitStoneCount);
    rotatePlayerIfApplicable(pit, playerOneBase, playerTwoBase);
    return board;
  }

  private void validateMoveFrom(Integer pit, Integer playerOneBase, Integer playerTwoBase) throws BoardMoveException {
    if (this.status.equals(DONE))
      throw new BoardMoveException("Game has ended. Player " + this.player + " won.");
    if ((this.player.equals(ONE) && pit.equals(playerOneBase)) || (this.player.equals(TWO) && pit.equals(playerTwoBase)))
      throw new BoardMoveException("The stones at the base should not be moved.");
    if ((this.player.equals(ONE) && pit > playerOneBase) || (this.player.equals(TWO) && pit <= playerOneBase))
      throw new BoardMoveException("Those stones are not yours to move.");
    if (this.board.pits()[pit].equals(0))
      throw new BoardMoveException("Choose a pit with stones.");
  }

  private void captureStonesIfApplicable(Integer pit, Integer playerOneBase, Integer playerTwoBase, Integer lastModifiedPitStoneCount) {
    if (!pit.equals(playerOneBase) && !pit.equals(playerTwoBase) &&
      lastModifiedPitStoneCount != null && lastModifiedPitStoneCount.equals(0) &&
      ((this.player.equals(ONE) && pit < playerOneBase) || (this.player.equals(TWO) && pit > playerOneBase))
    ) {
      var capturedPit = switch (this.player) {
        // oh, math... Y U DO BE LIKE DAT?!
        case ONE -> pit + playerOneBase + 1;
        case TWO -> pit - playerOneBase - 1;
      };
      var capturedStones = this.board.pits()[capturedPit] + this.board.pits()[pit];
      this.board.pits()[pit] = 0;
      this.board.pits()[capturedPit] = 0;
      switch (this.player) {
        case ONE -> this.board.pits()[playerOneBase] += capturedStones;
        case TWO -> this.board.pits()[playerTwoBase] += capturedStones;
      }
    }
  }

  private void rotatePlayerIfApplicable(Integer pit, Integer playerOneBase, Integer playerTwoBase) {
    var stonesOnPlayerOnePits = 0;
    var stonesOnPlayerTwoPits = 0;
    for (int i = 0; i < playerOneBase; i++) {
      stonesOnPlayerOnePits += this.board.pits()[i];
      stonesOnPlayerTwoPits += this.board.pits()[i + playerOneBase + 1];
    }
    if (stonesOnPlayerOnePits == 0 || stonesOnPlayerTwoPits == 0) {
      this.status = DONE;
    } else {
      if ((this.player.equals(ONE) && !pit.equals(playerOneBase)) ||
        (this.player.equals(TWO) && !pit.equals(playerTwoBase))) {
        this.player = switch (this.player) {
          case ONE -> TWO;
          case TWO -> ONE;
        };
      }
    }
  }

  public String gameStatus() {
    final var gameStatus = this.status.equals(PLAYABLE)? "Current" : "Final";
    final var playerOneBase = Integer.valueOf(this.board.pits().length / 2 - 1);
    final var playerTwoBase = Integer.valueOf(this.board.pits().length - 1);
    final var playerOneScore = this.board.pits()[playerOneBase];
    final var playerTwoScore = this.board.pits()[playerTwoBase];
    StringBuilder playerOneBoard = new StringBuilder();
    StringBuilder playerTwoBoard = new StringBuilder();
    for (int i = 0; i < playerOneBase; i++) {
      playerOneBoard.append("| %d ".formatted(this.board.pits()[i]));
      playerTwoBoard.append("| %d ".formatted(this.board.pits()[i + playerOneBase + 1]));
    }
    playerOneBoard.append("|| %d |".formatted(this.board.pits()[playerOneBase]));
    playerTwoBoard.append("|| %d |".formatted(this.board.pits()[playerTwoBase]));
    return """
      %s Board:
        Player ONE: %s
        Player TWO: %s
      %s Score:
        Player ONE: %d
        Player TWO: %d
      %s Player: %s
      Game: %s
      """.formatted(
      gameStatus,
      playerOneBoard.toString(),
      playerTwoBoard.toString(),
      gameStatus,
      playerOneScore,
      playerTwoScore,
      gameStatus,
      this.player,
      this.status
    );
  }
}
