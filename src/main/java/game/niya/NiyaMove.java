package game.niya;

import recon.Form;
import recon.Record;
import recon.Value;

public class NiyaMove {

  int row;
  int col;

  NiyaMove(int row, int col) {
    if (row < 0 || col < 0 || row > 3 || col > 3) {
      throw new IndexOutOfBoundsException("Row and Col must be in 0..=3");
    }
    this.row = row;
    this.col = col;
  }

  NiyaMove(NiyaMove m) { this(m.row, m.col); }

  @Override
  public String toString() {
    return "(" + row + "," + col + ")";
  }

  static final Form<NiyaMove> FORM = new Form<NiyaMove>() {
    @Override
    public String getTag() {
      return "move";
    }
    @Override
    public Class<?> getType() {
      return NiyaMove.class;
    }

    @Override
    public Value mold(NiyaMove move) {
      return Record.of().attr(getTag()).item(move.row).item(move.col);
    }

    @Override
    public NiyaMove cast(Value value) {
      if (getTag().equals(value.head().asAttr().getKey().stringValue())) {
        return new NiyaMove(value.getItem(1).intValue(), value.getItem(2).intValue());
      }
      return null;
    }
  };
}

