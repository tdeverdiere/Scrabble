package fr.tdeverdiere.scrabble.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "TYPES")
public class Type {

    public enum TypeName {
        MCT,
        MCD,
        LCT,
        LCD,
        START
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private int position;

    private String content;

    @ManyToOne
    private Game game;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public static Type create(int position, TypeName typeName) {
        Type type = new Type();
        type.setPosition(position);
        type.setContent(typeName.name().toLowerCase());

        return type;
    }
}
