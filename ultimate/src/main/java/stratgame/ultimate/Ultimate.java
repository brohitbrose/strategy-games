package stratgame.ultimate;

import stratgame.game.Game;
import stratgame.game.Player;
import stratgame.game.State;
import stratgame.tictactoe.Piece;

/**
 * {@link Game} that manages the lifecycle of an ultimate tic-tac-toe match.
 */
public class Ultimate implements Game<Integer> {

  /**
   * The {@code Player} that moves on the first and every alternate turn.
   */
  private final Player<Integer> x;

  /**
   * The {@code Player} that moves on the second and every alternate turn.
   */
  private final Player<Integer> o;

  /**
   * Internal {@link UltimateState}.
   */
  private final UltimateState state;

  /**
   * Initializes a blank {@code TicTacToe} instance.
   */
  public Ultimate(Player<Integer> x, Player<Integer> o) {
    this.x = x;
    this.o = o;
    this.state = new UltimateState();
  }

  @Override
  public Player<Integer> current() {
    return (state.movesMade() & 1) == 0 ? x : o;
  }

  @Override
  public State<Integer> state() {
    return state;
  }

  @Override
  public State<Integer> snapshot() {
    return new UltimateState(state);
  }

  @Override
  public void start() {
    await();
  }

  public Piece winner() {
    return state.winner();
  }
}
