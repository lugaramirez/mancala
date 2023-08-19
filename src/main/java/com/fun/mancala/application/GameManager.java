package com.fun.mancala.application;

import com.fun.mancala.application.exceptions.BoardInitializationException;
import com.fun.mancala.application.exceptions.BoardMoveException;
import com.fun.mancala.domain.models.Game;
import org.springframework.stereotype.Service;

import static com.fun.mancala.domain.models.Player.ONE;
import static com.fun.mancala.domain.models.Player.TWO;
import static com.fun.mancala.domain.models.Status.DONE;
import static com.fun.mancala.domain.models.Status.PLAYABLE;

@Service
public class GameManager {
  private static final int MAXIMUM_STONES = 10;
  private Game game;
  private Integer playerOneBase;
  private Integer playerTwoBase;

  public Game initialize(Integer[] initialBoard) throws BoardInitializationException {
    verifyInitialization(initialBoard);
    game = new Game(initialBoard);
    playerOneBase = game.getBoard().pits().length / 2 - 1;
    playerTwoBase = game.getBoard().pits().length - 1;
    return game;
  }

  public void clearGame() {
    this.game = null;
  }

  private void verifyInitialization(Integer[] initialBoard) throws BoardInitializationException {
    if (game != null)
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
      if (initialBoard[pit].compareTo(MAXIMUM_STONES) > 0)
        throw new BoardInitializationException("There are too many stones on pit "+ pit +". The maximum amount of stones is "+ MAXIMUM_STONES +". Fix the initialization board and retry.");
      if (initialBoard[pit].compareTo(0) < 0)
        throw new BoardInitializationException("There are negative amount of stones on pit "+ pit +". Fix the initialization board and retry.");
      if (initialBoard[pit].equals(0) && (++emptyPits > 2 || (!pit.equals(p1Base) && !pit.equals(p2Base))))
        throw new BoardInitializationException("The board should have only two empty pits at the right of each player.");
    }
  }

  public Game moveStonesFrom(Integer pit) throws BoardMoveException {
    validateMoveFrom(pit);

    var stones = game.getBoard().pits()[pit];
    Integer lastModifiedPitStoneCount = null;
    game.getBoard().pits()[pit] = 0;
    while (stones > 0) {
      ++pit;
      if ((game.getPlayer().equals(ONE) && pit.equals(playerTwoBase)) || (game.getPlayer().equals(TWO) && pit.equals(playerOneBase)))
        ++pit;
      if (pit >= game.getBoard().pits().length) pit = 0;
      stones--;
      lastModifiedPitStoneCount = game.getBoard().pits()[pit];
      game.getBoard().pits()[pit]++;
    }

    captureStonesIfApplicable(pit, lastModifiedPitStoneCount);
    rotatePlayerIfApplicable(pit);
    return game;
  }

  private void validateMoveFrom(Integer pit) throws BoardMoveException {
    if (game == null)
      throw new BoardMoveException("The board has not been initialized yet.");
    if (game.getStatus().equals(DONE))
      throw new BoardMoveException("Game has ended. Player " + game.getPlayer() + " won.");
    if ((game.getPlayer().equals(ONE) && pit.equals(playerOneBase)) || (game.getPlayer().equals(TWO) && pit.equals(playerTwoBase)))
      throw new BoardMoveException("The stones at the base should not be moved.");
    if ((game.getPlayer().equals(ONE) && pit > playerOneBase) || (game.getPlayer().equals(TWO) && pit <= playerOneBase))
      throw new BoardMoveException("Those stones are not yours to move.");
    if (game.getBoard().pits()[pit].equals(0))
      throw new BoardMoveException("Choose a pit with stones.");
  }

  private void captureStonesIfApplicable(Integer pit, Integer lastModifiedPitStoneCount) {
    if (!pit.equals(playerOneBase) && !pit.equals(playerTwoBase) &&
      lastModifiedPitStoneCount != null && lastModifiedPitStoneCount.equals(0) &&
      ((game.getPlayer().equals(ONE) && pit < playerOneBase) || (game.getPlayer().equals(TWO) && pit > playerOneBase))
    ) {
      var capturedPit = switch (game.getPlayer()) {
        // oh, math... Y U DO BE LIKE DAT?!
        case ONE -> pit + playerOneBase + 1;
        case TWO -> pit - playerOneBase - 1;
      };
      var capturedStones = game.getBoard().pits()[capturedPit] + game.getBoard().pits()[pit];
      game.getBoard().pits()[pit] = 0;
      game.getBoard().pits()[capturedPit] = 0;
      switch (game.getPlayer()) {
        case ONE -> game.getBoard().pits()[playerOneBase] += capturedStones;
        case TWO -> game.getBoard().pits()[playerTwoBase] += capturedStones;
      }
    }
  }

  private void rotatePlayerIfApplicable(Integer pit) {
    var stonesOnPlayerOnePits = 0;
    var stonesOnPlayerTwoPits = 0;
    for (int i = 0; i < playerOneBase; i++) {
      stonesOnPlayerOnePits += game.getBoard().pits()[i];
      stonesOnPlayerTwoPits += game.getBoard().pits()[i + playerOneBase + 1];
    }
    if (stonesOnPlayerOnePits == 0 || stonesOnPlayerTwoPits == 0) {
      game.setStatus(DONE);
    } else {
      if ((game.getPlayer().equals(ONE) && !pit.equals(playerOneBase)) ||
        (game.getPlayer().equals(TWO) && !pit.equals(playerTwoBase))) {
        game.setPlayer(
          switch (game.getPlayer()) {
            case ONE -> TWO;
            case TWO -> ONE;
          }
        );
      }
    }
  }

  public String gameStatus() {
    if (game == null)
      throw new BoardInitializationException("The board has not been initialized yet.");
    final var gameStatus = game.getStatus().equals(PLAYABLE)? "Current" : "Final";
    final var playerOneScore = game.getBoard().pits()[playerOneBase];
    final var playerTwoScore = game.getBoard().pits()[playerTwoBase];
    StringBuilder playerOneBoard = new StringBuilder();
    StringBuilder playerTwoBoard = new StringBuilder();
    for (int i = 0; i < playerOneBase; i++) {
      playerOneBoard.append("| %d ".formatted(game.getBoard().pits()[i]));
      playerTwoBoard.append("| %d ".formatted(game.getBoard().pits()[i + playerOneBase + 1]));
    }
    playerOneBoard.append("|| %d |".formatted(game.getBoard().pits()[playerOneBase]));
    playerTwoBoard.append("|| %d |".formatted(game.getBoard().pits()[playerTwoBase]));
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
      game.getPlayer(),
      game.getStatus()
    );
  }
}
