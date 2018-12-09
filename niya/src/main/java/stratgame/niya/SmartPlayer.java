package stratgame.niya;

import stratgame.ai.NegamaxPlayer;
import stratgame.ai.Negamaxable;
import stratgame.game.State;

/**
 * {@link NegamaxPlayer} for {@code NiyaStates}.
 */
public class SmartPlayer extends NegamaxPlayer<NiyaMove, Color, NiyaState> {

  /**
   * Constructs a new {@code SmartPlayer} with color {@code color}.
   */
  public SmartPlayer(Color color) {
    if (color == Color.NONE) {
      throw new IllegalArgumentException("Player color cannot be NONE");
    }
    this.piece = color;
  }

  @Override
  public int terminalValue(NiyaState s) {
    final int val = 16 - s.movesMade() + 1;
    return s.winner() == Color.NONE ? 0 :
        s.winner() == piece ?
            val : -val;
  }
}
