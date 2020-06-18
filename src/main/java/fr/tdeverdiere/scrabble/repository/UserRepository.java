package fr.tdeverdiere.scrabble.repository;

import fr.tdeverdiere.scrabble.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {

    User getUserByUsername(String username);
}
