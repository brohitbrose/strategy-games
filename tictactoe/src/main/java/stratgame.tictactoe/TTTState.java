package stratgame.tictactoe;

import java.util.ArrayList;
import java.util.List;
import stratgame.game.State;

/**
 * Internal class that manages the state of a Tic-tac-toe match.  Once a {@code
 * TTTState} instance is initialized, mutability of its fields is only offered
 * via calls to {@link #makeMove(Integer m)}.
 * <p>
 * A single {@code int}, {@code board}, captures the entire state of the match.
 * With {@code 3^9} possible states of the grid, we could have actually achieved
 * this in a 16-bit {@code char}, but the code is far easier to write (and
 * probably runs faster) when we can assume 2 bits per position, which would
 * require 18 bits.  Each grid position, starting from the top-left and snaking
 * to the right and down, is assigned a monotonically increasing index {@code i}
 * from 0 to 8.  The {@code i}th to <em>last</em> pair of bits identifies the
 * state at position {@code i}, with {@code 00}, {@code 01}, {@code 10}, and
 * {@code 11} denoting unmarked, O, X, and an illegal state, respectively.  So,
 * for example, a {@code board} value of {@code 0b100001100100100110} indicates
 * {@code XOX,_OX,O_X}.
 * <p>
 * A single {@code int}, {@code cache}, accelerates win determination. One way
 * to find a win condition is to increment a count for one of 3 horizontals, one
 * of 3 verticals, and between zero and two of 2 diagonals every time a piece is
 * placed down; the game ends if one of these counts hits 3.  Storing 3 requires
 * two bits, there are eight possible "lines" on a board, and we must keep track
 * of two pieces.  Thus, we need {@code 2*8*2=32} bits for {@code cache},
 * the perfect job for an {@code int}.  The counts for {@code X} are stored in
 * the rightmost 16 bits, with each pair from the <em>right</em> tracking the
 * top horizontal, middle horizontal, bottom horizontal, left vertical, middle
 * vertical, right vertical, downward diagonal, and upward diagonal,
 * respectively.  The counts for {@code O} follow identically and are stored in
 * the left 16 bits.
 */
public class TTTState implements State<Integer> {

  private int board; // match state
  private int movesMade; // moves made so far
  private List<Integer> validMoves; // valid moves on current turn
  private Piece winner = Piece.NONE; // winner as of current turn
  private int cache; // accelerate win determination

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
    this.cache = 0;
  }

  /**
   * Copy constructor.
   */
  public TTTState(TTTState s) {
    this.board = s.board;
    this.movesMade = s.movesMade;
    this.validMoves = new ArrayList<>(9);
    this.validMoves.addAll(s.validMoves);
    this.cache = s.cache;
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

  /**
   * Returns the two bits of {@code cacheCopy} that are {@code filter} pairs
   * from the right.
   */
  private static int extract(int cacheCopy, int filter) {
    return ((cacheCopy & (3 << filter)) >>> filter);
  }

  /**
   * Returns 3 if either {@code val} or {@code foundThree} is 3, 1 otherwise.
   */
  private static int collapse(int val, int foundThree) {
    return ((((val >>> 1) & val) | ((foundThree >>> 1) & foundThree)) << 1) | 1;
  }

  @Override
  public boolean makeMove(Integer m) {
    if (isOver() // can't make move if game is over...
        || (m < 0 || m > 8) // or m is outside 0..=8
        || ((3 << (m << 1)) & board) != 0) { // ...or m is occupied
      return false;
    }
    boolean x = (movesMade & 1) == 0; // true if X is current player
    int cacheCopy;
    int boardOffset;
    Piece p;
    int offset;
    if (x) {
      p = Piece.X;
      offset = 0;
      boardOffset = 1;
      cacheCopy = (cache & 0xFFFF);
    } else {
      p = Piece.O;
      offset = 16;
      boardOffset = 0;
      cacheCopy = (cache >>> 16);
    }
    board |= (1 << ((m << 1) + boardOffset)); // make move
    movesMade++;
    validMoves.remove(m);

    // relevant horizontal
    int filter = (m / 3) << 1;
    cacheCopy += (1 << filter);
    int foundThree = extract(cacheCopy, filter); // possible 0, 1, 2, 3
    // relevant vertical
    filter = ((m % 3) + 3) << 1;
    cacheCopy += (1 << filter);
    int val = extract(cacheCopy, filter);
    foundThree = collapse(val, foundThree); // possible 1, 3
    // diagonal that slopes down
    if ((m & 3) == 0) {
      filter = 12;
      cacheCopy += (1 << filter);
      val = extract(cacheCopy, filter);
      foundThree = collapse(val, foundThree); // possible 1, 3
    }
    // diagonal that slopes up
    if (m == 2 || m == 4 || m == 6) {
      filter = 14;
      cacheCopy += (1 << filter);
      val = extract(cacheCopy, filter);
      foundThree = collapse(val, foundThree); // possible 1, 3
    }
    // update cache
    cache = (cacheCopy << offset) | (cache & (0xFFFF << (16 - offset)));
    if (foundThree == 3) {
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
          throw new AssertionError("spot value was not 0, 1, or 2");
        }
        // once displayed, bits no longer needed. Shift for next grid position.
        copy = (copy >>> 2);
      }
      System.out.println();
    }
    System.out.println("-------");
  }
}
