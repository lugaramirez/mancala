package com.fun.mancala.infra.adapters.controllers;

import com.fun.mancala.application.GameManager;
import com.fun.mancala.application.exceptions.BoardMoveException;
import com.fun.mancala.infra.adapters.persitence.GameSpringRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PostGameMovementTest {
  private final GameSpringRepository db = mock();
  private final GameManager gameManager = new GameManager(db, db);
  private PostGameMovement sut;

  @BeforeEach
  void initializeTest() {
    when(db.persist(any())).thenReturn(true);
    gameManager.initialize(new Integer[]{1, 1, 0, 1, 1, 0});
    this.sut = new PostGameMovement(gameManager);
  }

  @Test
  void player_one_should_play_first() {
    final var response = sut.postGameMovement(0);

    assertThat(response).isEqualTo(ResponseEntity.ok("""
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
  void player_one_cannot_move_player_two_stones() {
    assertThatThrownBy(() -> sut.postGameMovement(4))
      .isInstanceOf(BoardMoveException.class)
      .hasMessage("Those stones are not yours to move.");
  }

  @Test
  void player_one_cannot_move_stones_from_base() {
    assertThatThrownBy(() -> sut.postGameMovement(2))
      .isInstanceOf(BoardMoveException.class)
      .hasMessage("The stones at the base should not be moved.");
  }

  @Test
  void player_two_should_play_second() {
    sut.postGameMovement(0);
    final var response = sut.postGameMovement(3);

    assertThat(response).isEqualTo(ResponseEntity.ok("""
      Current Board:
        Player ONE: | 0 | 2 || 0 |
        Player TWO: | 0 | 2 || 0 |
      Current Score:
        Player ONE: 0
        Player TWO: 0
      Current Player: ONE
      Game: PLAYABLE
      """));
  }

  @Test
  void player_two_cannot_play_first() {
    assertThatThrownBy(() -> sut.postGameMovement(3))
      .isInstanceOf(BoardMoveException.class)
      .hasMessage("Those stones are not yours to move.");
  }

  @Test
  void player_two_cannot_move_player_one_stones() {
    sut.postGameMovement(0);
    assertThatThrownBy(() -> sut.postGameMovement(1))
      .isInstanceOf(BoardMoveException.class)
      .hasMessage("Those stones are not yours to move.");
  }

  @Test
  void player_two_cannot_move_stones_from_base() {
    sut.postGameMovement(0);
    assertThatThrownBy(() -> sut.postGameMovement(5))
      .isInstanceOf(BoardMoveException.class)
      .hasMessage("The stones at the base should not be moved.");
  }

  @Test
  void game_status_done_player_one_wins() {
    sut.postGameMovement(1);
    final var response = sut.postGameMovement(0);

    assertThat(response).isEqualTo(ResponseEntity.ok("""
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
    sut.postGameMovement(0);
    sut.postGameMovement(4);
    final var response = sut.postGameMovement(3);

    assertThat(response).isEqualTo(ResponseEntity.ok("""
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

  @Test
  void after_game_finished_throw_exception() {
    sut.postGameMovement(1);
    sut.postGameMovement(0);

    assertThatThrownBy(() -> sut.postGameMovement(2))
      .isInstanceOf(BoardMoveException.class)
      .hasMessage("Game has ended. Player ONE won.");
  }
}
