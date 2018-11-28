package stratgame.tictactoe;

public class Main {

  public static void main(String[] args) {
//    final java.util.Scanner keyboard = new java.util.Scanner(System.in);
//    final TTTPlayer kPlayer = new TTTPlayer() {
//        @Override
//        public Integer decide(stratgame.game.State<Integer> state, java.util.List<Integer> possible) {
//          return keyboard.nextInt();
//        }
//      };
    final TicTacToe ttt = new TicTacToe(new SmartPlayer(Piece.X), new SmartPlayer(Piece.O));
    ttt.start();
    System.out.println(ttt.winner());
  }
}
