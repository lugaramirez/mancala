package com.fun.mancala.infra.adapters.persitence;

import com.fun.mancala.domain.models.Board;
import com.fun.mancala.domain.models.Game;
import com.fun.mancala.domain.ports.GamePersister;
import com.fun.mancala.domain.ports.GameRetriever;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.UUID;

@Component
public class GameSpringRepository implements GameRetriever, GamePersister {
  private final SpringJpaGameRepository gameRepository;

  public GameSpringRepository(SpringJpaGameRepository gameRepository) {
    this.gameRepository = gameRepository;
  }

  @Override
  public boolean persist(Game gameState) {
    var gameEntity = new com.fun.mancala.infra.adapters.persitence.entities.Game(
      gameState.getId(),
      Arrays.asList(gameState.getBoard().pits()),
      gameState.getPlayer(),
      gameState.getStatus()
    );
    gameRepository.save(gameEntity);
    return true;
  }

  @Override
  public Game.State retrieveById(UUID id) {
    var gameEntity = gameRepository.findById(id).orElseThrow();
    return new Game.State(
      gameEntity.id(),
      new Board(gameEntity.board().toArray(new Integer[0])),
      gameEntity.player(),
      gameEntity.status()
    );
  }
}
