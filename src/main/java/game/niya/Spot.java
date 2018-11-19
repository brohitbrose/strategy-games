package game.niya;

class Spot {

  enum Plant { MAPLE, CHERRY, PINE, IRIS }
  enum Poem { SUN, BIRD, RAIN, PAPER }

  final Plant plant;
  final Poem poem;
  private final int idx;
  Color color;

  public Spot(int plant, int poem) {
    this.plant = Plant.values()[plant];
    this.poem = Poem.values()[poem];
    idx = (plant << 2) + poem;
    this.color = Color.NONE;
  }

  Spot(int idx) {
    this(idx >> 2, idx & 3);
  }

  Spot(Spot s) {
    this.plant = s.plant;
    this.poem = s.poem;
    this.color = s.color;
    this.idx = s.idx;
  }

  @Override
  public String toString() {
    return " (" + plant + ", " + poem + ")";
  }
}
