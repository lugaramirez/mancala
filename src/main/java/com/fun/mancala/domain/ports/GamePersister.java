package com.fun.mancala.domain.ports;

import com.fun.mancala.domain.models.Game;

public interface GamePersister {
  boolean persist(Game gameState);
}
