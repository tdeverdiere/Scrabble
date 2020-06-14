package fr.tdeverdiere.scrabble.domain;

import java.util.List;

public class LettersPlay {

    private int firstPosition;
    private int lastPosition;
    private String direction;

    private List<LetterPlay> letters;

    public int getFirstPosition() {
        return firstPosition;
    }

    public void setFirstPosition(int firstPosition) {
        this.firstPosition = firstPosition;
    }

    public int getLastPosition() {
        return lastPosition;
    }

    public void setLastPosition(int lastPosition) {
        this.lastPosition = lastPosition;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public List<LetterPlay> getLetters() {
        return letters;
    }

    public void setLetters(List<LetterPlay> letters) {
        this.letters = letters;
    }
}
