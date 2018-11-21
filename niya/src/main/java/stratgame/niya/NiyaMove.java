package stratgame.niya;

/**
 * Straightforward wrapper class for a desired play's row and column indices.
 */
class NiyaMove {

  int row;
  int col;

  NiyaMove(int row, int col) {
    if (row < 0 || col < 0 || row > 3 || col > 3) {
      throw new IllegalArgumentException("row and col must be in 0..=3");
    }
    this.row = row;
    this.col = col;
  }

  NiyaMove(NiyaMove m) { this(m.row, m.col); }

  @Override
  public String toString() {
    return "(" + row + "," + col + ")";
  }
}

