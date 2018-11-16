package game.tictactoe;

import game.Game;
import game.Player;
import game.State;

public final class TicTacToe implements Game<Integer> {

  private final TTTPlayer x;
  private final TTTPlayer o;
  private final TTTState state;

  public TicTacToe(TTTPlayer x, TTTPlayer o) {
    this.x = x;
    this.o = o;
    this.state = new TTTState();
  }

  public TicTacToe(TicTacToe t) {
    this.x = t.x;
    this.o = t.o;
    this.state = t.state;
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
}
