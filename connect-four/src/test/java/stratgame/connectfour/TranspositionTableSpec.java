package stratgame.connectfour;

import org.testng.annotations.Test;
import stratgame.game.Player;
import stratgame.game.RandomPlayer;
import static org.testng.Assert.assertEquals;

public class TranspositionTableSpec {

  @Test
  public void testWithRandomScore() {
    final C4State state = new C4State();
    final Player<Integer, Color> player = new RandomPlayer<>() {};
    for (int i = 0; i < 8; i++) {
      state.makeMove(player.decide(state));
    }
    final byte score = (byte) (Math.random() * 73 - 36);
    final TranspositionTable t = new TranspositionTable();
    t.put(state.key(), score, TranspositionTable.Flag.EXACT);
    final long digest = t.getDigest(state.key());
    assertEquals(TranspositionTable.flag(digest), TranspositionTable.Flag.EXACT);
    assertEquals(TranspositionTable.value(t.getDigest(state.key())), score);
  }
}
