package fr.tdeverdiere.scrabble.exception;

public class GamePasswordInvalidException extends Exception {

    private String name;

    public GamePasswordInvalidException(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
