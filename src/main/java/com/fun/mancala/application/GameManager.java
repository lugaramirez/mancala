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
  private Integer playerOneBase;
  private Integer playerTwoBase;

  public Board initialize(Integer[] initialBoard) throws BoardInitializationException {
    verifyInitialization(initialBoard);
    board = new Board(initialBoard);
    player = ONE;
    status = PLAYABLE;
    playerOneBase = board.pits().length / 2 - 1;
    playerTwoBase = board.pits().length - 1;
    return board;
  }

  public void clearGame() {
    board = null;
  }

  private void verifyInitialization(Integer[] initialBoard) throws BoardInitializationException {
    if (board != null)
      throw new BoardInitializationException("The board is already initialized.");
    if (initialBoard == null)
      throw new BoardInitializationException("Provide an initial state to the board.");
    if (initialBoard.length % 2 != 0)
      throw new BoardInitializationException("The amount of pits on board should be even.");
    if (initialBoard.length < 6)
      throw new BoardInitializationException("The board should have at least two pits plus a base per player.");
    var emptyPits = 0;
    final var p1Base = Integer.valueOf(initialBoard.length / 2 - 1);
    final var p2Base = Integer.valueOf(initialBoard.length - 1);
    for (Integer pit = 0; pit < initialBoard.length; pit++) {
      if (initialBoard[pit].equals(0) && (++emptyPits > 2 || (!pit.equals(p1Base) && !pit.equals(p2Base))))
        throw new BoardInitializationException("The board should have only two empty pits at the right of each player.");
    }
  }

  public Board moveStonesFrom(Integer pit) throws BoardMoveException {
    validateMoveFrom(pit);

    var stones = board.pits()[pit];
    Integer lastModifiedPitStoneCount = null;
    board.pits()[pit] = 0;
    while (stones > 0) {
      ++pit;
      if ((player.equals(ONE) && pit.equals(playerTwoBase)) || (player.equals(TWO) && pit.equals(playerOneBase)))
        ++pit;
      if (pit >= board.pits().length) pit = 0;
      stones--;
      lastModifiedPitStoneCount = board.pits()[pit];
      board.pits()[pit]++;
    }

    captureStonesIfApplicable(pit, lastModifiedPitStoneCount);
    rotatePlayerIfApplicable(pit);
    return board;
  }

  private void validateMoveFrom(Integer pit) throws BoardMoveException {
    if (board == null)
      throw new BoardMoveException("The board has not been initialized yet.");
    if (status.equals(DONE))
      throw new BoardMoveException("Game has ended. Player " + player + " won.");
    if ((player.equals(ONE) && pit.equals(playerOneBase)) || (player.equals(TWO) && pit.equals(playerTwoBase)))
      throw new BoardMoveException("The stones at the base should not be moved.");
    if ((player.equals(ONE) && pit > playerOneBase) || (player.equals(TWO) && pit <= playerOneBase))
      throw new BoardMoveException("Those stones are not yours to move.");
    if (board.pits()[pit].equals(0))
      throw new BoardMoveException("Choose a pit with stones.");
  }

  private void captureStonesIfApplicable(Integer pit, Integer lastModifiedPitStoneCount) {
    if (!pit.equals(playerOneBase) && !pit.equals(playerTwoBase) &&
      lastModifiedPitStoneCount != null && lastModifiedPitStoneCount.equals(0) &&
      ((player.equals(ONE) && pit < playerOneBase) || (player.equals(TWO) && pit > playerOneBase))
    ) {
      var capturedPit = switch (player) {
        // oh, math... Y U DO BE LIKE DAT?!
        case ONE -> pit + playerOneBase + 1;
        case TWO -> pit - playerOneBase - 1;
      };
      var capturedStones = board.pits()[capturedPit] + board.pits()[pit];
      board.pits()[pit] = 0;
      board.pits()[capturedPit] = 0;
      switch (player) {
        case ONE -> board.pits()[playerOneBase] += capturedStones;
        case TWO -> board.pits()[playerTwoBase] += capturedStones;
      }
    }
  }

  private void rotatePlayerIfApplicable(Integer pit) {
    var stonesOnPlayerOnePits = 0;
    var stonesOnPlayerTwoPits = 0;
    for (int i = 0; i < playerOneBase; i++) {
      stonesOnPlayerOnePits += board.pits()[i];
      stonesOnPlayerTwoPits += board.pits()[i + playerOneBase + 1];
    }
    if (stonesOnPlayerOnePits == 0 || stonesOnPlayerTwoPits == 0) {
      status = DONE;
    } else {
      if ((player.equals(ONE) && !pit.equals(playerOneBase)) ||
        (player.equals(TWO) && !pit.equals(playerTwoBase))) {
        player = switch (player) {
          case ONE -> TWO;
          case TWO -> ONE;
        };
      }
    }
  }

  public String gameStatus() {
    if (board == null)
      throw new BoardInitializationException("The board has not been initialized yet.");
    final var gameStatus = status.equals(PLAYABLE)? "Current" : "Final";
    final var playerOneScore = board.pits()[playerOneBase];
    final var playerTwoScore = board.pits()[playerTwoBase];
    StringBuilder playerOneBoard = new StringBuilder();
    StringBuilder playerTwoBoard = new StringBuilder();
    for (int i = 0; i < playerOneBase; i++) {
      playerOneBoard.append("| %d ".formatted(board.pits()[i]));
      playerTwoBoard.append("| %d ".formatted(board.pits()[i + playerOneBase + 1]));
    }
    playerOneBoard.append("|| %d |".formatted(board.pits()[playerOneBase]));
    playerTwoBoard.append("|| %d |".formatted(board.pits()[playerTwoBase]));
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
      player,
      status
    );
  }
}
