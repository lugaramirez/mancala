package com.fun.mancala.infra.adapters.controllers;

import com.fun.mancala.application.GameManager;
import com.fun.mancala.domain.ports.GamePersister;
import com.fun.mancala.domain.ports.GameRetriever;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class DeleteGameTest {
  @Mock
  private GamePersister persister;
  @Mock
  private GameRetriever retriever;
  private final GameManager gameManager = new GameManager(retriever, persister);
  private final DeleteGame sut = new DeleteGame(gameManager);

  @Test
  void initialize_board_correctly_returns_game_status() {
    assertThat(sut.deleteGame()).isEqualTo(ResponseEntity.noContent().build());
  }
}
