package com.fun.mancala.infra.adapters.controllers;

import com.fun.mancala.application.GameManager;
import com.fun.mancala.application.exceptions.BoardInitializationException;
import com.fun.mancala.infra.adapters.persitence.GameSpringRepository;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PostGameInitializationTest {
  private final GameSpringRepository db = mock();
  private final GameManager gameManager = new GameManager(db, db);
  private final PostGameInitialization sut = new PostGameInitialization(gameManager);

  @Test
  void initialize_board_correctly_returns_game_status() {
    final var board = new Integer[]{1, 1, 0, 1, 1, 0};
    when(db.persist(any())).thenReturn(true);

    final var response = sut.postGameInitialization(board);

    assertThat(response).isEqualTo(ResponseEntity.ok("""
      Current Board:
        Player ONE: | 1 | 1 || 0 |
        Player TWO: | 1 | 1 || 0 |
      Current Score:
        Player ONE: 0
        Player TWO: 0
      Current Player: ONE
      Game: PLAYABLE
      """));
  }

  @Test
  void initialize_board_incorrectly_throws_exceptions() {
    final var oddNumberOfPits = new Integer[]{0, 0, 0};
    final var boardTooSmall = new Integer[]{1, 0, 1, 0};
    final var wrongAmountOfEmptyPits = new Integer[]{1, 1, 0, 1, 0, 0};
    final var badlyPositionedEmptyPits = new Integer[]{1, 1, 0, 0, 1, 1};
    final var goodBoard = new Integer[]{1, 1, 0, 1, 1, 0};
    when(db.persist(any())).thenReturn(true);

    assertThatThrownBy(() -> sut.postGameInitialization(null))
      .isInstanceOf(BoardInitializationException.class)
      .hasMessage("Provide an initial state to the board.");
    assertThatThrownBy(() -> sut.postGameInitialization(oddNumberOfPits))
      .isInstanceOf(BoardInitializationException.class)
      .hasMessage("The amount of pits on board should be even.");
    assertThatThrownBy(() -> sut.postGameInitialization(boardTooSmall))
      .isInstanceOf(BoardInitializationException.class)
      .hasMessage("The board should have at least two pits plus a base per player.");
    assertThatThrownBy(() -> sut.postGameInitialization(wrongAmountOfEmptyPits))
      .isInstanceOf(BoardInitializationException.class)
      .hasMessage("The board should have only two empty pits at the right of each player.");
    assertThatThrownBy(() -> sut.postGameInitialization(badlyPositionedEmptyPits))
      .isInstanceOf(BoardInitializationException.class)
      .hasMessage("The board should have only two empty pits at the right of each player.");
    assertThatThrownBy(() -> {
      sut.postGameInitialization(goodBoard);
      sut.postGameInitialization(goodBoard);
    })
      .isInstanceOf(BoardInitializationException.class)
      .hasMessage("The board is already initialized.");
  }
}
