package stratgame.niya;

public class Main {

  public static void main(String[] args) {
//    new Niya(new SmartPlayer(Color.RED), new SmartPlayer(Color.BLACK)).start();
    new Niya(new RandomPlayer(), new SmartPlayer(Color.BLACK)).start();
  }
}
