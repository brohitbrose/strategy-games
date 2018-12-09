package stratgame.niya;

/**
 * Straightforward wrapper enum for a desired play's row and column indices.
 */
enum NiyaMove {

  ZERO_ZERO (0, 0),
  ZERO_ONE (0, 1),
  ZERO_TWO (0, 2),
  ZERO_THREE (0, 3),
  ONE_ZERO (1, 0),
  ONE_ONE (1, 1),
  ONE_TWO (1, 2),
  ONE_THREE (1, 3),
  TWO_ZERO (2, 0),
  TWO_ONE (2, 1),
  TWO_TWO (2, 2),
  TWO_THREE (2, 3),
  THREE_ZERO (3, 0),
  THREE_ONE (3, 1),
  THREE_TWO (3, 2),
  THREE_THREE (3, 3);

  final int row;
  final int col;

  NiyaMove(int row, int col) {
    this.row = row;
    this.col = col;
  }

  static NiyaMove from(int row, int col) {
    if ((row & 3) != row || (col & 3) != col) {
      throw new IllegalArgumentException("row, col must be in 0..=3");
    }
    return NiyaMove.values()[(row << 2) + col];
  }
}
