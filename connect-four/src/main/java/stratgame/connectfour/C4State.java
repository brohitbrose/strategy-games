package stratgame.connectfour;

import java.util.ArrayList;
import java.util.List;
import stratgame.game.State;

/**
 * Context that manages the state of a Connect Four game instance.  Once a
 * {@code C4State} instance is initialized, mutability of its fields is only
 * offered via calls to {@link #makeMove(Integer)}.
 */
public class C4State implements State<Integer, Color> {

  private int movesMade;
  private long recentBoard; // perspective of piece that just played
  private long occupied;
  private Color winner;
  private int validMoveCount;
//  private final History history;

  @Override
  public int movesMade() {
    return this.movesMade;
  }

  private void addValidMove(List<Integer> moves, final int col) {
    if (isValidMove(col)) {
      moves.add(col);
    }
    this.validMoveCount = moves.size();
  }

  boolean isValidMove(final int col) {
    final long mask = 1L << 5 << (7*col);
    return (this.occupied & mask) == 0;
  }

  @Override
  public List<Integer> validMoves() {
    final List<Integer> validMoves = new ArrayList<>(validMoveCount);
    addValidMove(validMoves, 3);
    addValidMove(validMoves, 2);
    addValidMove(validMoves, 4);
    addValidMove(validMoves, 1);
    addValidMove(validMoves, 5);
    addValidMove(validMoves, 0);
    addValidMove(validMoves, 6);
    return validMoves;
  }

  public C4State() {
    this.movesMade = 0;
    this.recentBoard = 0L;
    this.occupied = 0L;
    this.winner = Color.NONE;
    this.validMoveCount = 7;
//    this.history = new History();
  }

  C4State(C4State that) {
    this.movesMade = that.movesMade;
    this.recentBoard = that.recentBoard;
    this.occupied = that.occupied;
    this.winner = that.winner;
    this.validMoveCount = that.validMoveCount;
//    this.history = new History(that.history);
  }

  boolean willWin(int m) {
    long cur = this.recentBoard ^ this.occupied;
    final long mask = 1L << (7*m);
    cur |= ((this.occupied + mask) & (63 << (7*m)));
    return hasWinner(cur);
  }

  private static boolean hasWinner(long currentBoard) {
    long shift;
    // vertical
    shift = (currentBoard >>> 1) & currentBoard;
    if (((shift & (shift >>> 2)) != 0)) {
      return true;
    }
    // horizontal
    shift = (currentBoard >>> 7) & currentBoard;
    if (((shift & (shift >>> 14)) != 0)) {
      return true;
    }
    // upward diagonal
    shift = (currentBoard >>> 8) & currentBoard;
    if (((shift & (shift >>> 16)) != 0)) {
      return true;
    }
    // downward diagonal
    shift = (currentBoard >>> 6) & currentBoard;
    if (((shift & (shift >>> 12)) != 0)) {
      return true;
    }
    return false;
  }

  @Override
  public boolean makeMove(Integer move) {
    final int mv = move.intValue();
    final long mask = 1L << 5 << (7*mv);
    if ((this.occupied & mask) == 0) {
      this.occupied |= (this.occupied + (mask >>> 5));
//      this.history.push(mv);
      this.movesMade++;
      this.recentBoard ^= this.occupied;
      if (hasWinner(this.recentBoard)) {
        this.winner = (this.movesMade & 1) == 0 ? Color.BLUE : Color.RED;
      }
      return true;
    }
    return false;
  }

  @Override
  public Integer undo() {
    throw new UnsupportedOperationException();
//    if (this.movesMade != 0) {
//      this.movesMade--;
////      final int mv = this.history.pop();
//      this.recentBoard ^= this.occupied;
//      this.occupied =
//    }
//    return null;
  }

  @Override
  public boolean isOver() {
    return this.winner != Color.NONE && movesMade() < 42;
  }

  @Override
  public void debug() {
    System.out.println("----Begin C4State----");
    final long otherBoard = this.recentBoard ^ this.occupied;
    final char recent, other;
    if (current() == Color.RED) {
      recent = 'B';
      other = 'R';
    } else {
      recent = 'R';
      other = 'B';
    }
    for (int i = 5; i >= 0; i--) {
      for (int j = 0; j <= 6; j++) {
        if ((this.recentBoard & (1L << i << 7*j)) == 0) {
          if ((otherBoard & (1L << i << 7*j)) != 0) {
            System.out.print(other);
          } else {
            System.out.print('_');
          }
        } else {
          System.out.print(recent);
        }
      }
      System.out.println(' ');
    }
    System.out.println("----End C4State----");
  }

  @Override
  public C4State clone() {
    return new C4State(this);
  }

  @Override
  public Color winner() {
    return this.winner;
  }

  Color current() {
    return (this.movesMade & 1) == 0 ? Color.RED : Color.BLUE;
  }

  long key() {
    return this.recentBoard + this.occupied;
  }

//  int nonLosingMoves() {
//    final long possible = (this.occupied + 0x40810204081L) & 0xFDFBF7EFDFBFL;
//
//  }
//
//  long currentWinMask() {
//    return winMask(this.recentBoard);
//  }
//
//  long otherWinMask() {
//    return winMask(this.recentBoard ^ this.occupied);
//  }
//
//  private long winMask(long state) {
//
//  }

//  private class History {
//    long first;
//    long second;
//
//    History(long first, long second) {
//      this.first = first;
//      this.second = second;
//    }
//
//    History(History other) {
//      this(other.first, other.second);
//    }
//
//    History() {
//      this(0L, 0L);
//    }
//
//    void push(int a) {
//      if (C4State.this.movesMade < 21 ) {
//        this.first |= (((long) a) << (3*C4State.this.movesMade));
//      } else {
//        this.second |= (((long) a) << (3*(C4State.this.movesMade - 21)));
//      }
//    }
//
//    Integer pop() {
//      if (C4State.this.movesMade < 21 ) {
//        return ;
//      } else {
//        return ;
//      }
//    }
//  }
}
