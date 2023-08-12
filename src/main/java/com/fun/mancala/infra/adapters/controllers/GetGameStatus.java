package com.fun.mancala.infra.adapters.controllers;

import com.fun.mancala.application.GameManager;
import com.fun.mancala.application.exceptions.BoardInitializationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GetGameStatus {
  private final GameManager gameManager;

  public GetGameStatus(GameManager gameManager) {
    this.gameManager = gameManager;
  }

  @GetMapping("/status")
  public ResponseEntity<String> getGameStatus() throws BoardInitializationException {
    return ResponseEntity.ok(gameManager.gameStatus());
  }
}
