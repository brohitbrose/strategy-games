package stratgame.tictactoe;

import stratgame.ai.NegamaxPlayer;
import stratgame.ai.NegamaxView;
import stratgame.game.Player;
import stratgame.game.State;

/**
 * {@link Player} that uses a minimax strategy to decide plays.
 */
public class SmartPlayer extends NegamaxPlayer<Integer, Piece, TTTState, View> {

  public SmartPlayer(Piece piece) {
    if (piece == Piece.NONE) {
      throw new IllegalArgumentException("Player piece cannot be NONE");
    }
    this.piece = piece;
  }

  @Override
  public View candidate(State<Integer> trueState, Piece p, int alpha, int beta, Integer choice) {
    return new View(trueState, p, alpha, beta, choice);
  }
}

class View extends NegamaxView<Integer, Piece, TTTState> {

  View(TTTState trueState, Piece p, int alpha, int beta, Integer choice) {
    super(trueState, p, alpha, beta, choice);
  }

  View(State<Integer> trueState, Piece p, int alpha, int beta, Integer choice) {
    this((TTTState) trueState, p, alpha, beta, choice);
  }

  View(View view, Integer choice) {
    super(view, choice);
  }

  @Override
  public View cloneAndMove(Integer m) {
    return new View(this, m);
  }

  @Override
  public int terminalValue() {
    final int val = 9 - state.movesMade() + 1;
    return state.winner() == Piece.NONE ? 0 :
        state.winner() == color ?
            val : -val;
  }
}
