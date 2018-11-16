package game.tictactoe;

import ai.Negamaxable;
import game.State;
import java.util.List;

public class SmartPlayer implements TTTPlayer {

  private final Piece piece;

  public SmartPlayer(Piece piece) {
    if (piece == Piece.NONE) throw new IllegalArgumentException();
    this.piece = piece;
  }

  @Override
  public Integer decide(State<Integer> trueState, List<Integer> possible) {
    int bestValue = Integer.MIN_VALUE;
    int bestChoice = -1;
    int alpha = -100;
    int beta = 100;
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

final class View extends TTTState implements Negamaxable<Integer> {

  private int alpha;
  private int beta;
  private final Piece color;

  View(State<Integer> state, Piece color, int alpha, int beta, int m) {
    super(state);
    this.color = color;
    this.alpha = -beta;
    this.beta = -alpha;
    makeMove(m);
  }

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
