package game.niya;

public class Main {

  public static void main(String[] args) {
    new Niya(new RandomPlayer(), new SmartPlayer(Color.BLACK)).start();
  }
}
