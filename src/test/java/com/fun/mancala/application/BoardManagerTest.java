package com.fun.mancala.application;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class BoardManagerTest {
  final private BoardManager sut = new BoardManager();

  @Nested
  class BoardInitialization {
    @Test
    void initial_state_of_board_is_correctly_passed() {
      final var initialization = new Integer[]{6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0};

      final var board = sut.initialize(initialization);

      assertThat(board.pits()).isInstanceOf(Integer[].class);
      assertThat(board.pits()).hasSize(initialization.length);
      assertThat(board.pits()).containsExactly(initialization);
    }

    @Test
    void initializing_board_with_incorrect_configuration_throws_exception() {
      final var oddNumberOfPits = new Integer[]{0, 0, 0};
      final var boardTooSmall = new Integer[]{1, 0, 1, 0};
      final var wrongAmountOfEmptyPits = new Integer[]{1, 1, 0, 1, 0, 0};
      final var badlyPositionedEmptyPits = new Integer[]{1, 1, 0, 0, 1, 1};
      final var goodBoard = new Integer[]{1, 1, 0, 1, 1, 0};

      assertThatThrownBy(() -> sut.initialize(oddNumberOfPits))
        .isInstanceOf(BoardInitializationException.class)
        .hasMessage("The amount of pits on board should be even.");
      assertThatThrownBy(() -> sut.initialize(boardTooSmall))
        .isInstanceOf(BoardInitializationException.class)
        .hasMessage("The board should have at least three pits per player.");
      assertThatThrownBy(() -> sut.initialize(wrongAmountOfEmptyPits))
        .isInstanceOf(BoardInitializationException.class)
        .hasMessage("The board should have only two empty pits at the right of each player.");
      assertThatThrownBy(() -> sut.initialize(badlyPositionedEmptyPits))
        .isInstanceOf(BoardInitializationException.class)
        .hasMessage("The board should have only two empty pits at the right of each player.");
      assertThatThrownBy(() -> {
        sut.initialize(goodBoard);
        sut.initialize(goodBoard);
      })
        .isInstanceOf(BoardInitializationException.class)
        .hasMessage("The board is already initialized.");
    }
  }

  @Nested
  class PlayerOnePlays {
    @Test
    void player_one_should_play_first() {
      final var initialization = new Integer[]{2, 2, 0, 2, 2, 0};
      final var result = new Integer[]{0, 3, 1, 2, 2, 0};

      sut.initialize(initialization);
      final var moved = sut.moveStonesFrom(0);

      assertThat(moved.pits()).containsExactly(result);
    }

    @Test
    void player_one_cannot_move_player_two_stones() {
      final var initialization = new Integer[]{2, 2, 0, 2, 2, 0};

      sut.initialize(initialization);
      assertThatThrownBy(() -> sut.moveStonesFrom(4))
        .isInstanceOf(BoardMoveException.class)
        .hasMessage("Those stones are not yours to move.");
    }

    @Test
    void player_one_cannot_move_stones_from_base() {
      final var initialization = new Integer[]{2, 2, 0, 2, 2, 0};

      sut.initialize(initialization);
      assertThatThrownBy(() -> sut.moveStonesFrom(2))
        .isInstanceOf(BoardMoveException.class)
        .hasMessage("The stones at the base should not be moved.");
    }
  }

  @Nested
  class PlayerTwoPlays {
    @Test
    void player_two_cannot_play_first() {
      final var initialization = new Integer[]{2, 2, 0, 2, 2, 0};

      sut.initialize(initialization);

      assertThatThrownBy(() -> sut.moveStonesFrom(3))
        .isInstanceOf(BoardMoveException.class)
        .hasMessage("Those stones are not yours to move.");
    }

    @Test
    void player_two_cannot_move_player_one_stones() {
      final var initialization = new Integer[]{2, 2, 0, 2, 2, 0};

      sut.initialize(initialization);
      sut.moveStonesFrom(1);
      assertThatThrownBy(() -> sut.moveStonesFrom(1))
        .isInstanceOf(BoardMoveException.class)
        .hasMessage("Those stones are not yours to move.");
    }

    @Test
    void player_two_cannot_move_stones_from_base() {
      final var initialization = new Integer[]{2, 2, 0, 2, 2, 0};

      sut.initialize(initialization);
      sut.moveStonesFrom(1);
      assertThatThrownBy(() -> sut.moveStonesFrom(5))
        .isInstanceOf(BoardMoveException.class)
        .hasMessage("The stones at the base should not be moved.");
    }
  }

  @Nested
  class ComplexMovement {
    @Test
    void moving_stones_from_last_pit_rotates_to_first_player_pit() {
      final var initialization = new Integer[]{2, 2, 0, 2, 2, 0};
      final var result = new Integer[]{1, 3, 1, 2, 0, 1};

      sut.initialize(initialization);
      sut.moveStonesFrom(0);
      final var moved = sut.moveStonesFrom(4);

      assertThat(moved.pits()).containsExactly(result);
    }

    @Test
    void moving_stones_from_player_one_pit_skips_second_player_base_pit_and_rotates() {
      final var initialization = new Integer[] { 2, 4, 0, 2, 2, 0 };
      final var result = new Integer[] { 3, 0, 1, 3, 3, 0 };

      sut.initialize(initialization);
      final var moved = sut.moveStonesFrom(1);

      assertThat(moved.pits()).containsExactly(result);
    }

    @Test
    void landing_on_own_empty_pit_captures_opponents_stones_for_player_one() {
        final var initialization = new Integer[] { 2, 5, 0, 2, 2, 0 };
        final var result = new Integer[] { 3, 1, 4, 3, 0, 0 };

        sut.initialize(initialization);
        final var moved = sut.moveStonesFrom(1);

        assertThat(moved.pits()).containsExactly(result);
    }

    @Test
    void landing_on_own_empty_pit_captures_opponents_stones_for_player_two() {
      final var initialization = new Integer[] { 2, 2, 0, 2, 5, 0 };
      // 1 { 2, 0, 1, 3, 5, 0 }
      // 4 { 3, 0, 1, 4, 1, 2 }
      final var result = new Integer[] { 3, 0, 1, 4, 1, 2 };

      sut.initialize(initialization);
      sut.moveStonesFrom(1);
      final var moved = sut.moveStonesFrom(4);

      assertThat(moved.pits()).containsExactly(result);
    }

    @Test
    void landing_on_opposing_empty_pit_does_not_capture() {
      final var initialization = new Integer[] { 3, 3, 0, 3, 3, 0 };
      final var result = new Integer[] { 4, 1, 1, 0, 5, 1 };

      sut.initialize(initialization);
      sut.moveStonesFrom(1);
      final var moved = sut.moveStonesFrom(3);

      assertThat(moved.pits()).containsExactly(result);
    }

    @Test
    //this should actually repeat turn!
    void moving_stones_from_player_two_pit_skips_second_player_base_pit_and_rotates() {
      final var initialization = new Integer[] { 2, 2, 0, 2, 4, 0 };
      final var result = new Integer[] { 3, 1, 1, 4, 0, 1 };

      sut.initialize(initialization);
      sut.moveStonesFrom(1);
      final var moved = sut.moveStonesFrom(4);

      assertThat(moved.pits()).containsExactly(result);
    }
  }
}
