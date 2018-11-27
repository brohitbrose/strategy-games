package stratgame.tictactoe;

import java.util.List;
import stratgame.ai.Negamaxable;
import stratgame.game.State;

/**
 * {@link TTTPlayer} that uses a minimax strategy to decide plays.
 */
public class SmartPlayer implements TTTPlayer {

  private final Piece piece;

  public SmartPlayer(Piece piece) {
    if (piece == Piece.NONE) {
      throw new IllegalArgumentException("Player piece cannot be NONE");
    }
    this.piece = piece;
  }

  @Override
  public Integer decide(State<Integer> trueState) {
    int bestValue = Integer.MIN_VALUE;
    int bestChoice = -1;
    int alpha = -100;
    int beta = 100;
    final List<Integer> possible = trueState.validMoves();
    for (int choice : possible) {
      final View updatedState = new View(trueState, this.piece, alpha, beta, choice);
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

/**
 * {@link Negamaxable} extension of {@link TTTState}.
 */
final class View extends TTTState implements Negamaxable<Integer> {

  private int alpha;
  private int beta;
  private final Piece color;

  /**
   * Constructs a new {@code View} that is the result of playing {@code m} in
   * {@code state}, assuming that {@code state} was bound by {@code alpha} and
   * {@code beta}, and updates {@code this.alpha} and {@code this.beta}
   * accordingly.
   */
  View(State<Integer> state, Piece color, int alpha, int beta, int m) {
    super(state);
    this.color = color;
    this.alpha = -beta;
    this.beta = -alpha;
    makeMove(m);
  }

  /**
   * Constructs a new {@code View} that is the result of playing {@code m} in
   * {@code view}, updating {@code alpha} and {@code beta} accordingly.
   */
  View(View view, int m) {
    super(view);
    this.color = view.color;
    this.alpha = -view.beta;
    this.beta = -view.alpha;
    makeMove(m);
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
  public Negamaxable<Integer> cloneAndMove(Integer m) {
    return new View(this, m);
  }

  @Override
  public int terminalValue() {
    final int val = 9 - this.movesMade() + 1;
    return this.winner() == Piece.NONE ? 0 :
        this.winner() == color ?
            val : -val;
  }

  @Override
  public int minimaxValue() {
    return negamaxValue(-1);
  }

  @Override
  public void move(Integer move) {
    makeMove(move);
  }
}
