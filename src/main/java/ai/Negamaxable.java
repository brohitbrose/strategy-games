package ai;

import game.State;

/**
 * Mutable {@link State} with a computable negamax value.
 */
public interface Negamaxable<M> extends Minimaxable<M>, State<M> {

  int alpha();
  void alpha(int alpha);

  int beta();
  void beta(int beta);

  Negamaxable<M> cloneAndMove(M m);

  int terminalValue();

  /**
   * The negamax value of this {@link State}, with a default alpha-beta pruning
   * implementation.
   */
  default int negamaxValue(int color) {
    if (this.isOver()) {
      return color * terminalValue();
    }
    int bestSoFar = Integer.MIN_VALUE;
    for (M choice : this.validMoves()) {
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
  default int minimaxValue() {
    return negamaxValue(-1);
  }
}
