package com.fun.mancala.application;


import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


class BoardManagerTest {
    final private BoardManager sut = new BoardManager();

    @Test
    void initial_state_of_board_is_14_pits_with_6_pits_with_6_seeds_and_1_empty_pit_twice() {
        final var board = sut.initialize();
        assertThat(board.pits()).isInstanceOf(Integer[].class);
        assertThat(board.pits()).hasSize(14);
        assertThat(board.pits()).containsExactly(
                6, 6, 6, 6, 6, 6, 0,
                6, 6, 6, 6, 6, 6, 0
        );
        assertThat(board.stones()).isEqualTo(72);
    }
}
