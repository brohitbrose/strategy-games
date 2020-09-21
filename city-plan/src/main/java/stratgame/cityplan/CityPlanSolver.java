package stratgame.cityplan;

import stratgame.ai.Backtracker;

public final class CityPlanSolver extends Backtracker<Integer, CityPlan> {

  @Override
  public Backtracker.MoveSuggester<Integer, CityPlan> moveSuggester(CityPlan puzzle) {
    return new CityPlanIterator(puzzle);
  }

  private static class CityPlanIterator implements MoveSuggester<Integer, CityPlan> {

    private int idx;
    private final int[] data;

    private CityPlanIterator(CityPlan puzzle) {
      if (puzzle.lastMove() == null) {
        this.idx = 0;
      } else {
        this.idx = puzzle.lastMove();
      }
      this.data = new int[3];
      puzzle.validMoveMap(this.data);
      advanceIdx();
    }

    private void advanceIdx() {
      while (!shouldStop(this.idx)) {
        this.idx++;
      }
    }

    private boolean shouldStop(int idx) {
      if (idx >= 81) return true;
      final int word = this.data[idx / WORD_SIZE];
      return (word & (1 << (idx % WORD_SIZE))) == 0;
    }

    @Override
    public boolean hasNext() {
      return this.idx < 81;
    }

    @Override
    public Integer next() {
      int res = this.idx;
      this.idx++;
      advanceIdx();
      return res;
    }

    @Override
    public void seed(CityPlan puzzle) {
      puzzle.validMoveMap(this.data);
      this.idx = puzzle.lastMove();
      advanceIdx();
    }

    private void debug() {
      for (int i = 0; i < 9; i++) {
        for (int j = 0; j < 9; j++) {
          if (shouldStop(9 * i + j)) {
            System.out.print('-');
          } else {
            System.out.print('*');
          }
        }
        System.out.println();
      }
    }

    private static final int WORD_SIZE = 27;
  }
}
