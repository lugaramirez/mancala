package com.fun.mancala.application;

import com.fun.mancala.application.exceptions.BoardInitializationException;
import com.fun.mancala.application.exceptions.BoardMoveException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GameManagerTest {
  private final GameManager sut = new GameManager();

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
      final var tooManyStones = new Integer[]{1, 20, 0, 1, 1, 0};
      final var negativeStones = new Integer[]{-1, 20, 0, 1, 1, 0};
      final var goodBoard = new Integer[]{1, 1, 0, 1, 1, 0};

      assertThatThrownBy(() -> sut.initialize(null))
        .isInstanceOf(BoardInitializationException.class)
        .hasMessage("Provide an initial state to the board.");
      assertThatThrownBy(() -> sut.initialize(oddNumberOfPits))
        .isInstanceOf(BoardInitializationException.class)
        .hasMessage("The amount of pits on board should be even.");
      assertThatThrownBy(() -> sut.initialize(boardTooSmall))
        .isInstanceOf(BoardInitializationException.class)
        .hasMessage("The board should have at least two pits plus a base per player.");
      assertThatThrownBy(() -> sut.initialize(wrongAmountOfEmptyPits))
        .isInstanceOf(BoardInitializationException.class)
        .hasMessage("The board should have only two empty pits at the right of each player.");
      assertThatThrownBy(() -> sut.initialize(badlyPositionedEmptyPits))
        .isInstanceOf(BoardInitializationException.class)
        .hasMessage("The board should have only two empty pits at the right of each player.");
      assertThatThrownBy(() -> sut.initialize(tooManyStones))
        .isInstanceOf(BoardInitializationException.class)
        .hasMessage("There are too many stones on pit 1. The maximum amount of stones is 10. Fix the initialization board and retry.");
      assertThatThrownBy(() -> sut.initialize(negativeStones))
        .isInstanceOf(BoardInitializationException.class)
        .hasMessage("There are negative amount of stones on pit 0. Fix the initialization board and retry.");
      assertThatThrownBy(() -> {
        sut.initialize(goodBoard);
        sut.initialize(goodBoard);
      })
        .isInstanceOf(BoardInitializationException.class)
        .hasMessage("The board is already initialized.");
    }

    @Test
    void initialize_clear_and_initialize_works() {
      final var initialization = new Integer[]{6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0};

      sut.initialize(initialization);
      sut.clearGame();
      final var board = sut.initialize(initialization);

      assertThat(board.pits()).isInstanceOf(Integer[].class);
      assertThat(board.pits()).hasSize(initialization.length);
      assertThat(board.pits()).containsExactly(initialization);
    }

    @Test
    void movement_on_uninitialized_board_throws_exception() {
      assertThatThrownBy(() -> sut.moveStonesFrom(0))
        .isInstanceOf(BoardMoveException.class)
        .hasMessage("The board has not been initialized yet.");
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
    void player_one_plays_a_pit_without_stones() {
      final var initialization = new Integer[]{1, 1, 0, 1, 1, 0};

      sut.initialize(initialization);
      sut.moveStonesFrom(1);

      assertThatThrownBy(() -> sut.moveStonesFrom(1))
        .isInstanceOf(BoardMoveException.class)
        .hasMessage("Choose a pit with stones.");
    }

    @Test
    void player_two_plays_a_pit_without_stones() {
      final var initialization = new Integer[]{1, 1, 0, 1, 1, 0};

      sut.initialize(initialization);
      sut.moveStonesFrom(0);
      sut.moveStonesFrom(4);

      assertThatThrownBy(() -> sut.moveStonesFrom(4))
        .isInstanceOf(BoardMoveException.class)
        .hasMessage("Choose a pit with stones.");
    }

    @Test
    void moving_stones_from_last_pit_rotates_to_first_player_pit() {
      final var initialization = new Integer[]{2, 2, 0, 2, 2, 0};
      final var result = new Integer[]{3, 0, 1, 3, 0, 1};

      sut.initialize(initialization);
      sut.moveStonesFrom(1);
      final var moved = sut.moveStonesFrom(4);

      assertThat(moved.pits()).containsExactly(result);
    }

    @Test
    void landing_on_base_pit_should_repeat_turn_for_player_one() {
      final var initialization = new Integer[]{2, 2, 0, 2, 2, 0};

      sut.initialize(initialization);
      sut.moveStonesFrom(0);
      assertThatThrownBy(() -> sut.moveStonesFrom(4))
        .isInstanceOf(BoardMoveException.class)
        .hasMessage("Those stones are not yours to move.");
    }

    @Test
    void landing_on_base_pit_should_repeat_turn_for_player_two() {
      final var initialization = new Integer[]{2, 2, 0, 1, 2, 0};

      sut.initialize(initialization);
      sut.moveStonesFrom(1);
      sut.moveStonesFrom(3);
      assertThatThrownBy(() -> sut.moveStonesFrom(0))
        .isInstanceOf(BoardMoveException.class)
        .hasMessage("Those stones are not yours to move.");

    }

    @Test
    void landing_on_own_empty_pit_captures_opponents_stones_for_player_one() {
      final var initialization = new Integer[]{2, 5, 0, 2, 2, 0};
      final var result = new Integer[]{3, 0, 5, 3, 0, 0};

      sut.initialize(initialization);
      final var moved = sut.moveStonesFrom(1);

      assertThat(moved.pits()).containsExactly(result);
    }

    @Test
    void landing_on_own_empty_pit_captures_opponents_stones_for_player_two() {
      final var initialization = new Integer[]{2, 2, 0, 2, 5, 0};
      final var result = new Integer[]{3, 0, 1, 4, 0, 3};

      sut.initialize(initialization);
      sut.moveStonesFrom(1);
      final var moved = sut.moveStonesFrom(4);

      assertThat(moved.pits()).containsExactly(result);
    }

    @Test
    void landing_on_opposing_empty_pit_does_not_capture() {
      final var initialization = new Integer[]{3, 3, 0, 3, 3, 0};
      final var result = new Integer[]{4, 1, 1, 0, 5, 1};

      sut.initialize(initialization);
      sut.moveStonesFrom(1);
      final var moved = sut.moveStonesFrom(3);

      assertThat(moved.pits()).containsExactly(result);
    }

    @Test
    void moving_stones_from_player_one_pit_skips_second_player_base_pit_and_rotates() {
      final var initialization = new Integer[]{2, 4, 0, 2, 2, 0};
      final var result = new Integer[]{3, 0, 1, 3, 3, 0};

      sut.initialize(initialization);
      final var moved = sut.moveStonesFrom(1);

      assertThat(moved.pits()).containsExactly(result);
    }

    @Test
    void moving_stones_from_player_two_pit_rotates_and_skips_first_player_base_pit() {
      final var initialization = new Integer[]{2, 2, 0, 2, 4, 0};
      final var result = new Integer[]{3, 1, 1, 4, 0, 1};

      sut.initialize(initialization);
      sut.moveStonesFrom(1);
      final var moved = sut.moveStonesFrom(4);

      assertThat(moved.pits()).containsExactly(result);
    }
  }

  @Nested
  class EndGameCondition {
    @Test
    void player_one_wins() {
      final var initialization = new Integer[]{2, 1, 0, 1, 1, 0};
      final var result = new Integer[]{0, 0, 3, 1, 1, 0};

      sut.initialize(initialization);
      sut.moveStonesFrom(1);
      sut.moveStonesFrom(0);
      final var moved = sut.moveStonesFrom(1);

      assertThatThrownBy(() -> sut.moveStonesFrom(0))
        .isInstanceOf(BoardMoveException.class)
        .hasMessage("Game has ended. Player ONE won.");
      assertThat(moved.pits()).containsExactly(result);
    }

    @Test
    void player_two_wins() {
      final var initialization = new Integer[]{1, 2, 0, 1, 1, 0};
      final var result = new Integer[]{1, 0, 1, 0, 0, 3};

      sut.initialize(initialization);
      sut.moveStonesFrom(1);
      sut.moveStonesFrom(4);
      sut.moveStonesFrom(3);
      final var moved = sut.moveStonesFrom(4);

      assertThatThrownBy(() -> sut.moveStonesFrom(0))
        .isInstanceOf(BoardMoveException.class)
        .hasMessage("Game has ended. Player TWO won.");
      assertThat(moved.pits()).containsExactly(result);
    }
  }

  @Nested
  class ReportGameStatus {
    @Test
    void uninitialized_board_throws_exception() {
      assertThatThrownBy(() -> sut.gameStatus())
        .isInstanceOf(BoardInitializationException.class)
        .hasMessage("The board has not been initialized yet.");
    }

    @Test
    void game_status_playable_game_player_one_turn() {
      final var initialization = new Integer[]{1, 1, 0, 1, 1, 0};

      sut.initialize(initialization);
      sut.moveStonesFrom(1);

      assertThat(sut.gameStatus()).isEqualTo("""
        Current Board:
          Player ONE: | 1 | 0 || 1 |
          Player TWO: | 1 | 1 || 0 |
        Current Score:
          Player ONE: 1
          Player TWO: 0
        Current Player: ONE
        Game: PLAYABLE
        """);
    }

    @Test
    void game_status_playable_game_player_two_turn() {
      final var initialization = new Integer[]{1, 1, 0, 1, 1, 0};

      sut.initialize(initialization);
      sut.moveStonesFrom(0);

      assertThat(sut.gameStatus()).isEqualTo("""
        Current Board:
          Player ONE: | 0 | 2 || 0 |
          Player TWO: | 1 | 1 || 0 |
        Current Score:
          Player ONE: 0
          Player TWO: 0
        Current Player: TWO
        Game: PLAYABLE
        """);
    }

    @Test
    void game_status_done_player_one_wins() {
      final var initialization = new Integer[]{2, 1, 0, 1, 1, 0};

      sut.initialize(initialization);
      sut.moveStonesFrom(1);
      sut.moveStonesFrom(0);
      sut.moveStonesFrom(1);

      assertThat(sut.gameStatus()).isEqualTo("""
        Final Board:
          Player ONE: | 0 | 0 || 3 |
          Player TWO: | 1 | 1 || 0 |
        Final Score:
          Player ONE: 3
          Player TWO: 0
        Final Player: ONE
        Game: DONE
        """);
    }

    @Test
    void game_status_done_player_two_wins() {
      final var initialization = new Integer[]{1, 2, 0, 1, 1, 0};

      sut.initialize(initialization);
      sut.moveStonesFrom(1);
      sut.moveStonesFrom(4);
      sut.moveStonesFrom(3);
      sut.moveStonesFrom(4);

      assertThat(sut.gameStatus()).isEqualTo("""
        Final Board:
          Player ONE: | 1 | 0 || 1 |
          Player TWO: | 0 | 0 || 3 |
        Final Score:
          Player ONE: 1
          Player TWO: 3
        Final Player: TWO
        Game: DONE
        """);
    }
  }
}
