package fr.tdeverdiere.scrabble.domain;

public class LetterPlay {

    private int position;
    private Letter letter;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public Letter getLetter() {
        return letter;
    }

    public void setLetter(Letter letter) {
        this.letter = letter;
    }
}
