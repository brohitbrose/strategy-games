package stratgame.niya;

import stratgame.game.RandomPlayer;

public class Main {

  public static void main(String[] args) {
    final Niya niya = new Niya(new RandomPlayer<NiyaMove>() { }, new SmartPlayer(Color.BLACK));
    niya.start();
    System.out.println(niya.winner());
  }
}
