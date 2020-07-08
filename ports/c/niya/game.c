#include "niya.h"

#include <assert.h>
#include <stdbool.h>
#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <time.h> 

uint64_t seed();

// =============================================================================
// Data type definition
// =============================================================================

typedef struct GameObj {
  State state;
  Player red;
  Player black;
} GameObj;


// =============================================================================
// Constructor
// =============================================================================

Game newGameSeed(uint64_t stateSeed, bool redSmart, bool blackSmart) {
  Game game = malloc(sizeof(GameObj));
  game->state = newState(stateSeed);
  game->red = redSmart ? newSmartPlayer(COLOR_RED)
    : newRandomPlayer(COLOR_RED);
  game->black = blackSmart ? newSmartPlayer(COLOR_BLACK)
    : newRandomPlayer(COLOR_BLACK);
  return game;
}

Game newGame(bool redSmart, bool blackSmart) {
  return newGameSeed(seed(), redSmart, blackSmart);
}


// =============================================================================
// Internal functions
// =============================================================================

// Returns a randomly-shuffled, unplayed Niya board.
uint64_t seed() {
  char arr[16];
  for (int i = 0 ; i < 16; i++) {
    arr[i] = i;
  }
  for (int i = 16 - 1; i >= 1; i--) {
    srand(time(0));
    int j = rand() % (i + 1);
    char tmp = arr[i];
    arr[i] = arr[j];
    arr[j] = tmp;
  }
  uint64_t res = 0LL;
  for (int i = 0; i < 16; i++) {
    res |= (((uint64_t) arr[i] & 0xFLL) << (i << 2));
  }  
  return res;
}


// =============================================================================
// Destructor
// =============================================================================

void freeGame(Game *p) {
  if (p != NULL && *p != NULL) {
    Game g = *p;
    freePlayer(&(g->black));
    freePlayer(&(g->red));
    freeState(&(g->state));
    free(g);
    *p = NULL;
  }
}


// =============================================================================
// Exported functions
// =============================================================================

// Returns the Player who should move on this turn.
Player current(Game game) {
  return (movesMade(game->state) & 1) == 0 ? game->red : game->black;
}

// Prompts current(g) to call decide(g->state).
void await(Game game) {
  const State state = game->state;
  State copy = clone(state);
  if (makeMove(game->state, decide(current(game), copy))) {
    // TODO: consider expressing this logic as a separate lifecycle function.
    debug(state);
    if (!isOver(state)) {
      await(game);
    }
  } else {
    printf("[WARN] invalid move submitted this turn.\n");
  }
  freeState(&copy);
}

// Starts g.
void start(Game game) {
  await(game);
}
