package game.tictactoe;

public class Main {

  public static void main(String[] args) {
    new TicTacToe(new SmartPlayer(Piece.X), new SmartPlayer(Piece.O)).start();
//    new TicTacToe(new SmartPlayer(Piece.X), new RandomPlayer()).start();
//    new TicTacToe(new RandomPlayer(), new SmartPlayer(Piece.O)).start();
  }
}
