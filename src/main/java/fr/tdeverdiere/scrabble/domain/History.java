package fr.tdeverdiere.scrabble.domain;

import org.springframework.core.ReactiveAdapterRegistry;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "HISTORIES")
public class History {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    private Game game;

    @OneToMany(cascade = CascadeType.ALL)
    private List<SquareLetter> squares;

    @OneToOne(cascade = CascadeType.ALL)
    private LettersPlay lettersPlay;
    private int index;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Score> scores;

    private String playerId;

    @OneToMany(cascade = CascadeType.ALL)
    private List<HistoryDeskLetter> deskLetters;

    public List<SquareLetter> getSquares() {
        return squares;
    }

    public void setSquares(List<SquareLetter> squares) {
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

    public List<Score> getScores() {
        return scores;
    }

    public void setScores(List<Score> scores) {
        this.scores = scores;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public List<HistoryDeskLetter> getDeskLetters() {
        return deskLetters;
    }

    public void setDeskLetters(List<HistoryDeskLetter> deskLetters) {
        this.deskLetters = deskLetters;
    }
}
