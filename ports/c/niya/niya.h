#ifndef NIYA_H_
#define NIYA_H_

#include <stdbool.h>
#include <stdint.h>

/*
 * Exported types.
 */

typedef enum { COLOR_RED, COLOR_BLACK, COLOR_NONE } Color;

typedef struct StateObj* State;

typedef struct PlayerObj* Player;

typedef struct GameObj* Game;


/*
 * Functions analogous to State methods.
 */

// Constructor.
State newState(uint64_t seed);

// Destructor.
void freeState(State *p);

// Returns the number of moves that have been played to achieve s.
int movesMade(State s);

// Fills moves with the valid moves on this turn. A value of 16 or more in moves
// marks the spot after the last real move. Ensure that moves has sufficient
// buffer size, i.e. the number of valid moves plus one for the sentinel.
void validMoves(State s, unsigned char *moves);

// Identifies whether m is currently a valid move in s.
// If it is, returns m's board position, otherwise returns a sentinel index that
// is "impossibly" greater than 15.
unsigned char validateDecision(State s, unsigned char m);

// If m is a valid move against the state encoded by s, then plays m against s,
// updates s accordingly, and returns true; otherwise, returns false.
bool makeMove(State s, unsigned char m);

// Returns whether s represents a final state of a Niya match.
bool isOver(State s);

// Displays a human-readable representation of s to stdout.
void debug(State s);

// Returns a new State identical to s.
State clone(State s);

// If s a non-tied final state, then returns the winning Color; otherwise,
// returns NONE.
Color winner(State s);


/*
 * Functions analogous to Player methods.
 */

// Constructor.
Player newRandomPlayer(Color color);

// Constructor.
Player newSmartPlayer(Color color);

// Destructor.
void freePlayer(Player *p);

// Returns a move to play against s given logic inherent to p.
unsigned char decide(Player p, State s);


/*
 * Functions analogous to Game methods
 */

// Constructor
Game newGame(bool redSmart, bool blackSmart);

// Constructor
Game newGameSeed(uint64_t seed, bool redSmart, bool blackSmart);

// Destructor
void freeGame(Game *p);

// Returns the Player who should move on this turn.
Player current(Game g);

// Returns the current State of g.
State state(Game g);

// Prompts current(g) to call decide(g->state).
void await(Game g);

// Starts g; may or may not call await(g) synchronously.
void start(Game g);

#endif
