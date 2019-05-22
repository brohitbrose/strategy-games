package stratgame.game;

public interface RandomPlayer<M, P> extends Player<M, P> {

  default M decide(State<M, P> state) {
    return state.validMoves().get((int) (Math.random() * state.validMoves().size()));
  }
}
