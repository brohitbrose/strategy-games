package stratgame.ai;

import java.util.List;
import stratgame.game.Player;
import stratgame.game.State;

public abstract class NegaScoutPlayer<M, P, S extends State<M, P>>
    implements Player<M, S> {

  /**
   * The mark that identifies this {@code Player} in a game.
   */
  protected P piece;

  protected NegaScoutPlayer() { }

  public interface TranspositionTable<K , V> {
    void put(K key, V val);

    V get(K key);
  }
}
