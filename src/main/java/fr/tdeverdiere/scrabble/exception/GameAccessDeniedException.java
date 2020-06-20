package fr.tdeverdiere.scrabble.exception;

public class GameAccessDeniedException extends Exception {

    private String name;
    public GameAccessDeniedException(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
