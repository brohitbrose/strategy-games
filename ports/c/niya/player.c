// TODO: remove benchmarks

#include "niya.h"

#include <assert.h>
#include <limits.h>
#include <stdio.h>
#include <stdlib.h>
#include <time.h>

// =============================================================================
// Data type definition
// =============================================================================

typedef struct PlayerObj {
  Color color;
  unsigned char (*decide)(Player, State);
} PlayerObj;


// =============================================================================
// Internal functions
// =============================================================================

static unsigned char decideRandom(Player p, State state) {
  int n = 0, i = 0;
  unsigned char moves[12];
  for (; i < 16; i++) {
    if (validateDecision(state, i) < 16) {
      moves[n++] = i;
    }
  }
  srand(time(0));
  return moves[rand() % n];
}


// Returns the negamax value of state relative to p, without evaluating any
// further subtrees.
static int terminalValue(Player p, State state) {
  int val = 16 - movesMade(state) + 1;
  return winner(state) == COLOR_NONE ? 0 :
    winner(state) == p->color ?
      val : -val;
}

// Returns the negamax value of state relative to p.
static int negamax(Player p, State state,
    int alpha, int beta, int color,
    int *count) {
  if (isOver(state)) {
    return color * terminalValue(p, state);
  }
  int bestSoFar = INT_MIN;
  int n = 16 - movesMade(state);
  unsigned char *possible = malloc(sizeof(unsigned char) * n);
  validMoves(state, possible);
  for (int i = 0; possible[i] < 16; i++) {
    State withMove = clone(state);
    makeMove(withMove, possible[i]);
    *count = (*count + 1);
    int nv = -negamax(p, withMove, -beta, -alpha, -color, count);
    if (nv > bestSoFar) {
      bestSoFar = nv;
      if (bestSoFar > alpha) {
        alpha = bestSoFar;
        if (alpha >= beta) {
          freeState(&withMove);
          break;
        }
      }
    }
    freeState(&withMove);
  }
  free(possible);
  return bestSoFar;
}

// Returns the optimal move for p to play in s using a negamax-based algorithm.
static unsigned char decideSmart(Player p, State state) {
  int bestValue = INT_MIN;
  unsigned char bestChoice = 16;
  int alpha = -INT_MAX;
  int beta = INT_MAX;
  int n = 16 - movesMade(state);
  unsigned char *possible = malloc(sizeof(unsigned char) * n);
  validMoves(state, possible);
  int count = 0;
  clock_t then = clock();
  for (int i = 0; possible[i] < 16; i++) {
    State withMove = clone(state);
    makeMove(withMove, possible[i]);
    count += 1;
    int nv = -negamax(p, withMove, -beta, -alpha, -1, &count);
    if (nv > bestValue) {
      bestValue = nv;
      bestChoice = possible[i];
      alpha = bestValue;
    }
    freeState(&withMove);
  }
  clock_t now = clock();
  double elapsed = (double) (now - then) / CLOCKS_PER_SEC;
  printf("Simulated %d moves in %f ms (%f mps)\n",
    count, elapsed * 1000, count / elapsed);
  free(possible);
  return bestChoice;
}


// =============================================================================
// Constructors
// =============================================================================

Player newRandomPlayer(Color color) {
  Player player = malloc(sizeof(PlayerObj));
  player->color = color;
  player->decide = decideRandom;
  return player;
}

Player newSmartPlayer(Color color) {
  Player player = malloc(sizeof(PlayerObj));
  player->color = color;
  player->decide = decideSmart;
  return player;
}


// =============================================================================
// Destructor
// =============================================================================

void freePlayer(Player *p) {
  if (p != NULL && *p != NULL) {
    free(*p);
    *p = NULL;
  }
}


// =============================================================================
// Exported functions
// =============================================================================

// Returns a move to play against s given logic inherent to p.
unsigned char decide(Player p, State s) {
  return p->decide(p, s);
}
