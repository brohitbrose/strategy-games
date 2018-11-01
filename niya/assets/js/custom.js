function idFromIndex(idx) {
  return "" + Math.floor(idx / 4) + (idx % 4);
}

// Webworkers

// LPT: Having trouble configuring your environment to allow multiple .js files?
// Just duplicate your model code, interpret your webworker logic as a String,
// and make that string addressable by a URL!

// TODO: don't do any of that. Requires, at minimum
// 1) a separate file for everything indented normally in smartWorkaround
// 2) ability to import everything indented excessively in smartWorkaround
function smartWorkaround(msg) {
                      const WINCONS = Object.freeze({
                        "0": [[1, 2, 3], [5, 10, 15], [4, 8, 12], [1, 4, 5]],
                        "1": [[2, 5, 6], [5, 9, 13]],
                        "2": [[3, 6, 7], [6, 10, 14]],
                        "3": [[6, 9, 12], [7, 11, 15]],
                        "4": [[5, 6, 7], [5, 8, 9]],
                        "5": [[6, 9, 10]],
                        "6": [[7, 10, 11]],
                        "8": [[9, 10, 11], [9, 12, 13]],
                        "9": [[10, 13, 14]],
                        "10": [[11, 14, 15]],
                        "12": [[13, 14, 15]]
                      });
                      function State() { }
                      State.prototype.init = function(initialState) {
                        this.movesMade = 0;
                        this.winner = "NONE";
                        this.board = initialState;
                        this.updateValidMoves();
                      };
                      State.prototype.serialize = function() {
                        const res = {};
                        res['winner'] = this.winner;
                        const board = [];
                        const length = this.board.length;
                        for (let i = 0; i < length; i++) {
                          board.push(this.board[i].serialize());
                        }
                        res['board'] = board;
                        res['movesMade'] = this.movesMade;
                        return res;
                      };
                      State.prototype.current = function() {
                        return this.movesMade % 2 == 0 ? "RED" : "BLACK";
                      };
                      State.prototype.getSpot = function(row, col) {
                        return this.board[(row<<2)+col];
                      };
                      State.prototype.validateDecision = function(m) {
                        if (!this.previous) return (m[0]*m[1]) % 3 === 0;
                        const candidate = this.getSpot(m[0],m[1]);
                        return candidate.color === "NONE" && 
                          (candidate.plant === this.previous.plant || candidate.poem === this.previous.poem);
                      };
                      State.prototype.updateValidMoves = function() {
                        this.validMoves = [];
                        if (this.winner !== "NONE") return;
                        if (this.hasRemaining()) {
                          for (let i = 0; i < 4; i++) {
                            for (let j = 0; j < 4; j++) {
                              const move = [i,j];
                              if (this.validateDecision(move)) {
                                this.validMoves.push(move);
                              }
                            }
                          }
                          if (this.validMoves.length === 0) {
                            this.winner = this.movesMade % 2 == 0 ? "BLACK" : "RED";
                          }
                        }
                      };
                      State.prototype.makeMove = function(m) {
                        if (this.validateDecision(m)) {
                          const spot = this.getSpot(m[0], m[1]);
                          spot.color = this.current();
                          this.checkForWinner();
                          this.movesMade++;
                          this.previous = spot;
                          this.updateValidMoves();
                          return true;
                        }
                        return false;
                      };
                      State.prototype.clone = function(state) {
                        var self = this;
                        this.movesMade = state.movesMade;
                        this.board = [];
                        for (let i = 0; i < state.board.length; i++) {
                          self.board[i] = new Spot(); self.board[i].clone(state.board[i]);
                        }
                        this.previous = state.previous;
                        this.winner = state.winner;
                        this.validMoves = [];
                        for (let move in state.validMoves) {
                          self.validMoves.push(state.validMoves[move]);
                        }
                      };
                      State.prototype.checkForWinner = function() {
                        var self = this;
                        const heads = Object.keys(WINCONS);
                        for (let j = heads.length - 1; j >= 0; j--) {
                          let key = heads[j];
                          const firstIdx = parseInt(key);
                          if (self.getSpot(firstIdx >> 2, firstIdx & 3).color === self.current()) {
                            const paths = WINCONS[key];
                            for (let i = paths.length - 1; i >= 0; i--) {
                              const path = paths[i];
                              var soFar = true;
                              for (let idx = path.length - 1; idx >= 0; idx--) {
                                const stone = path[idx];
                                if (self.getSpot(stone >> 2, stone & 3).color !== self.current()) {
                                  soFar = false;
                                  break;
                                }
                              }
                              if (soFar) {
                                self.winner = self.current();
                                return;
                              }
                            }
                          }
                        }
                      };
                      State.prototype.hasRemaining = function() {
                        return this.movesMade < 16;
                      };
                      function Spot(plant, poem) {
                        this.color = "NONE";
                        this.plant = plant;
                        this.poem = poem;
                        this.idx = (plant << 2) + poem;
                      }
                      Spot.prototype.serialize = function() {
                        return ({"color": this.color, "plant": this.plant, "poem": this.poem, "idx": this.idx});
                      };
                      Spot.prototype.clone = function (s) {
                        this.color = s.color;
                        this.plant = s.plant;
                        this.poem = s.poem;
                        this.idx = s.idx;
                      };  

  function deserializeSpot(json) {
    const res = new Spot(json.plant, json.poem);
    res.color = json.color;
    return res;
  }

  function deserializeState(json) {
    const res = new State();
    res.winner = json.winner;
    const board = [];
    for (let i in json.board) {
      board.push(json.board[i]);
    }
    res.movesMade = json.movesMade;
    res.board = board;
    if (json.previous) res.previous = deserializeSpot(json.previous);
    res.updateValidMoves();
    return res;
  }

  function makeSmartDecision(playerColor, trueState) {
    var bestValue = -100;
    var bestChoice = [];
    const alpha = -100;
    const beta = 100;
    const possible = trueState.validMoves;
    const length = possible.length;
    for (let i = 0; i < length; i++) {
      const updatedState = new State(); updatedState.clone(trueState);
      updatedState.makeMove(possible[i]);
      const nv = -negamaxValue(playerColor, updatedState, -1, -beta, -alpha);
      if (nv > bestValue) {
        bestValue = nv;
        bestChoice = possible[i];
      }
    }
    return bestChoice;
  }
  function negamaxValue(playerColor, state, color, alpha, beta) {
    if (state.winner !== "NONE" || !state.hasRemaining()) {
      return color * terminalValue(playerColor, state);
    }
    var bestSoFar = -100;
    const length = state.validMoves.length;
    for (let i = 0; i < length; i++) {
      const updatedState = new State(); updatedState.clone(state);
      updatedState.makeMove(state.validMoves[i]);
      bestSoFar = Math.max(-negamaxValue(playerColor, updatedState, -color, -beta, -alpha), bestSoFar);
      alpha = Math.max(alpha, bestSoFar);
      if (alpha >= beta) break;
    }
    return bestSoFar;
  }
  function terminalValue(playerColor, state) {
    const val = 16 - state.movesMade + 1;
    return state.winner == "NONE" ? 0 :
        state.winner == playerColor ? val :
          -val;
  }
  const state = deserializeState(msg.data.state);
  postMessage(makeSmartDecision(msg.data.color, state));
}

function dumbWorkaround(msg) {
  let nice = msg.data[Math.floor(Math.random() * msg.data.length)];
  postMessage(nice);
}

const smartWorker = new Worker(URL.createObjectURL(new Blob(["onmessage="+smartWorkaround.toString()], {type:"text/javascript"})));
smartWorker.onmessage = function(e) {
  if (game.status === "AWAITING_BACK") {
    game.makeMove(e.data);
  }
};
const dumbWorker = new Worker(URL.createObjectURL(new Blob(["onmessage="+dumbWorkaround.toString()], {type:"text/javascript"})));
dumbWorker.onmessage = function(e) {
  if (game.status === "AWAITING_BACK") {
    game.makeMove(e.data);
  }  
};

// Game Model
const WINCONS = Object.freeze({
  "0": [[1, 2, 3], [5, 10, 15], [4, 8, 12], [1, 4, 5]],
  "1": [[2, 5, 6], [5, 9, 13]],
  "2": [[3, 6, 7], [6, 10, 14]],
  "3": [[6, 9, 12], [7, 11, 15]],
  "4": [[5, 6, 7], [5, 8, 9]],
  "5": [[6, 9, 10]],
  "6": [[7, 10, 11]],
  "8": [[9, 10, 11], [9, 12, 13]],
  "9": [[10, 13, 14]],
  "10": [[11, 14, 15]],
  "12": [[13, 14, 15]]
});
function State() { }
State.prototype.init = function(initialState) {
  this.movesMade = 0;
  this.winner = "NONE";
  this.board = initialState;
  this.updateValidMoves();
};
State.prototype.serialize = function() {
  const res = {};
  res['winner'] = this.winner;
  const board = [];
  const length = this.board.length;
  for (let i = 0; i < length; i++) {
    board.push(this.board[i].serialize());
  }
  res['board'] = board;
  res['movesMade'] = this.movesMade;
  if (this.previous) res['previous'] = this.previous.serialize();
  return res;
};
State.prototype.current = function() {
  return this.movesMade % 2 == 0 ? "RED" : "BLACK";
};
State.prototype.getSpot = function(row, col) {
  return this.board[(row<<2)+col];
};
State.prototype.validateDecision = function(m) {
  if (!this.previous) return (m[0]*m[1]) % 3 === 0;
  const candidate = this.getSpot(m[0],m[1]);
  return candidate.color === "NONE" && 
    (candidate.plant === this.previous.plant || candidate.poem === this.previous.poem);
};
State.prototype.updateValidMoves = function() {
  this.validMoves = [];
  if (this.winner !== "NONE") return;
  if (this.hasRemaining()) {
    for (let i = 0; i < 4; i++) {
      for (let j = 0; j < 4; j++) {
        const move = [i,j];
        if (this.validateDecision(move)) {
          this.validMoves.push(move);
        }
      }
    }
    if (this.validMoves.length === 0) {
      this.winner = this.movesMade % 2 == 0 ? "BLACK" : "RED";
    }
  }
};
State.prototype.makeMove = function(m) {
  if (this.validateDecision(m)) {
    const spot = this.getSpot(m[0], m[1]);
    spot.color = this.current();
    this.checkForWinner();
    this.movesMade++;
    this.previous = spot;
    this.updateValidMoves();
    return true;
  }
  return false;
};
State.prototype.clone = function(state) {
  var self = this;
  this.movesMade = state.movesMade;
  this.board = [];
  for (let i = 0; i < state.board.length; i++) {
    self.board[i] = new Spot(); self.board[i].clone(state.board[i]);
  }
  // May need to change to deep copy later
  this.previous = state.previous;
  this.winner = state.winner;
  this.validMoves = [];
  for (let move in state.validMoves) {
    self.validMoves.push(state.validMoves[move]);
  }
};
State.prototype.checkForWinner = function() {
  var self = this;
  const heads = Object.keys(WINCONS);
  for (let j = heads.length - 1; j >= 0; j--) {
    let key = heads[j];
    const firstIdx = parseInt(key);
    if (self.getSpot(firstIdx >> 2, firstIdx & 3).color === self.current()) {
      const paths = WINCONS[key];
      for (let i = paths.length - 1; i >= 0; i--) {
        const path = paths[i];
        var soFar = true;
        for (let idx = path.length - 1; idx >= 0; idx--) {
          const stone = path[idx];
          if (self.getSpot(stone >> 2, stone & 3).color !== self.current()) {
            soFar = false;
            break;
          }
        }
        if (soFar) {
          self.winner = self.current();
          return;
        }
      }
    }
  }
};
State.prototype.hasRemaining = function() {
  return this.movesMade < 16;
};
function Spot(plant, poem) {
  this.color = "NONE";
  this.plant = plant;
  this.poem = poem;
  this.idx = (plant << 2) + poem;
}
Spot.prototype.serialize = function() {
  return ({"color": this.color, "plant": this.plant, "poem": this.poem, "idx": this.idx});
};
Spot.prototype.clone = function (s) {
  this.color = s.color;
  this.plant = s.plant;
  this.poem = s.poem;
  this.idx = s.idx;
};
function randomInit() {
  const arr = [];
  for (let i = 0; i < 16; i++) {
    arr.push(new Spot(i>>2,i&3));
  }
  for (let i = 16; i > 0; i--) {
    const backup = arr[i-1];
    const idx = (Math.floor) (Math.random() * i);
    arr[i-1] = arr[idx];
    arr[idx] = backup;
  }
  return arr;
}
// Game loop
function Game(redMode, blackMode, initialState) {
  this.status = "UNINITIALIZED";
  this.red = new Player("RED", redMode);
  this.black = new Player("BLACK", blackMode);
  // Verify initialState is valid, fallback to random otherwise
  var spots = [];
  if (initialState && initialState.length === 16) {
    const tmp = [];
    var uhOh = false;
    for (let i = 0; i < 16; i++) { tmp.push(false); }
    for (let idx = 0; idx < 16; idx++) {
      const tile = initialState[idx];
      if (tile < 0 || tile >= 16 || tmp[tile]) {
        spots = randomInit();
        uhOh = true;
        break;
      }
      tmp[tile] = true;
      spots[idx] = new Spot(tile>>2,tile&3);
    }
  } else {
    spots = randomInit();
  }
  this.state = new State(); this.state.init(spots);
}
Game.prototype.start = function() {
  view.drawGame();
  this.await(this.red);
};
const PLANT = ["MAPLE", "CHERRY", "PINE", "IRIS"];
const POEM = ["SUN", "BIRD", "RAIN", "PAPER"];
Game.prototype.await = function(p) {
  if (p.mode === "CLIENT") {
    this.status = "AWAITING_CLIENT";
  } else {
    this.status = "AWAITING_BACK";
    const stateCopy = new State();
    stateCopy.clone(this.state);
    switch (p.mode) {
      case ("SMART"):
        smartWorker.postMessage({"color": p.color, "state": stateCopy.serialize()});
        break;
      default:
        dumbWorker.postMessage(stateCopy.validMoves);
        break;
    }
  }
};
Game.prototype.current = function() {
  return this.state.current() === "RED" ? this.red : this.black;
};
Game.prototype.makeMove = function(m) {
  if (m && this.state.makeMove(m)) {
    if (game.state.previous) console.log(this.state.previous.color + ": " + PLANT[this.state.previous.plant] + ", " + POEM[this.state.previous.poem]);
    if (this.state.winner === "NONE" && this.state.hasRemaining()) {
      view.drawGame();
      this.await(this.current());
    } else {
      this.status = "DONE";
      view.drawDone();
    }
  }
};
Game.prototype.makeClientMove = function(row, col) {
  if (this.status === "AWAITING_CLIENT") {
    this.makeMove([row,col]);
  }
};

// Player logic
function Player(color, mode) {
  this.color = color;
  this.mode = mode;
}
// HTML Actuator
const view = {
  status: null,
  drawGame: function() {
    // Draw tiles
    // Would use for-in here, but it keeps flattening...
    for (let i = 0; i < game.state.board.length; i++) {
      const cell = document.getElementById(idFromIndex(i));
      cell.className = "grid-cell";
      switch (game.state.board[i].color) {
        case "RED":
          cell.innerHTML = "<img src=assets/imgs/icons/red.svg>";
          cell.classList.add("taken");
          break;
        case "BLACK":
          cell.innerHTML = "<img src=assets/imgs/icons/black.svg>";
          cell.classList.add("taken");
          break;
        case "NONE":
          const number = (parseInt(game.state.board[i].plant) << 2) + (parseInt(game.state.board[i].poem) & 3);
          cell.innerHTML = '<img src=assets/imgs/'+number+'.svg>';
          break;
        default:
          break; 
      }
    }
    // Draw hints
    const additionalClass = game.current().color.toLowerCase() + "-hint";
    for (let i = 0; i < game.state.validMoves.length; i++) {
      document.getElementById("" + game.state.validMoves[i][0] + "" + game.state.validMoves[i][1]).classList.add(additionalClass);
    }
  },
  drawDone: function() {
    this.drawGame();
    const messageContainer = document.querySelector(".game-message");
    messageContainer.classList.add("done");
    const msg = messageContainer.getElementsByTagName("p")[0];
    if (game.state.winner === "NONE") {
      msg.textContent = "A Tie!"
    } else {
      msg.textContent = (game.state.winner + " Wins!");
    }
  }
};

// MAIN BEGINS HERE
var game;
const PLANT_HACK = Object.freeze({MAPLE: 0, CHERRY: 1, PINE: 2, IRIS: 3});
const POEM_HACK = Object.freeze({SUN: 0, BIRD: 1, RAIN: 2, PAPER: 3});
function generateBoard(board) {
  var res = [];
  for (let idx in board) {
    res.push(PLANT_HACK[board[idx][0]] * 4 + POEM_HACK[board[idx][1]]);
  }
  return res;
}
// Buttons
function bindButton(selector, fn) {
  const button = document.querySelector(selector);
  button.addEventListener("click", fn);
}
bindButton(".restart-button", () => {
  const initial = generateBoard([
    ["MAPLE", "SUN"], ["CHERRY", "SUN"], ["PINE", "SUN"], ["IRIS", "SUN"],
    ["MAPLE", "BIRD"], ["CHERRY", "BIRD"], ["PINE", "BIRD"], ["IRIS", "BIRD"],
    ["MAPLE", "RAIN"], ["CHERRY", "RAIN"], ["PINE", "RAIN"], ["IRIS", "RAIN"],
    ["MAPLE", "PAPER"], ["CHERRY", "PAPER"], ["PINE", "PAPER"], ["IRIS", "PAPER"]
  ]);
  game = new Game("SMART", "CLIENT", initial);
  game.start();
});
bindButton(".retry-button", () => {
  const messageContainer = document.querySelector(".game-message");
  messageContainer.classList.remove("done");
  messageContainer.getElementsByTagName("p")[0].textContent = "";
});
