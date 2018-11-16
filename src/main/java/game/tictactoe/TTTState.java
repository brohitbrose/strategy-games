package game.tictactoe;

import game.State;
import java.util.ArrayList;
import java.util.List;

/**
 * Internal class that manages the state of a Tic-tac-toe match.  Once a {@code
 * TTTState} instance is initialized, mutability of its fields is only offered
 * via calls to {@link #makeMove(Integer m)}.
 * <p>
 * A single {@code int}, {@code board}, captures the entire state of the match.
 * With {@code 3^9} possible states of the grid, we could have actually achieved
 * this in a 16-bit {@code char}, but the code is far easier to write (and
 * probably runs faster) when we can assume 2 bits per position, which would
 * require 18 bits total.  Each grid position, starting from the top-left and
 * snaking to the right and down, is assigned a monotonically increasing index
 * {@code i} from 0 to 8.  The {@code i}th to <em>last</em>pair of bits
 * identifies the state at position {@code i}, with {@code 00}, {@code 01},
 * {@code 10}, and {@code 11} denoting unmarked, O, X, and an illegal state,
 * respectively.  So, for example, a {@code board} value of {@code
 * 0b100001100100100110} indicates {@code XOX,_OX,O_X}.
 * <p>
 * Two {@code int[]}s, {@code xMarks} and {@code oMarks} are used to accelerate
 * win condition determination.  Every time a piece is put down, we increment a
 * count for one of 3 horizontals, one of 3 verticals, and between zero and two
 * of 2 diagonals.  The game is over as soon as one of these values is 3.
 */
class TTTState implements State<Integer> {

  private int board; // match state
  private int movesMade; // moves made so far
  private List<Integer> validMoves; // valid moves on current turn
  private Piece winner = Piece.NONE; // winner as of current turn
  private int[] xMarks; // cache to quickly determine if X is winner
  private int[] oMarks; // cache to quickly determine if O is winner

  /**
   * Constructs a new {@code TTTState} without any moves played.
   */
  public TTTState() {
    this.board = 0;
    this.movesMade = 0;
    this.validMoves = new ArrayList<>(9);
    for (int i = 0; i < 9; i++) {
      this.validMoves.add(i);
    }
    xMarks = new int[8];
    oMarks = new int[8];
  }

  /**
   * Copy constructor.
   */
  public TTTState(TTTState s) {
    this.board = s.board;
    this.movesMade = s.movesMade;
    this.validMoves = new ArrayList<>(9);
    this.validMoves.addAll(s.validMoves);
    xMarks = new int[8];
    oMarks = new int[8];
    System.arraycopy(s.xMarks, 0, this.xMarks, 0, 8);
    System.arraycopy(s.oMarks, 0, this.oMarks, 0, 8);
  }

  /**
   * Copy constructor.
   */
  public TTTState(State<Integer> s) {
    this((TTTState) s);
  }

  @Override
  public int movesMade() {
    return movesMade;
  }

  @Override
  public List<Integer> validMoves() {
    return validMoves;
  }

  @Override
  public boolean makeMove(Integer m) {
    if (isOver() || // can't make move if game is over...
        ((3 << (m * 2)) & board) != 0) { // ...or position is already occupied
      return false;
    }
    boolean x = (movesMade & 1) == 0; // true if X is current player
    Piece p;
    int[] cache;
    int offset;
    if (x) {
      p = Piece.X;
      cache = xMarks;
      offset = 1;
    } else {
      p = Piece.O;
      cache = oMarks;
      offset = 0;
    }
    board |= (1 << (m * 2 + offset)); // make move
    movesMade++;
    validMoves.remove(m);
    // increment and check running counts for relevant horizontals, verticals,
    // and diagonals
    cache[m/3] += 1;
    cache[(m%3) + 3] += 1;
    int maxSoFar = Math.max(cache[m/3], cache[(m%3)+3]);
    if (m == 0 || m == 4 || m == 8) {
      cache[6] += 1;
      maxSoFar = Math.max(maxSoFar, cache[6]);
    }
    if (m == 2 || m == 4 || m == 6) {
      cache[7] += 1;
      maxSoFar = Math.max(maxSoFar, cache[7]);
    }
    if (maxSoFar >= 3) {
      winner = p;
      validMoves.clear();
    }
    return true;
  }

  @Override
  public boolean isOver() {
    return winner != Piece.NONE || validMoves.isEmpty();
  }

  Piece winner() {
    return winner;
  }

  @Override
  public void debug() {
    int copy = this.board;
    final int mask = 3; // 0b0000...011
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        int filter = copy & mask; // last two bits of `board`
        if (filter == 0) {
          System.out.print('_'); // 0b00 indicates no moves were made
        } else if (filter == 1) {
          System.out.print('O'); // 0b01 indicates 'O'
        } else if (filter == 2) {
          System.out.print('X'); // 0b10 indicates 'X'
        } else {
          throw new IllegalStateException(); // should never see 0b11
        }
        copy = (copy >>> 2); // once displayed, bits no longer needed
      }
      System.out.println();
    }
    System.out.println("-------");
  }
}
