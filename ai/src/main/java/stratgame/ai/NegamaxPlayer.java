package stratgame.ai;

import java.util.List;
import stratgame.game.Player;
import stratgame.game.State;

public abstract class NegamaxPlayer<M,
                                    P,
                                    S extends State<M>,
                                    N extends NegamaxView<M, P, S>>
    implements Player<M> {

  protected P piece;

  public NegamaxPlayer() { }

  public abstract N candidate(State<M> trueState, P piece, int alpha, int beta, M choice);

  @Override
  public M decide(State<M> trueState) {
    int bestValue = Integer.MIN_VALUE;
    M bestChoice = null;
    int alpha = -100;
    int beta = 100;
    final List<M> possible = trueState.validMoves();
    for (M choice : possible) {
      final NegamaxView<M, P, S> updatedState = candidate(trueState, this.piece, alpha, beta, choice);
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

}
