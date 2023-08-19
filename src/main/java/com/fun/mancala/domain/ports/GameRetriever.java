package com.fun.mancala.domain.ports;

import com.fun.mancala.domain.models.Game;

import java.util.UUID;

public interface GameRetriever {
  Game.State retrieveById(UUID id);
}
