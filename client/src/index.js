import React from 'react';
import ReactDOM from 'react-dom';
import { DndProvider } from 'react-dnd'
import Backend from 'react-dnd-html5-backend'

import { Board } from './board.js';
import { BoardPlay } from './boardplay.js';
import { GameRepository } from './gamerepository.js';
import { Desk } from './desk.js';
import './index.css';

let boardSize = 255;

var urlParams;
(window.onpopstate = function () {
    var match,
        pl     = /\+/g,  // Regex for replacing addition symbol with a space
        search = /([^&=]+)=?([^&]*)/g,
        decode = function (s) { return decodeURIComponent(s.replace(pl, " ")); },
        query  = window.location.search.substring(1);

    urlParams = {};
    while (match = search.exec(query))
        urlParams[decode(match[1])] = decode(match[2]);
})();

let POSITION_START = 112;
let INITIAL_DESK_LETTERS =  [
    {id: 1, letter: {content: 'W', point: 10}},
    {id: 2, letter: {content: 'A', point: 1}},
    {id: 3, letter: {content: 'G', point: 3}},
    {id: 4, letter: {content: 'O', point: 1}},
    {id: 5, letter: {content: 'N', point: 1}},
    {id: 6, letter: {content: 'A', point: 1}},
    {id: 7, letter: {content: 'U', point: 1}}
];

const CURRENT_LETTERS_PLAY_INIT = { firstPosition: null, lastPosition: null, direction: null, letters: Array(0)};

class Game extends React.Component {
    constructor(props) {
        super(props);
        let types =  Array(boardSize).fill('square-standard');

        this.gameRepository = new GameRepository((game) => this.initGame(game));
        this.gameId = urlParams["game"];

        // Have a game id and date and players.
        // TODO get other players' name and id.

        let players = Array(4);

        let nextPossiblePositions = new Set();
        nextPossiblePositions.add(POSITION_START);

        // TODO get letters

        this.state = {
            history: [{
                squares: Array(boardSize).fill(null),
                lettersPlay: null,
                index: 0,
                score: 0,
                deskLetters: INITIAL_DESK_LETTERS
            }],
            gameId: this.gameId,
            types: types,
            currentLettersPlay: CURRENT_LETTERS_PLAY_INIT,
            nextPossiblePositions: nextPossiblePositions,
            xIsNext: true,
            stepNumber: 0,
            movesInChronoOrder: true,
            players: players,
            currentPlayerId: '0',
            deskLetters: INITIAL_DESK_LETTERS
        };

        this.boardPlay = new BoardPlay(POSITION_START, types, this.state.currentLettersPlay, this.state.history[0].squares);
    }

    componentDidMount() {
        this.gameRepository.setupWebSocket();
        this.gameRepository.getGame(this.state.gameId, (game) => { this.initGame(game) });
    }

    initGame(game) {
        let boardSize = game.boardSize;
        if (game.stepNumber === 0) {
            game.history[0].squares = Array(boardSize).fill(null);
        }
        this.setState(game);

        this.setState({
            deskLetters: INITIAL_DESK_LETTERS
        });
        this.gameRepository.sendGameState(game);
    }

    validate() {
        const history = this.state.history.slice(0, this.state.stepNumber + 1);
        const current = history[history.length - 1];
        const squares = current.squares.slice();

        this.boardPlay.currentLettersPlay = this.state.currentLettersPlay;
        this.boardPlay.squares = squares;

        let score = this.boardPlay.calculateScore();
        this.boardPlay.updateSquares();

        // TODO get more letters
        // IF no more letters the game ends here (have to see the rules)
        // Count others letters ?

        this.setState({
            history: history.concat([{
                squares: this.boardPlay.squares,
                lettersPlay: this.boardPlay.currentLettersPlay,
                index: history.length,
                score: score,
                playerId: this.state.currentPlayerId,
                deskLetters: this.state.deskLetters.slice()
            }]),
            currentLettersPlay: CURRENT_LETTERS_PLAY_INIT,
            stepNumber: history.length,
            xIsNext: !this.state.xIsNext,
        });

        this.boardPlay.currentLettersPlay = CURRENT_LETTERS_PLAY_INIT;

        this.updateNextPossiblePositions(history.length);

        // TODO send result to server.
        this.gameRepository.sendGameState(this.state);
    }

    cancel() {
        const current = this.state.history[this.state.stepNumber];
        const squares = current.squares;

        let deskLetters = this.state.deskLetters.concat(this.state.currentLettersPlay.letters);
        this.setState({
            currentLettersPlay: CURRENT_LETTERS_PLAY_INIT,
            deskLetters: deskLetters
        });

        this.boardPlay.currentLettersPlay = CURRENT_LETTERS_PLAY_INIT;
        this.boardPlay.squares = squares;

        this.updateNextPossiblePositions(this.state.stepNumber);
    }

    dropLetter(i, item) {
        if (!this.state.nextPossiblePositions.has(i)) {
            return;
        }
        const current = this.state.history[this.state.stepNumber];
        const squares = current.squares;

        this.boardPlay.currentLettersPlay = this.state.currentLettersPlay;
        this.boardPlay.squares = squares;
        this.boardPlay.updateCurrentLettersPlay(i, item);

        this.setState({
            currentLettersPlay: this.boardPlay.currentLettersPlay
        });

        this.updateNextPossiblePositions(this.state.stepNumber);

        const deskLetters = this.state.deskLetters.slice();
        const { letter, index } = this.findDeskLetter(item.id, deskLetters);
        deskLetters.splice(index, 1);
        this.setState({
            deskLetters: deskLetters
        });

    }

    updateNextPossiblePositions(turnIndex) {
        let nextPossiblePositions = this.boardPlay.calculateNextPossiblePositions(turnIndex);
        this.setState({
            nextPossiblePositions: nextPossiblePositions
        });
    }
    canDropLetter(i, item) {
        const nextPossiblePositions = this.state.nextPossiblePositions;

        return nextPossiblePositions.has(i);
    }

    jumpTo(step) {
        const current = this.state.history[step];
        this.setState({
            stepNumber: step,
            xIsNext: (step % 2) === 0,
            deskLetters: current.deskLetters
        });
        this.boardPlay.currentLettersPlay = CURRENT_LETTERS_PLAY_INIT;
        this.boardPlay.squares = this.state.history[step].squares;

        this.updateNextPossiblePositions(step);
    }

    moveDeskLetter(id, atIndex, deskLetters) {
        const { letter, index } = this.findDeskLetter(id, deskLetters);
        let replacedLetter = deskLetters.splice(atIndex, 1, letter);
        deskLetters.splice(index, 1, replacedLetter[0]);

        this.setState({
            deskLetters: deskLetters
        });
    }

    findDeskLetter(id, deskLetters) {
        const letter = deskLetters.filter((l) => l.id === id)[0];

        return {
            letter,
            index: deskLetters.indexOf(letter),
        }
    }

    render() {
        const allhistory = this.state.history.slice(0, this.state.history.length);
        const history = this.state.history.slice(0, this.state.stepNumber + 1);
        const current = history[this.state.stepNumber];
        const types = this.state.types;
        const currentLettersPlay = this.state.currentLettersPlay;
        const deskLetters = this.state.deskLetters.slice();

        if (!this.state.movesInChronoOrder) {
            history.reverse();
        }

        const moves = allhistory.map((step, move) => {
            let button;
            let word;

            if (step.index && step.lettersPlay) {
                word = step.lettersPlay.letters.map((value) => {
                    return value.letter.content;
                });

                button = <button onClick={() => this.jumpTo(step.index)}>Revenir au tour n° <b>{step.index}</b> - ( {word} )</button>
            } else {
                button = <button onClick={() => this.jumpTo(step.index)}>Revenir au début</button>
            }
            return (
                <li key={step.index}>
                    {button}
                </li>
            )
        });

        const players = this.state.players.map((player) => {
            let total = 0;
            let scores = [];
            history.forEach((turn) => { if (turn.playerId === player.username) {
                total = total + turn.score;
                scores = scores.concat(turn.score);
            }});
            return (
                <div className="player-score" key={player.id}>
                    <div>{player.firstName} : {total}</div>
                    <div className="score">
                    {
                        scores.map((score) => {
                            return (
                                <div>{score}</div>
                            )
                        })
                    }
                    </div>
                </div>
            )
        });
        return (
            <DndProvider backend={Backend}>
                <div className="game">
                    <div className="game-board">
                        <Board
                            currentLettersPlay = {currentLettersPlay}
                            squares = {current.squares}
                            types = {types}
                            dropLetter={(i, item) => this.dropLetter(i, item)}
                            canDropLetter={(i, item) => this.canDropLetter(i, item)}
                        />
                    </div>
                    <div className="game-side">
                        <div className="game-info">
                            <div className="plays">
                                <ol>{moves}</ol>
                            </div>
                            <div className="players-info">
                                {players}
                            </div>
                        </div>
                        <Desk deskLetters={deskLetters} moveDeskLetter={(id, atIndex, deskLetters) => this.moveDeskLetter(id, atIndex, deskLetters)} findDeskLetter={(id, deskLetters) => this.findDeskLetter(id, deskLetters)} lettersPlayed={this.state.currentLettersPlay} validate={() => this.validate()} cancel={() => this.cancel()}/>
                    </div>
                </div>
            </DndProvider>
        );
    }
}

// ========================================

ReactDOM.render(
    <Game />,
    document.getElementById('root')
);
