package com.fun.mancala.domain.models;

import java.util.Arrays;

public record Board(Integer[] pits) {
    // overridden methods added because sonar...
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Board board = (Board) o;
        return Arrays.equals(pits, board.pits);
    }

    @Override
    public int hashCode() {
        return 31 + Arrays.hashCode(pits);
    }

    @Override
    public String toString() {
        return "Board{" +
                "pits=" + Arrays.toString(pits) +
                '}';
    }
}
