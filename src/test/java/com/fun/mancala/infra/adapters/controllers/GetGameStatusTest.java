package com.fun.mancala.infra.adapters.controllers;

import com.fun.mancala.application.GameManager;
import com.fun.mancala.application.exceptions.BoardInitializationException;
import com.fun.mancala.infra.adapters.persitence.GameSpringRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GetGameStatusTest {
  private final GameSpringRepository db = mock();
  private final GameManager gameManager = new GameManager(db, db);
  private GetGameStatus sut;

  @BeforeEach
  void initializeTest() {
    when(db.persist(any())).thenReturn(true);
    gameManager.clearGame();
    gameManager.initialize(new Integer[]{1, 1, 0, 1, 1, 0});
    sut = new GetGameStatus(gameManager);
  }

  @Test
  void uninitialized_board_throws_exception() {
    gameManager.clearGame();
    assertThatThrownBy(() -> sut.getGameStatus())
      .isInstanceOf(BoardInitializationException.class)
      .hasMessage("The board has not been initialized yet.");
  }

  @Test
  void player_one_should_play_first() {
    assertThat(sut.getGameStatus()).isEqualTo(ResponseEntity.ok("""
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
  void player_two_should_play_second() {
    gameManager.moveStonesFrom(0);

    assertThat(sut.getGameStatus()).isEqualTo(ResponseEntity.ok("""
      Current Board:
        Player ONE: | 0 | 2 || 0 |
        Player TWO: | 1 | 1 || 0 |
      Current Score:
        Player ONE: 0
        Player TWO: 0
      Current Player: TWO
      Game: PLAYABLE
      """));
  }

  @Test
  void game_status_done_player_one_wins() {
    gameManager.moveStonesFrom(1);
    gameManager.moveStonesFrom(0);

    assertThat(sut.getGameStatus()).isEqualTo(ResponseEntity.ok("""
        Final Board:
          Player ONE: | 0 | 0 || 3 |
          Player TWO: | 1 | 0 || 0 |
        Final Score:
          Player ONE: 3
          Player TWO: 0
        Final Player: ONE
        Game: DONE
        """));
  }

  @Test
  void game_status_done_player_two_wins() {
    gameManager.moveStonesFrom(0);
    gameManager.moveStonesFrom(4);
    gameManager.moveStonesFrom(3);

    assertThat(sut.getGameStatus()).isEqualTo(ResponseEntity.ok("""
        Final Board:
          Player ONE: | 0 | 0 || 0 |
          Player TWO: | 0 | 0 || 4 |
        Final Score:
          Player ONE: 0
          Player TWO: 4
        Final Player: TWO
        Game: DONE
        """));
  }
}
