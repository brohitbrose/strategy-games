package stratgame.game;

public interface RandomPlayer<M> extends Player<M> {

  default M decide(State<M> state) {
    return state.validMoves().get((int) (Math.random() * state.validMoves().size()));
  }
}
