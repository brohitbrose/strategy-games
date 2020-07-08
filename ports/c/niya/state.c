#include "niya.h"

#include <assert.h>
#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

// =============================================================================
// Data type definition
// =============================================================================
/*
 * StateObj fields:
 * 
 *  - board
 *    Encodes each tile board[i] (0 <= i < 16) of the 4x4 grid. Increasing i
 *    corresponds to a left-right movement before top-down, e.g. (zero-indexed)
 *    row 2, column 3 corresponds to i=11.
 *    The only legal values for the four left bits are 0 (unclaimed), 1
 *    (claimed by COLOR_RED), and 2 (claimed by COLOR_BLACK).
 *    The four right bits encode the plant and poem of this tile, with the left
 *    two encoding the plant and right two encoding the poem.
 *  - validMoves
 *    Again encodes each tile, this time indexed by an encoded plant and poem
 *    (using the same logic as the right four bits of each board[i]), and with
 *    the right four bits storing the index.
 *    The logic for each set of left four bits is identical to that of board.
 *  - movesMade
 *    The number of moves played in this state so far.
 *  - prev
 *    Encodes, just as before, the plant and poem of the last move played.
 *  - winner
 *    The winning Color in the current state.
 *  - redCover
 *    See below.
 *  - blackCover
 *    See below.
 * 
 * Quickly determining if a win condition has been met is tricky.  The basic
 * idea is that every time we mark a spot, we can increment running counts for
 * every row, column, diagonal, or square that contains this spot, and the game
 * is over as soon as any of these counts hits 4.  To model this without
 * collections, we reap the fact that storing 0 through 4 only requires three
 * bits, and with 19 unique win conditions (4 horizontals, 4 verticals, 2
 * diagonals, 9 squares), we only need 57 bits.  64 bits per color is plenty,
 * hence redCover and blackCover.
 *
 * For each 64-bit cover, each triple of bits, starting from the right,
 * represents
 * 
 *   - Each of four rows, from top to bottom
 *   - Each of four columns, from left to right
 *   - The downward diagonal, then the upward diagonal
 *   - Each 2x2 square, left to right within each row starting from the top
 *
 * respectively.
 *
 * So, for example, a cover value of
 *
 *   001,010,001,001,010,001,001,001,010;
 *   001,011;
 *   001,001,010,001;
 *   001,001,001,010
 *
 * indicates that exactly:
 *
 *   - 1 spot was filled in each row but the top; each column but the second
 *   (from the left); the upward diagonal; and the top-center, middle-left,
 *   middle-right, bottom-left, and bottom-right 2x2 squares
 *   - 2 spots were filled in the top row, the second column, and all other
 *   2x2 squares
 *   - 3 spots were filled in the downward diagonal.
 * 
 * Initially, cover is 0. Whenever a piece is placed, the correct bit triples in
 * cover must be incremented by 1. Since we know exactly which triples to
 * increment for a given move, we can achieve this easily by adding the correct
 * magic number (really just a 64-bit integer that contains the sequence 001 in
 * the right places) to cover. Because 4 is conveniently a power of 2 and we
 * enforce that none of our bit triples exceeds 4 in value, checking for a win
 * is as simple as masking cover against 0000000100100100...100100 and receiving
 * a nonzero result.
 */

#define TILE_COUNT 16
#define INVALID_MOVE 16
#define WIN_MASK 0x124924924924924LL
#define PLANT_MASK 12
#define PLANT_SHIFT 2
#define POEM_MASK 3
#define COLOR_SHIFT 4
#define RED_MASK 16
#define BLACK_MASK 32


typedef struct StateObj {
  unsigned char board[TILE_COUNT]; // TODO: remove, redundant with validMoves
  unsigned char validMoves[TILE_COUNT];
  int movesMade;
  unsigned char prev;
  Color winner;
  int64_t redCover;
  int64_t blackCover;
} StateObj;

static const int64_t INCREMENTS[TILE_COUNT] = {
  0x41001001LL, 0x240008001LL, 0x1200040001LL, 0x1008200001LL,
  0x8040001008LL, 0x48241008008LL, 0x241208040008LL, 0x201000200008LL,
  0x1008000001040LL, 0x9048008008040LL, 0x48240001040040LL, 0x40200000200040LL,
  0x1000008001200LL, 0x9000000008200LL, 0x48000000040200LL, 0x40000001200200LL
};


// =============================================================================
// Constructors
// =============================================================================

State newState(uint64_t seed) {
  // TODO: sanity check seed.
  State state = malloc(sizeof(StateObj));
  int i;
  unsigned char cur;
  for (i = 0; i < TILE_COUNT; i++) {
    cur = (unsigned char) (seed & 0xFLL); // last 4 bits
    state->board[i] = cur;
    state->validMoves[cur] = i;
    seed = (seed >> 4LL); // repeat with next 4-bit set
  }
  state->movesMade = 0;
  state->prev = INVALID_MOVE;
  state->winner = COLOR_NONE;
  state->redCover = 0LL;
  state->blackCover = 0LL;
  return state;
}


// =============================================================================
// Destructor
// =============================================================================

void freeState(State *p) {
  if (p != NULL && *p != NULL) {
    free(*p);
    *p = NULL;
  }
}


// =============================================================================
// Internal functions
// =============================================================================

// debug() helper that writes a single character to stdout.
// If the spot at row i, column j in s is claimed by RED, then writes R.
// If it is claimed by BLACK, then writes B.
// If it is unclaimed, then writes _.
static void writeSpotColor(State state, int i, int j) {
  const int idx = (i << 2) | j;
  const unsigned char entry = state->board[idx];
  const int color = entry >> COLOR_SHIFT;
  switch (color) {
    case 0:
      printf("_");
      break;
    case 1:
      printf("R");
      break;
    case 2:
      printf("B");
      break;    
    default:
      printf("[ERROR] Bad debug color\n");
      exit(-1);
  }
}

// Returns whether s is completely covered.
static inline bool empty(State s) {
  return s->movesMade == TILE_COUNT;
}

// Returns whether Red is due for the next turn in s.
static inline bool redTurn(State s) {
  return (s->movesMade & 1) == 0;
}

// Checks whether a "strict" win-condition has been met (i.e. without forcing
// the opponent into a no-move situation) and updates internal fields in s
// accordingly.
static void checkStrictWinner(State s, long cover) {
  if ((cover & WIN_MASK) != 0L) {
    s->winner = redTurn(s) ? COLOR_RED : COLOR_BLACK;
  }
}

// Sets s->winner to the color that moved last if unclaimed tiles exist but
// valid moves do not.
static void checkValidMoves(State s) {
  if (!isOver(s)) {
    int i;
    unsigned char plant = (s->prev & PLANT_MASK) >> PLANT_SHIFT,
      poem = s->prev & POEM_MASK;
    for (i = 0; i < 4; i++) {
      // validMoves[j] with j in 0..=15 corresponds to a tile with
      //   plant = j <floordiv> 4, poem = j mod 4.
      // So if the previous move has some plant and poem, then the next move
      // must be an unclaimed tile at one of the indices
      //   4 * plant + i, or
      //   poem + 4 * i
      // with i in 0..=3.
      if (s->validMoves[(plant << 2) + i] < INVALID_MOVE 
          || s->validMoves[poem + (i << 2)] < INVALID_MOVE) {
        return;
      }
    }
    s->winner = redTurn(s) ? COLOR_BLACK : COLOR_RED;    
  }
}


// =============================================================================
// Exported functions
// =============================================================================

// Returns the number of moves that have been played to achieve s.
int movesMade(State s) {
  return s->movesMade;
}

// Fills moves with the valid moves on this turn. A value of 16 or more in moves
// marks the spot after the last real move. Ensure that moves has sufficient
// buffer size, i.e. the number of valid moves plus one for the sentinel.
void validMoves(State s, unsigned char *moves) {
  if (s->movesMade == 0) { // first turn simply returns border moves
    for (int i = 0; i < 4; i++) {
      moves[i] = s->board[i]; // top row
      moves[i + 4] = s->board[i + 12]; // bottom row
    }
    // remaining border pieces
    moves[8] = s->board[4];
    moves[9] = s->board[7];
    moves[10] = s->board[8];
    moves[11] = s->board[11];
    // sentinel
    moves[12] = 16;
  } else {
    int n = 0;
    unsigned char plant = (s->prev & PLANT_MASK) >> PLANT_SHIFT,
      poem = s->prev & POEM_MASK;
    for (int i = 0; i < 4; i++) {
      // plant or poem match for all non-first turns
      int plantMatchIdx = (plant << 2) + i;
      if (s->validMoves[plantMatchIdx] < INVALID_MOVE) {
        moves[n++] = plantMatchIdx;
      } else {
        int poemMatchIdx = poem + (i << 2);
        if (s->validMoves[poemMatchIdx] < INVALID_MOVE) {
          moves[n++] = poemMatchIdx;
        }
      }
    }
    // sentinel
    moves[n] = 16;  
  }
}

// Identifies whether m is currently a valid move in s.
// If it is, returns m's board position, otherwise returns a sentinel index that
// is "impossibly" over 15.
unsigned char validateDecision(State s, unsigned char m) {
  unsigned char idx = s->validMoves[m];
  if (s->prev >= INVALID_MOVE) {  // first move must be on the border
    return ((idx >> 2) * (idx & 3)) % 3 == 0 ? idx : INVALID_MOVE;
  } else if (idx >= INVALID_MOVE) { // spot cannot already be claimed
    // TODO: consider a separate macro from for this check's condition.
    return idx;
  } else { // m and s->prev must match either the plant or the poem symbol
    unsigned char mPlant = m & PLANT_MASK, pPlant = (s->prev) & PLANT_MASK,
      mPoem = m & POEM_MASK, pPoem = (s->prev) & POEM_MASK;
    return mPlant == pPlant || mPoem == pPoem ? idx : INVALID_MOVE;
  }
}

// If m is a valid move against the state encoded by s, then plays m against s,
// updates s accordingly, and returns true; otherwise, returns false.
bool makeMove(State s, unsigned char m) {
  unsigned char idx;
  if (!isOver(s) && (idx = validateDecision(s, m)) < INVALID_MOVE) {
    int64_t cover;
    unsigned char mask;
    if (redTurn(s)) {
      s->redCover += INCREMENTS[idx];
      mask = RED_MASK;
      cover = s->redCover;
    } else {
      s->blackCover += INCREMENTS[idx];
      mask = BLACK_MASK;
      cover = s->blackCover;
    }
    checkStrictWinner(s, cover);
    s->movesMade++;
    s->prev = m;
    s->validMoves[m] |= mask;
    s->board[idx] |= mask;
    checkValidMoves(s);
    return true;
  }
  return false;
}

// Returns whether s represents a final state of a Niya match.
bool isOver(State s) {
  return s->winner != COLOR_NONE || empty(s);
}

// Returns a new State identical to s.
State clone(State s) {
  State c = malloc(sizeof(StateObj));
  memcpy(c, s, sizeof(StateObj));
  return c;
}

// If s is a non-tied final state, then returns the winning Color; otherwise,
// returns COLOR_NONE.
Color winner(State s) {
  return s->winner;
}

// Displays a human-readable representation of s to stdout.
void debug(State state) {
  printf("previous plant: %d\n", (state->prev & PLANT_MASK) >> PLANT_SHIFT);
  printf("previous poem: %d\n", state->prev & POEM_MASK);
  int i, j;
  for (i = 0; i < 4; i++) {
    for (j = 0; j < 4; j++) {
      writeSpotColor(state, i, j);
    }
    printf("\n");
  }
  printf("winner: %d\n", state->winner);
  printf("---\n");
}
