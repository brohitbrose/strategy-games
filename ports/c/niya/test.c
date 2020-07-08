#include "niya.h"

#include <assert.h>

// =============================================================================
// State-focused test cases
// =============================================================================

static void testHorizontalWinConditions() {
  State state;

  // first row
  state = newState(0x2E9A71BF3D486C50LL);
  makeMove(state, 0); makeMove(state, 2);
  makeMove(state, 6); makeMove(state, 14);
  makeMove(state, 12); makeMove(state, 13);
  makeMove(state, 5);
  assert(winner(state) == COLOR_RED);
  freeState(&state);

  // second row
  state = newState(0x63EBA840C92D5F17LL);
  makeMove(state, 1); makeMove(state, 2);
  makeMove(state, 14); makeMove(state, 13);
  makeMove(state, 5); makeMove(state, 9);
  makeMove(state, 8); makeMove(state, 12);
  assert(winner(state) == COLOR_BLACK);
  freeState(&state);

  // third row
  state = newState(0x14206D38EAC97FB5LL);
  makeMove(state, 8); makeMove(state, 0);
  makeMove(state, 3); makeMove(state, 7);
  makeMove(state, 6); makeMove(state, 10);
  makeMove(state, 11); makeMove(state, 15);
  makeMove(state, 13);
  assert(winner(state) == COLOR_RED);
  freeState(&state);

  // fourth row
  state = newState(0x5842AED9763CB10FLL);
  makeMove(state, 8); makeMove(state, 12);
  makeMove(state, 4); makeMove(state, 6);
  makeMove(state, 5); makeMove(state, 13);
  makeMove(state, 14); makeMove(state, 10);
  makeMove(state, 2);
  assert(winner(state) == COLOR_RED);
  freeState(&state);  
}

static void testVerticalWinConditions() {

}

static void testDiagonalWinConditions() {
  
}

static void testSquareWinConditions() {
  
}

static void testCheckmateWinConditions() {
  
}

static void stateSpec() {
  testHorizontalWinConditions();
  testVerticalWinConditions();
  testDiagonalWinConditions();
  testSquareWinConditions();
  testCheckmateWinConditions();
}

// =============================================================================
// Player-focused test cases
// =============================================================================

static void playerSpec() {

}

// =============================================================================
// Game-focused test cases
// =============================================================================

static void gameSpec() {
  Game g = newGame(true, true);
  start(g);
  freeGame(&g);
}

// =============================================================================
// Test driver
// =============================================================================

int main(int argc, char **argv) {
  stateSpec();
  playerSpec();
  gameSpec();
}
