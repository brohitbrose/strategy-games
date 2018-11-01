# Niya

Game loop, AI, and UI for the [Niya](https://geekdad.com/2015/02/niya/) board game.

The game loop and AI were initially written in Java. To enable a web UI, I piggybacked off the following projects

* [Java-WebSocket](https://github.com/TooTallNate/Java-WebSocket)
* [recon-java](https://github.com/swimit/recon-java)
* [swim-util-java](https://github.com/swimit/swim-util-java)

All of these dependencies, as well as a ton of serialization code, can go away with a port to JS.

UI shamelessly based off [2048](https://github.com/gabrielecirulli/2048), minus all of the animation code, because I can't even pretend to be good at either designing or pushing pixels.

All images taken from [Wikimedia Commons](https://commons.wikimedia.org/wiki/Main_Page)

## Run

Prequisites:

* Java
* A browser (e.g. Chrome, Safari)
* A shell (e.g. Terminal, Command Prompt)

### Mac/Linux

1. Clone the repository to your system
2. From a shell pointed to your local repository, run `niya/bin/niya`
3. Open `index.html` in your browser.

### Windows

1. Clone the repository to your system
2. From a shell pointed to your local repository, run `niya/bin/niya`
3. Open `index.html` in your browser.
