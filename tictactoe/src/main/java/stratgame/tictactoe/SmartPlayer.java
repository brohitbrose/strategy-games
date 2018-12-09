package stratgame.tictactoe;

import stratgame.ai.NegamaxPlayer;
import stratgame.game.State;

/**
 * {@link NegamaxPlayer} for {@code TTTStates}.
 */
public class SmartPlayer extends NegamaxPlayer<Integer, Piece, TTTState> {

  public SmartPlayer(Piece piece) {
    if (piece == Piece.NONE) {
      throw new IllegalArgumentException("Player piece cannot be NONE");
    }
    this.piece = piece;
  }

  @Override
  public int terminalValue(TTTState s) {
    final int val = 9 - s.movesMade() + 1;
    return s.winner() == Piece.NONE ? 0 :
        s.winner() == piece ?
            val : -val;
  }
}
