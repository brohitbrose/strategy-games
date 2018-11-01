package game.niya;

public class Main {

  public static void main(String[] args) {
    new Niya(new SmartPlayer(Color.RED), new SmartPlayer(Color.BLACK)).start();
  }
}
