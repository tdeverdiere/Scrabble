package fr.tdeverdiere.scrabble.service;

import fr.tdeverdiere.scrabble.domain.Game;
import fr.tdeverdiere.scrabble.domain.History;
import fr.tdeverdiere.scrabble.domain.Type;
import fr.tdeverdiere.scrabble.domain.User;
import fr.tdeverdiere.scrabble.repository.GameRepository;
import fr.tdeverdiere.scrabble.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


@Service
public class GameService {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private UserRepository userRepository;

    public static int getDefaultBoardSize() {
        return 225;
    }
    public static List<Type> createDefaultTypes() {
        List<Type> types = new ArrayList<>();
        addTypes(types, Type.TypeName.MCT, 0, 7, 14, 105, 119, 210, 217, 224);
        addTypes(types, Type.TypeName.MCD, 16, 28, 32, 42, 48, 56, 64, 70, 154, 160, 168, 176, 182, 192, 196, 208);
        addTypes(types, Type.TypeName.LCT, 20, 24, 76, 80, 84, 88, 136, 140, 144, 148, 200, 204);
        addTypes(types, Type.TypeName.LCD, 3, 11, 36, 38, 45, 52, 59, 92, 96, 98, 102, 108, 116, 122, 126, 128, 132, 165, 172, 179, 186, 188, 213, 221);
        addTypes(types, Type.TypeName.START, 112);

        return types;
    }

    public Game createNewGame() {
        Game game = new Game();
        game.setStepNumber(0);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new AccessDeniedException("Cannot create game.");
        }

        org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
        User userSample = new User();
        userSample.setUsername(user.getUsername());
        userSample.setEnabled(true);

        Example<User> sample = Example.of(userSample);
        Optional<User> internalUser = userRepository.findOne(sample);

        if (!internalUser.isPresent()) {
            throw new AccessDeniedException("Unable to find user " + user.getUsername());
        }
        game.setPlayers(Arrays.asList());

        History history = new History();
        history.setIndex(0);
        history.setPlayerId(user.getUsername());
        game.setHistory(Arrays.asList(history));
        game.setBoardSize(getDefaultBoardSize());
        game.setTypes(createDefaultTypes());

        return gameRepository.save(game);
    }

    private static void addTypes(List<Type> types, Type.TypeName typeName, int... positions) {
        for (int position : positions) {
            types.add(Type.create(position, typeName));
        }
    }

}
