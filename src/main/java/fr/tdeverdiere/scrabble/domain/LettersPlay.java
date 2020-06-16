package fr.tdeverdiere.scrabble.domain;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "LETTERSPLAY")
public class LettersPlay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private int firstPosition;
    private int lastPosition;
    private String direction;

    @ManyToOne
    private History history;

    @OneToMany(cascade = CascadeType.ALL)
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
