package stratgame.connectfour;

import stratgame.game.Player;
import stratgame.game.State;

public class SmartPlayer implements Player<Integer, Color> {

  private final static int[] MOVES = new int[]{3, 4, 2, 5, 1, 6, 0};

  private final Color color;
  private final TranspositionTable table;

  public SmartPlayer(Color color) {
    this.color = color;
    this.table = new TranspositionTable();
  }

  @Override
  @SuppressWarnings("unchecked")
  public Integer decide(State<Integer, Color> state) {

    int bestValue = Integer.MIN_VALUE;
    Integer bestChoice = null;

    int alpha = -0x7FFFFFFF;
    int beta = 0x7FFFFFFF;

    final C4State trueState = (C4State) state;
    for (int choice : MOVES) {
      if (trueState.isValidMove(choice)) {
        if (trueState.willWin(choice)) {
          return choice;
        }
        final C4State updatedState = trueState.clone();
        updatedState.makeMove(choice);
        final int nv = -negamax(updatedState, -beta, -alpha, -1);
        if (nv > bestValue) {
          bestValue = nv;
          bestChoice = choice;
          alpha = bestValue;
        }
      }
    }
    return bestChoice;
  }

  @SuppressWarnings("unchecked")
  public int negamax(C4State node, int alpha, int beta, int color) {
    if (node.isOver()) {
      return color * terminalValue(node);
    }

    final int oldAlpha = alpha;

    final long digest = table.getDigest(node.key());
    switch (TranspositionTable.flag(digest)) {
      case EXACT:
        return TranspositionTable.value(digest);
      case LOWER:
        int value = TranspositionTable.value(digest);
        alpha = Math.max(alpha, value);
        if (alpha >= beta) return value;
        break;
      case UPPER:
        value = TranspositionTable.value(digest);
        beta = Math.min(beta, value);
        if (alpha >= beta) return value;
        break;
      default:
        break;
    }

    int bestSoFar = Integer.MIN_VALUE;
    for (int choice : MOVES) {
      if (node.isValidMove(choice)) {
        if (node.willWin(choice)) {
          return color * (42 - node.movesMade()) * (node.current() == this.color ? 1 : -1);
        }
        final C4State updatedState = node.clone();
        updatedState.makeMove(choice);
        final int newValue = -negamax(updatedState, -beta, -alpha, -color);
        if (newValue > bestSoFar) {
          bestSoFar = newValue;
          if (bestSoFar > alpha) {
            alpha = bestSoFar;
            if (alpha >= beta) {
              break;
            }
          }
        }
      }
    }

    final TranspositionTable.Flag flag;
    if (bestSoFar <= oldAlpha) {
      flag = TranspositionTable.Flag.UPPER;
    } else if (bestSoFar >= beta) {
      flag = TranspositionTable.Flag.LOWER;
    } else {
      flag = TranspositionTable.Flag.EXACT;
    }
    table.put(node.key(), (byte) bestSoFar, flag);
    return bestSoFar;
  }

  public int terminalValue(C4State s) {
    final int val = 43 - s.movesMade();
    return s.winner() == Color.NONE ? 0 :
        s.winner() == color ?
            val : -val;
  }
}
