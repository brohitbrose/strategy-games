package game.niya;

import game.State;
import java.util.List;
import java.util.Scanner;

/**
 * Manual {@link NiyaPlayer} that prompts a user to pipe the desired move's row
 * and column into {@code stdin}.
 */
public class KeyboardPlayer implements NiyaPlayer {

  private final Scanner keyboard;

  public KeyboardPlayer() {
    keyboard = new Scanner(System.in);
  }

  @Override
  public NiyaMove decide(State<NiyaMove> s, List<NiyaMove> possible) {
    return new NiyaMove(keyboard.nextInt(), keyboard.nextInt());
  }
}
