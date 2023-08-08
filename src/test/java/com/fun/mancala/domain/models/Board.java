package com.fun.mancala.domain.models;

public class Board {

    final private Integer[] pits = new Integer[] { 6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0 };

    public Integer[] pits() {
        return pits;
    }

    public Integer stones() {
        return 72;
    }
}
