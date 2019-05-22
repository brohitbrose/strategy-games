package stratgame.niya;

public class Main {

  public static void main(String[] args) {
    final Niya niya = new Niya(new SmartPlayer(Color.RED), new SmartPlayer(Color.BLACK));
    final long then = System.currentTimeMillis();
    niya.start();
    System.out.println("finished in " + (System.currentTimeMillis() - then) + " ms");
    System.out.println(niya.winner());
  }
}
