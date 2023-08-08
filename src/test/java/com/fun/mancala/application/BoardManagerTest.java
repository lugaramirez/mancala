package com.fun.mancala.application;


import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


class BoardManagerTest {
    final private BoardManager sut = new BoardManager();

    @Test
    void initial_state_of_board_is_correctly_passed() {
        final var initialization = new Integer[] { 6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0 };

        final var board = sut.initialize(initialization);

        assertThat(board.pits()).isInstanceOf(Integer[].class);
        assertThat(board.pits()).hasSize(initialization.length);
        assertThat(board.pits()).containsExactly(initialization);
    }

    @Test
    void move_pits_from_first_pit_changes_board() {
        final var initialization = new Integer[] { 2, 2, 0, 2, 2, 0 };
        final var result = new Integer[] { 0, 3, 1, 2, 2, 0 };

        sut.initialize(initialization);

        final var moved = sut.move(0);

        assertThat(moved.pits()).containsExactly(result);
    }

    @Test
    void move_pits_from_last_player_pit_changes_board_and_rotates_to_first_player_pit() {
        final var initialization = new Integer[] { 2, 2, 0, 2, 2, 0 };
        final var result = new Integer[] { 3, 2, 0, 2, 0, 1 };

        sut.initialize(initialization);

        final var moved = sut.move(4);

        assertThat(moved.pits()).containsExactly(result);
    }
}
