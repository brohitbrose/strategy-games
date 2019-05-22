package stratgame.connectfour;

public class TranspositionTable {

  private final long[] arr;
  private static final int SIZE = 8391697;

  public TranspositionTable() {
    this.arr = new long[SIZE];
  }

  private int hash(long key) {
    return (int) (key % (long) SIZE);
  }

  public long getDigest(long key) {
    final int idx = hash(key);
    final long entry = arr[idx];
    if ((entry >>> 10) == key) {
      return entry;
    }
    return 0L;
  }

  public void put(long key, byte val, Flag flag) {
    final int idx = hash(key);
    final long entry = (key << 10) | (flag.val << 8) | (val & 0xFF);
    this.arr[idx] = entry;
  }

  public static Flag flag(long entry) {
    return Flag.values()[(int) ((entry & (0b11L << 8)) >>> 8)];
  }

  public static int value(long entry) {
    return (byte) (entry & 0xFFL);
  }

  public enum Flag {

    NONE(0), EXACT(1), LOWER(2), UPPER(3);

    final int val;

    Flag(int val) { this.val = val; }
  }
}
