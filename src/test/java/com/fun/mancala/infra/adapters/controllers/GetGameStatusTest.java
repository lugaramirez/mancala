package com.fun.mancala.infra.adapters.controllers;

import com.fun.mancala.application.GameManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class GetGameStatusTest {
  private GetGameStatus sut;

  private final GameManager game = new GameManager();

  @BeforeEach
  void initializeTest() {
    game.clearGame();
    game.initialize(new Integer[]{1, 1, 0, 1, 1, 0});
    sut = new GetGameStatus(game);
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
    game.moveStonesFrom(0);

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
    game.moveStonesFrom(1);
    game.moveStonesFrom(0);

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
    game.moveStonesFrom(0);
    game.moveStonesFrom(4);
    game.moveStonesFrom(3);

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
