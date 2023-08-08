package com.fun.mancala.application;

import com.fun.mancala.domain.models.Board;

import java.util.Arrays;

public class BoardManager {

    private Board board;

    public Board initialize(Integer[] initialBoard) {
        this.board = new Board(initialBoard);
        return this.board;
    }

    public Board move(Integer fromPit) {
        var pit = fromPit+1;
        while (board.pits()[fromPit] > 0) {
            board.pits()[fromPit]--;
            board.pits()[pit]++;
            if (++pit >= board.pits().length) pit = 0;
        }
        return board;
    }
}
