package com.fun.mancala.infra.adapters.controllers;

import com.fun.mancala.application.GameManager;
import com.fun.mancala.application.exceptions.BoardMoveException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PostGameMovement {
  private final GameManager gameManager;

  public PostGameMovement(GameManager gameManager) {
    this.gameManager = gameManager;
  }

  @PostMapping("/move")
  public ResponseEntity<String> postGameMovement(@RequestBody Integer pit) throws BoardMoveException {
    gameManager.moveStonesFrom(pit);
    return ResponseEntity.ok(gameManager.gameStatus());
  }
}
