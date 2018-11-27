package stratgame.ultimate;

import java.util.ArrayList;
import java.util.List;
import stratgame.game.State;
import stratgame.tictactoe.Piece;
import stratgame.tictactoe.TTTState;

/**
 * Context that manages the state of an Ultimate match.  Once an {@code
 * UltimateState} is initialized, mutability of its fields is only offered via
 * calls to {@link #makeMove(Integer m)}.  The first 16 bits of {@code m}
 * determine target 3x3 square of the move, and the last 16 bits determine the
 * target spot within the prior square.
 * <p>
 * Each 3x3 square that acts as its own tic-tac-toe match can almost be
 * represented by a {@code stratgame.tictactoe.TTTState}, except we must change
 * the logic that determines which {@code stratgame.tictactoe.Piece} will be
 * placed.  We capture this behavior in the {@code Individual} class, and an
 * {@code UltimateState} tracks nine such {@code Individuals}.  An {@code
 * UltimateState} is responsible for calling {@code currentPiece(Piece)} against
 * each {@code Individual} with the correct {@code Piece} before calling {@code
 * makeMove(Integer)}.
 */
public class UltimateState implements State<Integer> {

  private int board; // match state
  private int movesMade; // moves made so far
  private Piece winner; // winner as of current turn
  private Individual[] individuals; // local games
  private int cache; // accelerate win determination
  private int previous; // previous move
  private int heuristic; // upper bound for valid move count

  private static final int[] INCREMENTS = new int[]{
      0b0001000001000001, 0b0000000100000001, 0b0100010000000001,
      0b0000000001000100, 0b0101000100000100, 0b0000010000000100,
      0b0100000001010000, 0b0000000100010000, 0b0001010000010000
    };  

  public UltimateState() {
    this.board = 0;
    this.movesMade = 0;
    this.winner = Piece.NONE;
    this.individuals = new Individual[]{
        new Individual(), new Individual(), new Individual(),
        new Individual(), new Individual(), new Individual(),
        new Individual(), new Individual(), new Individual()
      };
    this.cache = cache;
    this.previous = 0xFFFFFFFF;
    this.heuristic = 81;
  }

  /**
   * Copy constructor.
   */
  public UltimateState(UltimateState s) {
    this.board = s.board;
    this.movesMade = s.movesMade;
    this.winner = s.winner;
    this.individuals = new Individual[9];
    for (int i = 0; i < 9; i++) {
      this.individuals[i] = new Individual(s.individuals[i]);
    }
    this.cache = cache;
    this.previous = s.previous;
    this.heuristic = s.heuristic;
  }

  /**
   * Copy constructor.
   */
  public UltimateState(State<Integer> s) {
    this((UltimateState) s);
  }

  @Override
  public int movesMade() {
    return movesMade;
  }

  @Override
  public List<Integer> validMoves() {
    final ArrayList<Integer> result;
    final int outer = inner(previous);
    // if previous move restricts validMoves...
    if (previous != 0xFFFFFFFF && !individuals[outer].isOver()) {
      final Individual ind = individuals[outer];
      final int length = ind.validMoves().size();
      result = new ArrayList<>(length);
      final int prefix = outer << 16;
      // ...then return all valid moves from predetermined valid board...
      for (Integer inner : ind.validMoves()) {
        result.add(prefix + inner);
      }
    } else {
      result = new ArrayList<>(heuristic);
      // otherwise return all valid moves from all unfinished boards
      for (int i = 0; i < 9; i++) {
        Individual individual = individuals[i];
        if (!individual.isOver()) {
          final int prefix = i << 16;
          for (Integer inner : individual.validMoves()) {
            result.add(prefix + inner);
          }
        }
      }
    }
    return result;
  }

  /**
   * Returns the {@code Piece} that is responsible for the next move, {@code
   * NONE} if the match is over.
   */
  protected Piece currentPiece() {
    return isOver() ? Piece.NONE :
        (movesMade & 1) == 0 ? Piece.X : Piece.O;
  }  

  /**
   * Blindly plays {@code m} and returns whether the act resulted in a victory.
   */
  private boolean moveAndCheck(int cacheCopy, int m, int offset, int boardOffset) {
    // make move
    board |= (1 << ((m << 1) + boardOffset));
    // update cache
    cacheCopy += INCREMENTS[m];
    cache = (cacheCopy << offset) | (cache & (0xFFFF << (16 - offset)));
    return ((cacheCopy & 0xAAAA) & ((cacheCopy & 0x5555) << 1)) != 0;
  }

  @Override
  public boolean makeMove(Integer m) {
    final int outer = outer(m); // outer index of this move
    final int prevInner = inner(previous); // inner index of previous move
    // validate m for this turn
    if (previous == 0xFFFFFFFF || prevInner == outer || individuals[prevInner].isOver()) {
      final Piece p = currentPiece();
      // fail-fast if match is over
      if (p == Piece.NONE) {
        return false;
      }
      // play m in local game
      final Individual ind = individuals[outer];
      ind.currentPiece(p);
      boolean moveSucceeded = ind.makeMove(inner(m));
      if (moveSucceeded) {
        // update previous and movesMade if local play was successful
        previous = m;
        movesMade++;
        if (ind.isOver()) {
          // decrement upper bound for valid move count, then update cache
          heuristic -= 9;
          int cacheCopy;
          int boardOffset;
          int offset;
          if (p == Piece.X) {
            offset = 0;
            boardOffset = 1;
            cacheCopy = (cache & 0xFFFF);
          } else {
            offset = 16;
            boardOffset = 0;
            cacheCopy = (cache >>> 16);
          }
          if (moveAndCheck(cacheCopy, outer, offset, boardOffset)) {
            winner = p;
          }
        }
      }
      return moveSucceeded;
    }
    return false;
  }

  @Override
  public boolean isOver() {
    return winner != Piece.NONE || validMoves().isEmpty();
  }

  // utility method to avoid even further debug() bloat
  private void buildUnfinished(char[][] chars, int row, int col, Individual ind) {
    int copy = ind.board();
    final int mask = 3; // 0b0000...011
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        int filter = copy & mask; // last two bits of `board`
        if (filter == 0) {
          chars[row+i][col+j] = '_'; // 0b00 indicates no moves were made
        } else if (filter == 1) {
          chars[row+i][col+j] = 'O'; // 0b01 indicates 'O'
        } else if (filter == 2) {
          chars[row+i][col+j] = 'X'; // 0b10 indicates 'X'
        } else {
          throw new AssertionError("spot value was not 0, 1, or 2");
        }
        // once displayed, bits no longer needed. Shift for next grid position.
        copy = (copy >>> 2);
      }
    }
  }

  @Override
  public void debug() {
    final char[][] chars = new char[13][13];
    // horizontal borders
    for (int i = 0; i < 13; i += 4) {
      for (int j = 0; j < 13; j++) {
        chars[i][j] = '=';
      }
    }
    // vertical borders
    for (int i = 1; i <= 9; i += 4) {
      for (int j = i; j <= i+2; j++) {
        for (int k = 0; k <= 12; k += 4) {
          chars[j][k] = '|';
        }
      }
    }
    // local matches
    for (int i = 0; i < 9; i++) {
      final int row = 4 * (i/3) + 1;
      final int col = 4 * (i%3) + 1;
      final Individual ind = individuals[i];
      if (ind.isOver()) {
        if (ind.winner() == Piece.X) { // if X won local match
          chars[row][col] = '@'; chars[row][col+1] = ' '; chars[row][col+2] = '@';
          chars[row+1][col] = ' '; chars[row+1][col+1] = '@'; chars[row+1][col+2] = ' ';
          chars[row+2][col] = '@'; chars[row+2][col+1] = ' '; chars[row+2][col+2] = '@';
        } else if (ind.winner() == Piece.O) { // if O won local match
          for (int r = row; r < row+3; r++) {
            for (int c = col; c < col+3; c++) {
              chars[r][c] = '@';
            }
          }
          chars[row+1][col+1] = ' ';
        } else { // local match was draw
          for (int r = row; r < row+3; i++) {
            for (int c = col; c < col+3; c++) {
              chars[r][c] = '/';
            }
          }          
        }
      } else { // local match is ongoing
        buildUnfinished(chars, row, col, ind);
      }
    }
    for (int i = 0; i < 13; i++) {
      for (int j = 0; j < 13; j++) {
        System.out.print(chars[i][j]);
      }
      System.out.println();
    }
    System.out.println("---");
  }

  /**
   * Returns the local index of the playable move {@code i}.
   */
  private static int inner(int i) {
    return i & 0x0000FFFF;
  }

  /**
   * Returns the global index of the playable move {@code i}.
   */
  private static int outer(int i) {
    return (i & 0xFFFF0000) >>> 16;
  }

  /**
   * Returns the projection of a move's global index {@code outer} and local
   * index {@code inner} into a playable move.
   */
  public static int project(int outer, int inner) {
    if (outer < 0 || outer > 8 || inner < 0 || inner > 8) {
      throw new IllegalArgumentException("outer and inner must be in 0..=8");
    }
    return (outer << 16) + inner;
  }
}

/**
 * Context that manages the state of a tic-tac-toe match within an Ultimate
 * match.
 */
class Individual extends TTTState {

  Piece currentPiece;

  Individual() {
    this.currentPiece = Piece.NONE;
  }

  /**
   * Copy constructor.
   */
  Individual(Individual s) {
    super(s);
    this.currentPiece = s.currentPiece;
  }

  /**
   * Copy constructor.
   */
  Individual(State<Integer> s) {
    super(s);
    this.currentPiece = Piece.NONE;
  }

  void currentPiece(Piece piece) {
    currentPiece = piece;
  }

  @Override
  protected Piece currentPiece(Integer nextMove) {
    return nextMove < 0 || nextMove > 8 // out of bounds
        || isOver() // match is over
        || ((3 << (nextMove << 1)) & board()) != 0 ? // spot is occupied
          Piece.NONE : currentPiece;
  }
}
