package stratgame.niya;

public class Main {

  public static void main(String[] args) {
    final Niya niya = new Niya(new SmartPlayer(Color.RED), new SmartPlayer(Color.BLACK));
    niya.start();
    System.out.println(niya.winner());
  }
}
