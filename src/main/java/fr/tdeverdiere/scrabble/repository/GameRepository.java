package fr.tdeverdiere.scrabble.repository;

import fr.tdeverdiere.scrabble.domain.Game;
import fr.tdeverdiere.scrabble.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<Game, Integer> {

}
