package game.tictactoe;

import game.State;
import java.util.ArrayList;
import java.util.List;

public class TTTState implements State<Integer> {

  private int board;
  private int movesMade;
  private List<Integer> validMoves;
  private Piece winner = Piece.NONE;
  private int[] xMarks;
  private int[] oMarks;

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
    if (isOver()) {
      return false;
    }
    if (((3 << (m * 2)) & board) != 0) {
      return false;
    }
    boolean x = (movesMade & 1) == 0;
    Piece p;
    int[] cache;
    if (x) {
      p = Piece.X;
      cache = xMarks;
    } else {
      p = Piece.O;
      cache = oMarks;
    }
    board = board | (1 << (m * 2 + (x ? 1 : 0)));
    movesMade++;
    validMoves.remove(m);
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
    final int mask = 3;
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        int filter = copy & mask;
        if (filter == 0) {
          System.out.print('_');
        } else if (filter == 1) {
          System.out.print('O');
        } else if (filter == 2) {
          System.out.print('X');
        }
        copy = (copy >>> 2);
      }
      System.out.println();
    }
    System.out.println("-------");
  }
}

enum Piece { X, O, NONE }
