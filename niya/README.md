# Niya

Game loop, AI, and UI for the [Niya](https://geekdad.com/2015/02/niya/) board game.

The game loop and AI were initially written in Java. To enable a web UI, I piggybacked off the following projects

* [Java-WebSocket](https://github.com/TooTallNate/Java-WebSocket)
* [recon-java](https://github.com/swimit/recon-java)
* [swim-util-java](https://github.com/swimit/swim-util-java)

All of these dependencies, as well as a ton of serialization code, went away with a port to JS. An implementation that still uses, and may later take advantage of, these dependencies can be found in the [legacy/websocket](https://github.com/brohitbrose/niya/tree/legacy/websocket) branch.

UI shamelessly based off [2048](https://github.com/gabrielecirulli/2048), minus all of the animation code, because I can't even pretend to be good at either designing or pushing pixels.

All images taken from [Wikimedia Commons](https://commons.wikimedia.org/wiki/Main_Page).

## Run

Simply clone the repository, then open `index.html` in any browser.

Currently, the game loads with an (unbeatable, provided that it moves first) AI as the first player and a human as the second. To change this behavior, change the game constructor arguments accordingly in the following chunk of `assets/js/custom.js`:

```js
bindButton(".restart-button", () => {
  game = new Game("SMART", "CLIENT"); // edit this line
  game.start();
});
```

Valid modes are:

1. `"CLIENT"`: A user provides moves for this player via mouseclicks.
2. `"DUMB"`: An "AI" provides random, but valid, moves for this player.
3. `"SMART"`: An AI provides optimal moves for this player. It uses a non-depth-limited [negamax](https://en.wikipedia.org/wiki/Negamax) with [alpha-beta pruning](https://en.wikipedia.org/wiki/Alpha%E2%80%93beta_pruning).
