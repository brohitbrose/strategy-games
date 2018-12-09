package stratgame.ai;

import java.util.List;
import stratgame.game.Player;
import stratgame.game.State;

/**
 * {@link Player} that uses a minimax strategy to decide plays.
 */
public abstract class NegamaxPlayer<M,
                                    P,
                                    S extends State<M>>
    implements Player<M> {

  /**
   * The mark that identifies this {@code Player} in a game.
   */
  protected P piece;

  public NegamaxPlayer() { }

  /**
   * Returns the {@code NegamaxView} that is the result of placing {@code piece}
   * in {@code trueState} at position {@code choice}, assuming that the current
   * negamax bounds are {@code alpha} and {@code beta}.
   */
  @Override
  @SuppressWarnings("unchecked")
  public final M decide(State<M> trueState) {
    int bestValue = Integer.MIN_VALUE;
    M bestChoice = null;
    int alpha = -100;
    int beta = 100;
    final List<M> possible = trueState.validMoves();
    for (M choice : possible) {
      final NegamaxView updatedState = new NegamaxView((S) trueState, alpha, beta, choice);
      final int nv = -updatedState.minimaxValue();
      if (nv > bestValue) {
        bestValue = nv;
        bestChoice = choice;
        alpha = bestValue;
        // Note that alpha will never match or exceed beta from the top level,
        // so we skip any fast-fail checks here.
      }
    }
    return bestChoice;
  }

  public abstract int terminalValue(S state);

  private class NegamaxView implements Negamaxable<M> {

    private int alpha;
    private int beta;
    private S state;

    /**
     * Constructs a new {@code View} that is the result of playing {@code m} in
     * {@code state}, assuming that {@code state} was bound by {@code alpha} and
     * {@code beta}, and updates {@code this.alpha} and {@code this.beta}
     * accordingly.
     */
    @SuppressWarnings("unchecked")
    NegamaxView(S state, int alpha, int beta, M m) {
      this.alpha = -beta;
      this.beta = -alpha;
      this.state = (S) state.clone();
      this.state.makeMove(m);
    }

    /**
     * Constructs a new {@code View} that is the result of playing {@code m} in
     * {@code view}, updating {@code alpha} and {@code beta} accordingly.
     */
    @SuppressWarnings("unchecked")
    NegamaxView(NegamaxView view, M m) {
      this.alpha = -view.beta;
      this.beta = -view.alpha;
      this.state = (S) view.state.clone();
      this.state.makeMove(m);
    }

    @Override
    public int alpha() {
      return alpha;
    }

    @Override
    public void alpha(int alpha) {
      this.alpha = alpha;
    }

    @Override
    public int beta() {
      return beta;
    }

    @Override
    public void beta(int beta) {
      this.beta = beta;
    }

    @Override
    public int negamaxValue(int color) {
      if (state.isOver()) {
        return color * terminalValue();
      }
      int bestSoFar = Integer.MIN_VALUE;
      for (M choice : state.validMoves()) {
        final Negamaxable<M> updatedState = cloneAndMove(choice);
        final int newValue = -updatedState.negamaxValue(-color);
        if (newValue > bestSoFar) {
          bestSoFar = newValue;
          if (bestSoFar > alpha()) {
            alpha(bestSoFar);
            if (alpha() >= beta()) break;
          }
        }
      }
      return bestSoFar;
    }

    @Override
    public void move(M move) {
      state.makeMove(move);
    }

    @Override
    public NegamaxView cloneAndMove(M m) {
      return new NegamaxView(this, m);
    }

    @Override
    public int terminalValue() {
      return NegamaxPlayer.this.terminalValue(state);
    }
  }
}
