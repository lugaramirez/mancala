package com.fun.mancala.infra.adapters.controllers;

import com.fun.mancala.application.GameManager;
import com.fun.mancala.application.exceptions.BoardInitializationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PostGameInitialization {
  private final GameManager gameManager;

  public PostGameInitialization(GameManager gameManager) {
    this.gameManager = gameManager;
  }

  @PostMapping("/initialize")
  public ResponseEntity<String> postGameInitialization(@RequestBody Integer[] initialBoard) throws BoardInitializationException {
    gameManager.initialize(initialBoard);
    return ResponseEntity.ok(gameManager.gameStatus());
  }
}
