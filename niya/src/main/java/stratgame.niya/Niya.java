package stratgame.niya;

import stratgame.game.Game;

/**
 * {@link Game} that manages the lifecycle of a Niya match.
 */
public final class Niya implements Game<NiyaMove> {

  /**
   * The {@code Player} that moves on the first and every alternate turn.
   */
  private final NiyaPlayer red;

  /**
   * The {@code Player} that moves on the second and every alternate turn.
   */
  private final NiyaPlayer black;

  /**
   * Internal {@link NiyaState}.
   */
  private final NiyaState state;

  /**
   * Initializes a {@code Niya} instance with {@code initialState}.
   *
   * @param red the {@code Player} who moves first
   * @param black the {@code Player} who moves second
   * @param initialState an array containing some permutation of the integers
   *                     0-15, inclusive.
   * @throws IllegalArgumentException if {@code initialState.length != 16} or
   * {@code initialState} does not contain every integer from 0 to 15.
   */
  public Niya(NiyaPlayer red, NiyaPlayer black, int[] initialState) {

    // Set players
    this.red = red;
    this.black = black;
    // Set initial state
    state = new NiyaState(initialState);
  }

  /**
   * Initializes a {@code Niya} instance with a randomized board configuration.
   */
  public Niya(NiyaPlayer red, NiyaPlayer black) {
    this(red, black, randomInit());
  }

  private static int[] randomInit() {
    final int[] board = new int[16];
    for (int i = 0; i < board.length; i++) {
      board[i] = i;
    }
    for (int i = 16; i > 0; i--) {
      final int backup = board[i-1];
      final int idx = (int) (Math.random() * i);
      board[i-1] = board[idx];
      board[idx] = backup;
    }
    return board;
  }

  @Override
  public NiyaPlayer current() {
    return state.currentColor() == Color.RED ? red : black;
  }

  @Override
  public NiyaState state() {
    return state;
  }

  @Override
  public NiyaState snapshot() {
    return new NiyaState(this.state);
  }

  @Override
  public void start() {
    state.displayTiles();
    await();
  }

  @Override
  public void await() {
    final NiyaState copy = snapshot();
    System.out.println("Possible: " + copy.validMoves());
    playMove(current().decide(copy, copy.validMoves()));
  }
}
