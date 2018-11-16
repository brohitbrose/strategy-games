package game.niya;

import game.State;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import recon.Record;
import recon.Value;

/**
 * Internal class that manages the state of a Niya game instance.  Once a {@code
 * NiyaState} instance is initialized, mutability of its fields is only offered
 * via calls to either {@link #makeMove(NiyaMove)} or {@link #reset()}.
 */
class NiyaState implements State<NiyaMove> {

  /**
   * A 1D array of {@link Spot Spots} that represents the 2D grid.  {@code
   * board} index {@code idx} maps to the (zero-indexed) grid row {@code idx /
   * 4} and grid column {@code idx % 4}.
   */
  private Spot[] board;

  /**
   * The {@code Spot} that was selected in the previous turn, {@code null} if
   * and only if no moves were played yet.
   */
  private Spot previous;

  /**
   * The number of moves that have been played so far.
   */
  private int movesMade;

  /**
   * The winner of the {@code Game} managed by this {@code NiyaState}.  {@code NONE}
   * if either the match is still going or the match ended in a tie.
   */
  private Color winner;

  private List<NiyaMove> validMoves;

  /**
   * The {@code Color} that should move in the current turn.
   */
  public Color currentColor() {
    return (movesMade & 1) == 0 ? Color.RED : Color.BLACK;
  }

  /**
   * The {@code Color} that should not move in the current turn.
   */
  public Color otherColor() { return (movesMade & 1) == 0 ? Color.BLACK : Color.RED; }

  /**
   * The winner of the {@code Game} managed by this {@code NiyaState}.  {@code NONE}
   * if either the match is still going or the match ended in a tie.
   */
  public Color winner() { return winner; }

  /**
   * The number of moves that have been played so far.
   */
  public int movesMade() { return movesMade; }

  /**
   * The list of {@code Moves} that can be made in the current turn.
   */
  public List<NiyaMove> validMoves() { return validMoves; }

  /**
   * Constructs a new {@code NiyaState} initialized with a copy of the entries, sans
   * color, in {@code initialState}.
   */
  public NiyaState(Spot[] initialState) {
    previous = null;
    movesMade = 0;
    winner = Color.NONE;
    board = new Spot[16];
    for (int i = 0; i < 16; i++) {
      board[i] = new Spot(initialState[i]);
      board[i].color = Color.NONE;
    }
    board = initialState;
    // The number of valid moves will never exceed 10
    validMoves = new ArrayList<>(12);
    updateValidMoves();
  }


  /**
   * Copy constructor.
   */
  public NiyaState(State<NiyaMove> s) {
    this((NiyaState) s);
  }

  /**
   * Copy constructor.
   */
  public NiyaState(NiyaState s) {
    board = new Spot[s.board.length];
    for (int i = 0; i < board.length; i++) {
      board[i] = new Spot(s.board[i]);
    }
    previous = s.previous;
    movesMade = s.movesMade;
    winner = s.winner;
    List<NiyaMove> moves = new ArrayList<>(12);
    for (NiyaMove m: s.validMoves) {
      moves.add(new NiyaMove(m));
    }
    validMoves = moves;
  }

  /**
   * Projects {@code row} and {@code col} to the single-dimensional {@code
   * this.board} index.  Assumes {@code row} and {@code col} are each in the
   * range 0..=3.
   */
  private static int project(int row, int col) {
    return (row << 2) + col;
  }

  /**
   * Expands {@code index} into its row and column components.  Assumes {@code
   * index} is in the range 0..=15.
   */
  private static NiyaMove expand(int index) {
    return new NiyaMove(index >> 2, index & 3);
  }

  /**
   * Returns the {@code Spot} at grid position ({@code m.row}, {@code m.col}).
   */
  public Spot getSpot(NiyaMove m) {
    return getSpot(m.row,m.col);
  }

  /**
   * Returns the {@code Spot} at grid position ({@code row}, {@code col}).
   */
  public Spot getSpot(int row, int col) {
    return getSpot(project(row,col));
  }

  /**
   * Returns the {@code Spot} at {@code board } index {@code idx}.
   */
  private Spot getSpot(int idx) {
    return board[idx];
  }

  @Override
  public boolean makeMove(NiyaMove m) {
    if (validateDecision(m)) {
      final Spot s = getSpot(m);
      s.color = currentColor();
      checkStrictWinner();
      movesMade++;
      previous = s;
      updateValidMoves();
      return true;
    }
    return false;
  }

  @Override
  public boolean isOver() {
    return winner != Color.NONE || !hasRemaining();
  }

  /**
   * Returns whether {@code m} is a valid move in this turn.
   */
  private boolean validateDecision(NiyaMove m) {
    if (previous == null) {
      return (m.row * m.col) % 3 == 0;
    }
    final Spot s = getSpot(m.row, m.col);
    return s.color == Color.NONE &&
      (s.plant == previous.plant || s.poem == previous.poem);
  }

  /**
   * Checks whether a "strict" win-condition has been met (i.e. without forcing
   * the opponent into a no-move situation) and updates internal fields
   * accordingly.
   */
  private void checkStrictWinner() {
    for (Map.Entry<Integer, List<List<Integer>>> entry : WIN.entrySet()) {
      final int firstIdx = entry.getKey();
      if (getSpot(firstIdx).color == currentColor()) {
        for (List<Integer> list : entry.getValue()) {
          boolean soFar = true;
          for (Integer idx : list) {
            if (getSpot(idx).color != currentColor()) {
              soFar = false;
              break;
            }
          }
          if (soFar) {
            winner = currentColor();
            return;
          }
        }
      }
    }
  }

  public boolean hasRemaining() {
    return movesMade < 16;
  }

  /**
   * Updates {@code validMoves} for this turn, updating {@code winner} if no
   * moves remain but uncolored {@code Spots} do.
   */
  private void updateValidMoves() {
    validMoves.clear();
    if (hasRemaining()) {
      for (int i = 0; i < 4; i++) {
        for (int j = 0; j < 4; j++) {
          final NiyaMove m = new NiyaMove(i,j);
          if (validateDecision(m)) {
            validMoves.add(m);
          }
        }
      }
      if (validMoves.isEmpty()) {
        winner = otherColor();
      }
    }
  }

  /**
   * Every possible strict win condition, i.e. not including those that make the
   * opponent unable to move.
   */
  private static final Map<Integer, List<List<Integer>>> WIN = new HashMap<>();
  static {
    WIN.put(project(0,0), Arrays.asList(
      Arrays.asList(project(0,1), project(0,2), project(0,3)),
      Arrays.asList(project(1,1), project(2,2), project(3,3)),
      Arrays.asList(project(1,0), project(2,0), project(3,0)),
      Arrays.asList(project(0,1), project(1,0), project(1,1))
    ));
    WIN.put(project(0,1), Arrays.asList(
      Arrays.asList(project(0,2), project(1,1), project(1,2)),
      Arrays.asList(project(1,1), project(2,1), project(3,1))
    ));
    WIN.put(project(0,2), Arrays.asList(
      Arrays.asList(project(0,3), project(1,2), project(1,3)),
      Arrays.asList(project(1,2), project(2,2), project(3,2))
    ));
    WIN.put(project(0,3), Arrays.asList(
      Arrays.asList(project(1,2), project(2,1), project(3,0)),
      Arrays.asList(project(1,3), project(2,3), project(3,3))
    ));
    WIN.put(project(1,0), Arrays.asList(
      Arrays.asList(project(1,1), project(1,2), project(1,3)),
      Arrays.asList(project(1,1), project(2,0), project(2,1))
    ));
    WIN.put(project(1,1), Collections.singletonList(
      Arrays.asList(project(1,2), project(2,1), project(2,2))
    ));
    WIN.put(project(1,2), Collections.singletonList(
      Arrays.asList(project(1,3), project(2,2), project(2,3))
    ));
    WIN.put(project(2,0), Arrays.asList(
      Arrays.asList(project(2,1), project(2,2), project(2,3)),
      Arrays.asList(project(2,1), project(3,0), project(3,1))
    ));
    WIN.put(project(2,1), Collections.singletonList(
      Arrays.asList(project(2,2), project(3,1), project(3,2))
    ));
    WIN.put(project(2,2), Collections.singletonList(
      Arrays.asList(project(2,3), project(3,2), project(3,3))
    ));
    WIN.put(project(3,0), Collections.singletonList(
      Arrays.asList(project(3,1), project(3,2), project(3,3))
    ));
  }
  void reset() {
    final LinkedList<Spot> list = new LinkedList<>();
    for (int i = 0; i < 4; i++) {
      for (int j = 0; j < 4; j++) {
        list.add(new Spot(i,j));
      }
    }
    // Shuffle spots
    board = new Spot[16];
    for (int i = 0; i < board.length; i++) {
      board[i] = list.remove((int) (Math.random() * list.size()));
    }
    previous = null;
    movesMade = 0;
    updateValidMoves();
    winner = Color.NONE;
  }
  void displayTiles() {
    System.out.println("----------");
    for (int i = 0; i < 4; i++) {
      for (int j = 0; j < 4; j++) {
        System.out.print(getSpot(i,j));
      }
      System.out.println();
    }
  }

  private Value toValue() {
    final Record rec;
    if (winner != Color.NONE || !hasRemaining()) {
      rec = Record.of().slot("winner",winner.toString());
      if (previous != null) rec.slot("previousMove",previous.toValue());
    } else {
      final Record moves = Record.of();
      for (NiyaMove m: validMoves) {
        moves.withItem(NiyaMove.FORM.mold(m));
      }
      rec = Record.of().slot("currentPlayer", currentColor().toString())
          .slot("validMoves", moves);
    }
    final Record board = Record.of();
    for (Spot s: this.board) {
      board.withItem(s.toValue());
    }
    return rec.slot("board", board);
  }

  @Override
  public String toString() {
    return toValue().toRecon();
  }

  @Override
  public void debug() {
    for (int i = 0; i < 4; i++) {
      for (int j = 0; j < 4; j++) {
        String temp = getSpot(i,j).color.toString().substring(0,1);
        if ("N".equals(temp)) temp = "_";
        System.out.print(temp);
      }
      System.out.println();
    }
    if (isOver()) {
      System.out.println("WINNER: " + winner);
    }
  }
}
