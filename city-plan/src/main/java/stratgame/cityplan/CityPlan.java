package stratgame.cityplan;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import stratgame.game.Puzzle;

public class CityPlan implements Puzzle<Integer> {

  private long counts;
  private int[] moves;
  private int movesMade;
  private final int[][] neighborhoods;
  private final NeighborhoodLookup nbhLookup;
  private final ValidPlayChecker validPlayChecker;

  private CityPlan(long counts, int[] moves, int movesMade,
                   int[][] neighborhoods, NeighborhoodLookup nbhLookup,
                   ValidPlayChecker validPlayChecker) {
    this.counts = counts;
    this.moves = Arrays.copyOf(moves, moves.length);
    this.movesMade = movesMade;
    this.neighborhoods = neighborhoods;
    this.nbhLookup = nbhLookup;
    this.validPlayChecker = validPlayChecker;
  }

  public CityPlan(int[][] neighborhoods) {
    this.counts = 0L;
    this.moves = new int[18];
    this.movesMade = 0;
    // TODO: copy
    this.neighborhoods = neighborhoods;
    this.nbhLookup = new NeighborhoodLookup(neighborhoods);
    this.validPlayChecker = new ValidPlayChecker(neighborhoods);
  }

  @Override
  public boolean makeMove(Integer move) {
    // Sanity check
    if (move == null || move < 0 || move >= CELLS) {
      return false;
    }
    // Can't move after the puzzle is solved
    if (isSolved()) {
      return false;
    }
    // Ensure legal move
    if (!this.validPlayChecker.isAllowed(move)) {
      return false;
    }
    // Play move
    this.moves[this.movesMade++] = move;
    // Update row, column, and neighborhood counts
    final int row = move / ROWS,
        col = move % COLS,
        nbh = this.nbhLookup.getNeighborhood(move);
    final int rowCount = incrementRow(row),
      colCount = incrementCol(col),
      nbhCount = incrementNbh(nbh);
    // Forbid future moves to:
    // - the tile
    this.validPlayChecker.invalidate(move);
    // - its neighbors
    this.validPlayChecker.invalidateNeighbors(move);
    // - any "complete" rows, columns, or neighborhoods
    if (rowCount == 2) {
      this.validPlayChecker.invalidateRow(row);
    }
    if (colCount == 2) {
      this.validPlayChecker.invalidateCol(col);
    }
    if (nbhCount == 2) {
      this.validPlayChecker.invalidateNbh(nbh);
    }
    return true;
  }

  @Override
  public Integer lastMove() {
    if (this.movesMade == 0) {
      return null;
    }
    return this.moves[this.movesMade - 1];
  }

  @Override
  public List<Integer> moves() {
    final List<Integer> res = new ArrayList<>(movesMade);
    for (int intValue : this.moves) {
      res.add(intValue);
    }
    return res;
  }

  @Override
  public boolean isSolved() {
    return this.counts == 0b101010101010101010101010101010101010101010101010101010L;
  }

  @Override
  public CityPlan clone() {
    return new CityPlan(this.counts, this.moves, this.movesMade,
        this.neighborhoods, this.nbhLookup.copy(),
        this.validPlayChecker.copy());
  }

  public void debug() {
    System.out.println(Arrays.toString(this.moves));
    this.validPlayChecker.debug();
  }

  public int getNeighborhood(int idx) {
    return this.nbhLookup.getNeighborhood(idx);
  }

  public int rowCount(int row) {
    return count(ROW_OFFSET, row);
  }

  public int colCount(int col) {
    return count(COL_OFFSET, col);
  }

  public int nbhCount(int nbh) {
    return count(NBH_OFFSET, nbh);
  }

  public void validMoveMap(int[] buffer) {
    System.arraycopy(this.validPlayChecker.data, 0, buffer, 0, 3);
  }

  private int incrementRow(int row) {
    int res = rowCount(row);
    if (res < 2) {
      incrementCount(ROW_OFFSET, row);
      res++;
    }
    return res;
  }

  private int incrementCol(int col) {
    int res = colCount(col);
    if (res < 2) {
      incrementCount(COL_OFFSET, col);
      res++;
    }
    return res;
  }

  private int incrementNbh(int nbh) {
    int res = nbhCount(nbh);
    if (res < 2) {
      incrementCount(NBH_OFFSET, nbh);
      res++;
    }
    return res;
  }

  private int count(int offset, int idx) {
    final int shift = BITS_PER_COUNT * (offset * ROWS + idx);
    return (int) ((this.counts & (COUNT_MASK << shift)) >>> shift);
  }

  private void incrementCount(int offset, int idx) {
    this.counts += (1L << (BITS_PER_COUNT * (offset * ROWS + idx)));
  }

  private static final int ROWS = 9;
  private static final int COLS = ROWS;
  private static final int CELLS = ROWS * COLS;
  private static final int NBHS = 9;
  private static final int BITS_PER_COUNT = 2;
  private static final long COUNT_MASK = (1L << (BITS_PER_COUNT)) - 1L;
  // TODO: enumify these
  private static final int ROW_OFFSET = 0;
  private static final int COL_OFFSET = 1;
  private static final int NBH_OFFSET = 2;

  /**
   * Reasonably compact (576-bit) mapping from tile to neighborhood.
   */
  private static class NeighborhoodLookup {

    private final long[] data;

    private NeighborhoodLookup(long[] data) {
      this.data = Arrays.copyOf(data, data.length);
    }

    private NeighborhoodLookup(int[][] data) {
      if (data.length != NBHS) {
        throw new CityPlanException("[ERROR] Must seed exactly " + NBHS + " neighborhoods");
      }
      this.data = new long[ROWS];
      int remaining = CELLS;
      for (int i = 0; i < data.length; i++) {
        final int[] nbh = data[i];
        for (int j = 0; j < nbh.length; j++) {
          final int idx = nbh[j];
          final int row = idx / ROWS,
              col = idx % COLS;
          if (readBits(this.data[row], col) != 0) {
            throw new CityPlanException("[ERROR] Tile " + idx + " belongs to multiple neighborhoods");
          }
          final long encodedBits = 1L + i;
          this.data[row] = this.data[row] | (encodedBits << (BITS_PER_ENTRY * col));
          remaining--;
        }
      }
      if (remaining != 0) {
        throw new CityPlanException("[ERROR] Some tiles belong to no neighborhoods");
      }
    }

    private int getNeighborhood(int idx) {
      return getNeighborhood(this.data[idx / ROWS], idx % COLS);
    }

    private NeighborhoodLookup copy() {
      return new NeighborhoodLookup(this.data);
    }

    private static int readBits(long base, int col) {
      final long offset = BITS_PER_ENTRY * col;
      return (int) ((base & (BASE_MASK << offset)) >>> offset);
    }

    private static int getNeighborhood(long base, int col) {
      return readBits(base, col) - 1;
    }

    private static final int BITS_PER_ENTRY = 4;
    private static final long BASE_MASK = (1L << BITS_PER_ENTRY) - 1L;

  }

  /**
   * Compact (96-bit) O(1) verifier of move legality.
   */
  private static class ValidPlayChecker {

    private final int[] data;
    private int[][] neighborhoods;

    private ValidPlayChecker(int[][] neighborhoods) {
      this.data = new int[WORDS];
      this.neighborhoods = neighborhoods;
    }

    private ValidPlayChecker(int[][] neighborhoods, int[] data) {
      this.data = data;
      this.neighborhoods = neighborhoods;
    }

    private boolean isAllowed(int idx) {
      final int word = this.data[idx / WORD_SIZE];
      return (word & (1 << (idx % WORD_SIZE))) == 0;
    }

    private void invalidate(int idx) {
      this.data[idx / WORD_SIZE] |= (1 << (idx % WORD_SIZE));
    }

    private void invalidateNeighbors(int idx) {
      invalidateAboveNeighbors(idx);
      invalidateBelowNeighbors(idx);
    }

    private void invalidateAboveNeighbors(int idx) {
      final int row = idx / 9,
          col = idx % 9;
      if (row != 0) {
        if (col != 0) {
          invalidate(idx - 10);
          invalidate(idx - 1);
        }
        if (col != 8) {
          invalidate(idx - 8);
          invalidate(idx + 1);
        }
        invalidate(idx - 9);
      }
    }

    private void invalidateBelowNeighbors(int idx) {
      final int row = idx / 9,
          col = idx % 9;
      if (row != 8) {
        if (col != 0) {
          invalidate(idx + 8);
        }
        if (col != 8) {
          invalidate(idx + 10);
        }
        invalidate(idx + 9);
      }
    }

    private void invalidateRow(int row) {
      this.data[row / WORDS] |= (ROW_MASK << ((row % WORDS) * COLS));
    }

    private void invalidateCol(int col) {
      for (int i = 0; i < WORDS; i++) {
        this.data[i] |= (COL_MASK << col);
      }
    }

    private void invalidateNbh(int idx) {
      for (int tile : this.neighborhoods[idx]) {
        invalidate(tile);
      }
    }

    private void debug() {
      for (int i = 0; i < 9; i++) {
        for (int j = 0; j < 9; j++) {
          if (isAllowed(9 * i + j)) {
            System.out.print('-');
          } else {
            System.out.print('*');
          }
        }
        System.out.println();
      }
    }

    private ValidPlayChecker copy() {
      return new ValidPlayChecker(this.neighborhoods, Arrays.copyOf(this.data, this.data.length));
    }

    private static final int WORD_SIZE = 27;
    private static final int WORDS = 3;
    private static final int ROW_MASK = (1 << COLS) - 1;
    private static final int COL_MASK; // 0b1000000001000000001;
    static {
      int res = 1;
      for (int i = 0; i < WORDS; i++) {
        res |= (1 << (i * COLS));
      }
      COL_MASK = res;
    }

  }

  private static class CityPlanException extends RuntimeException {

    CityPlanException(String msg) {
      super(msg);
    }

    CityPlanException(String msg, Throwable err) {
      super(msg, err);
    }

  }

}
