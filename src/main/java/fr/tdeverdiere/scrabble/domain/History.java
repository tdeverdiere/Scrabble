package fr.tdeverdiere.scrabble.domain;

import java.util.List;

public class History {

    private List<Letter> squares;
    private LettersPlay lettersPlay;
    private int index;
    private int score;
    private String playerId;
    private List<Letter> deskLetters;

    public List<Letter> getSquares() {
        return squares;
    }

    public void setSquares(List<Letter> squares) {
        this.squares = squares;
    }

    public LettersPlay getLettersPlay() {
        return lettersPlay;
    }

    public void setLettersPlay(LettersPlay lettersPlay) {
        this.lettersPlay = lettersPlay;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public List<Letter> getDeskLetters() {
        return deskLetters;
    }

    public void setDeskLetters(List<Letter> deskLetters) {
        this.deskLetters = deskLetters;
    }
}
