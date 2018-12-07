package stratgame.ai;

import stratgame.game.State;

public abstract class NegamaxView<M, P, S extends State<M>> implements Negamaxable<M> {

  protected int alpha;
  protected int beta;
  protected final P color;
  protected S state;

  /**
   * Constructs a new {@code View} that is the result of playing {@code m} in
   * {@code state}, assuming that {@code state} was bound by {@code alpha} and
   * {@code beta}, and updates {@code this.alpha} and {@code this.beta}
   * accordingly.
   */
  @SuppressWarnings("unchecked")
  public NegamaxView(S state, P color, int alpha, int beta, M m) {
    this.color = color;
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
  public NegamaxView(NegamaxView<M, P, S> view, M m) {
    this.color = view.color;
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
}
