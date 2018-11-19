package game.niya;

import game.State;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Internal class that manages the state of a Niya game instance.  Once a {@code
 * NiyaState} instance is initialized, mutability of its fields is only offered
 * via calls to either {@link #makeMove(NiyaMove)} or {@link #reset()}.
 * <p>
 * A 1D array of {@link Spot Spots}, {@code board}, represents the 2D grid.
 * {@code board} index {@code idx} maps to the (zero-indexed) grid row {@code
 * idx / 4} and grid column {@code idx % 4}.
 * <p>
 * Some nontrivial hackery is used to quickly determine if a win condition has
 * been met.  The basic idea is that every time we mark a spot, we can increment
 * running counts for every row, column, diagonal, or square that contains this
 * spot, and the game is over as soon as any of these counts hits 4.  Instead
 * of explicitly using collections, we harness fact that storing 4 requires only
 * three bits, and with 19 unique win conditions (4 horizontals, 4 verticals, 2
 * diagonals, 9 squares), we only need 57 bits.  Thus, a 64-bit {@code long} is
 * plenty, and we keep one per color ({@code redCache} and {@code blackCache}).
 * Each triple of bits, starting from the right, represents each row from top to
 * bottom, each vertical from left to right, the downward diagonal, the upward
 * diagonal, and each 2x2 square (left to right within each row from top to
 * bottom), respectively.  So, for example, a {@code cache} value of {@code
 * 001,010,001,010,100,010,001,010,001;010,010;000,010,010,000;000,010,010,000}
 * (commas distinguish specifics within a "type" like square or vertical,
 * semicolons indicate new types) indicates exactly
 * <p><ul>
 * <li>0 spots filled for each "outer" horizontal and vertical
 * <li>1 spot filled for each "corner" 2x2 square
 * <li>2 spots filled for each "inner" horizontal, "inner" vertical, and the
 * top- and bottom-middle 2x2 squares
 * <li>3 spots filled nowhere on the board
 * <li>4 spots filled in the middle 2x2 square
 * </ul>
 * <p>
 * You may correctly deduce this to mean that the middle 2x2 square has been
 * filled out.
 * <p>
 * Initially, {@code cache} is {@code 0}.  Whenever a piece is placed, the right
 * bit triples in {@code cache} must be incremented by 1.  If we knew exactly
 * which triples to increment for a given move, we could achieve this in
 * "parallel" by simply adding the right number (whose binary representation has
 * every triple, leading zeros excluded, take the form of either {@code 000} or
 * {@code 001}) to {@code cache}.  Luckily, these numbers <em>are</em>
 * statically known and are stored in {@code INCREMENTS}.
 */
class NiyaState implements State<NiyaMove> {

  /**
   * A 1D array of {@link Spot Spots} that represents the 2D grid.  {@code
   * board} index {@code idx} maps to the (zero-indexed) grid row {@code idx /
   * 4} and grid column {@code idx % 4}.
   */
  private Spot[] board; // match state
  private Spot previous; // (possibly null) Spot selected in previous turn
  private int movesMade; // moves made so far
  private Color winner; // winner as of current turn
  private List<NiyaMove> validMoves; // valid moves on current turn
  private long redCache; // accelerates red win determination
  private long blackCache; // accelerates black win determination

  private static final long[] INCREMENTS = new long[]{
      0x41001001L, 0x240008001L, 0x1200040001L, 0x1008200001L,
      0x8040001008L, 0x48241008008L, 0x241208040008L, 0x201000200008L,
      0x1008000001040L, 0x9048008008040L, 0x48240001040040L, 0x40200000200040L,
      0x1000008001200L, 0x9000000008200L, 0x48000000040200L, 0x40000001200200L
    };

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
   * The winner of the {@code Game} managed by this {@code NiyaState}.  {@code
   * NONE} if either the match is still going or the match ended in a tie.
   */
  public Color winner() { return winner; }

  public int movesMade() { return movesMade; }

  /**
   * The list of {@code Moves} that can be made in the current turn.
   */
  public List<NiyaMove> validMoves() { return validMoves; }

  /**
   * Constructs a new {@code NiyaState} initialized with a copy of the entries,
   * sans color, in {@code initialState}.
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
    redCache = 0L;
    blackCache = 0L;
    // The number of valid moves will never exceed 12
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
    redCache = s.redCache;
    blackCache = s.blackCache;
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
      long cache;
      if (currentColor() == Color.RED) {
        redCache += INCREMENTS[project(m.row,m.col)];
        cache = redCache;
      } else {
        blackCache += INCREMENTS[project(m.row,m.col)];
        cache = blackCache;
      }
      checkStrictWinner(cache);
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
  private void checkStrictWinner(long cache) {
    if ((cache & 0x124924924924924L) != 0L) {
      winner = currentColor();
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
    redCache = 0L;
    blackCache = 0L;
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
