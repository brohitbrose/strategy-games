# Niya

Game loop, AI, and UI for the Niya board game.

## Gameplay

### Pieces

- 16 tiles
  - Each tile contains exactly one "plant" symbol (maple, cherry, pine, iris)
  - Each tile contains exactly one "poetic" symbol (sun, bird, rain, paper)
  - Each tile is unique
- 8 red tokens
- 8 black tokens

### Rules

1. Arrange the tiles, preferably at random, in a 4x4 grid.
2. The red player moves first by replacing a tile with a red token. Only on this
first move, this tile *must* come from the border, i.e. the inner 2x2 square is
forbidden.
3. The black player moves second. Although the "border rule" no longer applies,
an even stricter rule is now in play *for the rest of the match*: every move
must now match either the "plant" or the "poem" of the move before it.
4. Players alternate turns until one of the following conditions is met:
    - A move forms a complete row, column, diagonal, or 2x2 square of one color.
    The player who made this move wins.
    - No more moves can be made. If there are empty spots on the board, then the
    player who moved last wins. Otherwise, the game ends in a tie.

## Run

TODO
