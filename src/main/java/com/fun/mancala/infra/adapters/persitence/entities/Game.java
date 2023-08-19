package com.fun.mancala.infra.adapters.persitence.entities;

import com.fun.mancala.domain.models.Player;
import com.fun.mancala.domain.models.Status;
import jakarta.persistence.*;

import java.util.List;
import java.util.UUID;

@Entity
public final class Game {
    @Id
    private final UUID id;
    @ElementCollection
    private final List<Integer> board;
    private final Player player;
    private final Status status;

    public Game(UUID id, List<Integer> board, Player player, Status status) {
        this.id = id;
        this.board = board;
        this.player = player;
        this.status = status;
    }

    public Game() {
        this(null, null, null, null);
    }

    public UUID id() {
        return id;
    }

    public List<Integer> board() {
        return board;
    }

    public Player player() {
        return player;
    }

    public Status status() {
      return status;
    }

}
