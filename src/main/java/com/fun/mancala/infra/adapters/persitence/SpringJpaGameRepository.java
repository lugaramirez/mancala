package com.fun.mancala.infra.adapters.persitence;

import com.fun.mancala.infra.adapters.persitence.entities.Game;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface SpringJpaGameRepository extends CrudRepository<Game, UUID> {

}
