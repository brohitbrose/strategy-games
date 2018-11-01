package game.niya;

import ai.Minimaxable;
import java.util.List;

/**
 * {@link NiyaPlayer} that uses a minimax strategy to decide plays.
 */
public class SmartPlayer implements NiyaPlayer {

  /**
   * The {@link Color} of this {@code Player}, either {@code RED} or {@code
   * BLACK}.
   */
  private final Color color;

  /**
   * Constructs a new {@code SmartPlayer} whose color is {@code color}.
   */
  public SmartPlayer(Color color) {
    if (color == Color.NONE) throw new IllegalArgumentException();
    this.color = color;
  }

  @Override
  public NiyaMove decide(NiyaState trueState, List<NiyaMove> possible) {
    int bestValue = Integer.MIN_VALUE;
    NiyaMove bestChoice = null;
    for (NiyaMove choice: possible) {
      final View updatedState = new View(trueState, this.color);
      updatedState.move(choice);
      final int nv = -updatedState.minimaxValue();
      if (nv > bestValue) {
        bestValue = nv;
        bestChoice = choice;
      }
    }
    return bestChoice;
  }
}

final class View extends NiyaState implements Minimaxable<NiyaMove> {

  private final Color color;

  View(NiyaState state, Color color) {
    super(state);
    this.color = color;
  }

  View (View view) {
    super(view);
    this.color = view.color;
  }

  private int terminalValue() {
    final int val = 16 - this.movesMade() + 1;
    return this.winner() == Color.NONE ? 0 :
      this.winner() == color ?
        val : -val;
  }

  private int negamaxValue(int color, int alpha, int beta) {
    if (this.isOver()) {
      return color * terminalValue();
    }
    int bestSoFar = Integer.MIN_VALUE;
    for (NiyaMove choice: this.validMoves()) {
      final View updatedState = new View(this);
      updatedState.move(choice);
      bestSoFar = Math.max(-updatedState.negamaxValue(-color, -beta, -alpha), bestSoFar);
      alpha = Math.max(alpha, bestSoFar);
      if (alpha >= beta) break;
    }
    return bestSoFar;
  }

  @Override
  public int minimaxValue() {
    final int alpha = -100;
    final int beta = 100;
    return negamaxValue(-1, -beta, -alpha);
  }

  @Override
  public void move(NiyaMove move) {
    makeMove(move);
  }
}
