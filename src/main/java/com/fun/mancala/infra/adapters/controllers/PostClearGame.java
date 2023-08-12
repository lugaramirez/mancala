package com.fun.mancala.infra.adapters.controllers;

import com.fun.mancala.application.GameManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PostClearGame {
  private final GameManager gameManager;

  public PostClearGame(GameManager gameManager) {
    this.gameManager = gameManager;
  }

  @PostMapping("/clear")
  public ResponseEntity<Void> postClearGame() {
    gameManager.clearGame();
    return ResponseEntity.ok().build();
  }
}
