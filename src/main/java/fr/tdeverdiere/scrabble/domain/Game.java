package fr.tdeverdiere.scrabble.domain;

import java.util.List;

public class Game {

    private List<History> history;

    private List<String> types;

    private int stepNumber;

    private List<User> players;

    private String currentPlayerId;

    private List<Letter> deskLetters;
}
