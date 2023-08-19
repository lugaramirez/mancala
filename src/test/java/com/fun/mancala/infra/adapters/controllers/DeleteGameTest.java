package com.fun.mancala.infra.adapters.controllers;

import com.fun.mancala.application.GameManager;
import com.fun.mancala.infra.adapters.persitence.GameSpringRepository;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class DeleteGameTest {
  private final GameSpringRepository db = mock();
  private final GameManager gameManager = new GameManager(db, db);
  private final DeleteGame sut = new DeleteGame(gameManager);

  @Test
  void initialize_board_correctly_returns_game_status() {
    assertThat(sut.deleteGame()).isEqualTo(ResponseEntity.noContent().build());
  }
}
