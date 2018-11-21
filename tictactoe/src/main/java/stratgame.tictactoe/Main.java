package stratgame.tictactoe;

public class Main {

  public static void main(String[] args) {
    new TicTacToe(new SmartPlayer(Piece.X), new SmartPlayer(Piece.O)).start();
//    new TicTacToe(new SmartPlayer(Piece.X), new RandomPlayer()).start();
//    new TicTacToe(new RandomPlayer(), new SmartPlayer(Piece.O)).start();

//    final java.util.Scanner keyboard = new java.util.Scanner(System.in);
//    new TicTacToe(new TTTPlayer() {
//        @Override
//        public Integer decide(stratgame.game.State<Integer> state, java.util.List<Integer> possible) {
//          return keyboard.nextInt();
//        }
//      }, new SmartPlayer(Piece.O)).start();
  }
}
