package com.fun.mancala.infra.adapters.controllers;

import com.fun.mancala.application.GameManager;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class PostClearGameTest {
  private final PostClearGame sut = new PostClearGame(new GameManager());

  @Test
  void initialize_board_correctly_returns_game_status() {
    assertThat(sut.postClearGame()).isEqualTo(ResponseEntity.ok().build());
  }
}
