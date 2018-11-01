package game.niya;

import game.Game;

public final class Niya implements Game<NiyaState, NiyaMove> {

  /**
   * The {@code Player} that moves on the first and every alternate turn.
   */
  private NiyaPlayer red;

  /**
   * The {@code Player} that moves on the second and every alternate turn.
   */
  private NiyaPlayer black;

  /**
   * Internal {@link NiyaState}.
   */
  private NiyaState state;

  /**
   * Co
   *
   * @param red the {@code Player} who moves first
   * @param black the {@code Player} who moves second
   * @param initialState an array containing some permutation of the integers
   *                     0-15, inclusive.
   * @throws IllegalArgumentException if
   */
  public Niya(NiyaPlayer red, NiyaPlayer black, int[] initialState) {

    // Set players
    this.red = red;
    this.black = black;

    // Sanitize and set initial state
    final Spot[] spots = new Spot[16];
    if (initialState.length == 16) {
      boolean[] tmp = new boolean[16];
      for (int idx = 0; idx < 16; idx++) {
        final int i = initialState[idx];
        try {
          if (tmp[i]) {
            throw new IllegalArgumentException();
          }
          tmp[i] = true;
          spots[idx] = new Spot(i);
        } catch (ArrayIndexOutOfBoundsException e) {
          throw new IllegalArgumentException();
        }
      }
    } else {
      throw new IllegalArgumentException();
    }
    state = new NiyaState(spots);
  }

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
  public void start() {
    state.displayTiles();
    await();
  }

  @Override
  public void await() {
    System.out.println("Possible: " + state.validMoves());
    final NiyaState copy = state();
    makeMove(current().decide(copy, copy.validMoves()));
  }
}
