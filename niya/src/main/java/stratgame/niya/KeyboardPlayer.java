package stratgame.niya;

import java.util.Scanner;
import stratgame.game.Player;
import stratgame.game.State;

/**
 * Manual {@link Player} that prompts a user to pipe the desired move's row and
 * column into {@code stdin}.
 */
public class KeyboardPlayer implements Player<NiyaMove> {

  private final Scanner keyboard;

  public KeyboardPlayer() {
    keyboard = new Scanner(System.in);
  }

  @Override
  public NiyaMove decide(State<NiyaMove> s) {
    NiyaMove decision;
    while (true) {
      try {
        decision = new NiyaMove(keyboard.nextInt(), keyboard.nextInt());
        break;
      } catch (IllegalArgumentException e) {
        System.out.println("Try again: " + e.getMessage());
      }
    }
    return decision;
  }
}
