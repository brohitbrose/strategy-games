package stratgame.ultimate;

import java.util.ArrayList;
import java.util.List;
import stratgame.game.State;
import stratgame.tictactoe.Piece;
import stratgame.tictactoe.TTTState;

public class UltimateState implements State<Integer> {

  private int board;
  private int movesMade;
  private List<Integer> validMoves;
  private Piece winner;
  private Individual[] individuals;

  public UltimateState() {
    this.board = 0;
    this.movesMade = 0;
    this.validMoves = new ArrayList<>(81);
    for (int i = 0; i < 81; i++) {
      this.validMoves.add(i);
    }
    this.winner = Piece.NONE;
    this.individuals = new Individual[]{
        new Individual(), new Individual(), new Individual(),
        new Individual(), new Individual(), new Individual(),
        new Individual(), new Individual(), new Individual()
      };
  }

  @Override
  public int movesMade() {
    return movesMade;
  }

  @Override
  public List<Integer> validMoves() {
    return null;
  }

  @Override
  public boolean makeMove(Integer m) {
    return false;
  }

  @Override
  public boolean isOver() {
    return false;
  }

  @Override
  public void debug() {

  }
}

class Individual extends TTTState {

  Piece currentPiece;

  Individual() {
    // note: implicit call to super();
    this.currentPiece = Piece.NONE;
  }

  /**
   * Copy constructor.
   */
  Individual(Individual s) {
    super(s);
    this.currentPiece = s.currentPiece;
  }

  /**
   * Copy constructor.
   */
  Individual(State<Integer> s) {
    super(s);
    this.currentPiece = Piece.NONE;
  }

  void currentPiece(Piece piece) {
    currentPiece = piece;
  }

  @Override
  protected Piece currentPiece(Integer nextMove) {
    return nextMove < 0 || nextMove > 8 // out of bounds
        || isOver() // match is over
        || ((3 << (nextMove << 1)) & board()) != 0 ? // spot is occupied
          Piece.NONE : currentPiece;
  }
}
