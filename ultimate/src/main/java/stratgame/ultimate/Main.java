package stratgame.ultimate;

import stratgame.game.RandomPlayer;

public class Main {

  public static void main(String[] args) {
    final Ultimate match = new Ultimate(new RandomPlayer<Integer>(){}, new RandomPlayer<Integer>(){});
    match.start();
    System.out.println(match.winner());
  }
}
