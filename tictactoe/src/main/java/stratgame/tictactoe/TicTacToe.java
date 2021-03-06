package stratgame.tictactoe;

import stratgame.game.Game;
import stratgame.game.Player;
import stratgame.game.State;

/**
 * {@link Game} that manages the lifecycle of a Tic-tac-toe match.
 */
public final class TicTacToe implements Game<Integer> {

  /**
   * The {@code Player} that moves on the first and every alternate turn.
   */
  private final Player<Integer> x;

  /**
   * The {@code Player} that moves on the second and every alternate turn.
   */
  private final Player<Integer> o;

  /**
   * Internal {@link TTTState}.
   */
  private final TTTState state;

  /**
   * Initializes a blank {@code TicTacToe} instance.
   */
  public TicTacToe(Player<Integer> x, Player<Integer> o) {
    this.x = x;
    this.o = o;
    this.state = new TTTState();
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
    return new TTTState(this.state);
  }

  @Override
  public void start() {
    await();
  }

  public Piece winner() {
    return state.winner();
  }
}
