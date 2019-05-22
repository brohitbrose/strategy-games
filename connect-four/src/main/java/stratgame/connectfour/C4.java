package stratgame.connectfour;

import stratgame.game.Game;
import stratgame.game.Player;

/**
 * {@link Game} that manages the lifecycle of a Connect 4 match.
 */
public final class C4 implements Game<Integer, Color> {

  /**
   * The {@code Player} that moves on the first and every alternate turn.
   */
  private Player<Integer, Color> red;

  /**
   * The {@code Player} that moves on the second and every alternate turn.
   */
  private final Player<Integer, Color> blue;

  /**
   * Internal {@link C4State}.
   */
  private final C4State state;

  /**
   * Initializes a fresh {@code C4} instance.
   *
   * @param red the {@code Player} who moves first
   * @param blue the {@code Player} who moves second
   */
  public C4(Player<Integer, Color> red, Player<Integer, Color> blue) {

    // Set players
    this.red = red;
    this.blue = blue;
    this.state = new C4State();
  }

  @Override
  public Player<Integer, Color> current() {
    return state.current() == Color.RED ? red : blue;
  }

  @Override
  public C4State state() {
    return this.state;
  }

  @Override
  public C4State snapshot() {
    return new C4State(this.state);
  }

  @Override
  public void start() {
    await();
  }

  public Color winner() {
    return state.winner();
  }
}

