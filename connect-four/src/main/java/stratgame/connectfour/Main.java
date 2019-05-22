package stratgame.connectfour;

import stratgame.game.RandomPlayer;

public class Main {

  public static void main(String[] args) {
    final C4 game = new C4(
        new RandomPlayer<>() {},
        new RandomPlayer<>() {}
      );
    game.start();
    System.out.println(game.winner() + " won in " + game.state().movesMade() + " moves");
  }
}
