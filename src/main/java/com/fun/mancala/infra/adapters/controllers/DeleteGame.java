package com.fun.mancala.infra.adapters.controllers;

import com.fun.mancala.application.GameManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DeleteGame {
  private final GameManager gameManager;

  public DeleteGame(GameManager gameManager) {
    this.gameManager = gameManager;
  }

  @DeleteMapping()
  public ResponseEntity<Void> deleteGame() {
    gameManager.clearGame();
    return ResponseEntity.noContent().build();
  }
}
