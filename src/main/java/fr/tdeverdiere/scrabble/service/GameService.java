package fr.tdeverdiere.scrabble.service;

import fr.tdeverdiere.scrabble.domain.Game;
import fr.tdeverdiere.scrabble.domain.History;
import fr.tdeverdiere.scrabble.domain.Type;
import fr.tdeverdiere.scrabble.domain.User;
import fr.tdeverdiere.scrabble.exception.GameAccessDeniedException;
import fr.tdeverdiere.scrabble.exception.GameNotExistsException;
import fr.tdeverdiere.scrabble.exception.GamePasswordInvalidException;
import fr.tdeverdiere.scrabble.repository.GameRepository;
import fr.tdeverdiere.scrabble.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;


@Service
public class GameService {

    private PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;


    public static int getDefaultBoardSize() {
        return 255;
    }
    public static SortedSet<Type> createDefaultTypes() {
        int boardSize = getDefaultBoardSize();

        SortedSet<Type> types = new TreeSet<>();
        addTypes(types, Type.TypeName.MCT, 0, 7, 14, 105, 119, 210, 217, 224);
        addTypes(types, Type.TypeName.MCD, 16, 28, 32, 42, 48, 56, 64, 70, 154, 160, 168, 176, 182, 192, 196, 208);
        addTypes(types, Type.TypeName.LCT, 20, 24, 76, 80, 84, 88, 136, 140, 144, 148, 200, 204);
        addTypes(types, Type.TypeName.LCD, 3, 11, 36, 38, 45, 52, 59, 92, 96, 98, 102, 108, 116, 122, 126, 128, 132, 165, 172, 179, 186, 188, 213, 221);
        addTypes(types, Type.TypeName.START, 112);

        // fill the others to standard
        int[] squaresPositions = new int[boardSize];
        for (int i = 0; i < boardSize; i++) {
            squaresPositions[i] = i;
        }

        addTypes(types, Type.TypeName.STANDARD, squaresPositions);

        return types;
    }

    public Game createNewGame(String name, String password, Integer numberofplayers) {
        Game game = new Game();
        game.setStepNumber(0);
        game.setName(name);
        if (numberofplayers == null) {
            numberofplayers = 2;
        }
        game.setNumberOfPlayers(numberofplayers);
        LocalDateTime now = LocalDateTime.now();
        game.setCreationDate(now);
        game.setModificationDate(now);
        game.setPassword(passwordEncoder.encode(password));
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
        game.setPlayers(Arrays.asList(internalUser.get()));

        History history = new History();
        history.setIndex(0);
        history.setPlayerId(user.getUsername());

        history.setScore(0);
        game.setHistory(Arrays.asList(history));
        game.setBoardSize(getDefaultBoardSize());
        game.setTypes(createDefaultTypes());

        return gameRepository.save(game);
    }

    public Game getGame(String id) throws GameAccessDeniedException, GameNotExistsException {
        Optional<Game> optionalGame = gameRepository.findById(Integer.parseInt(id));

        if (!optionalGame.isPresent()) {
            throw new GameNotExistsException();
        }

        String name = optionalGame.get().getName();
        User user = userService.getCurrentUser();
        return optionalGame.filter(game -> game.getPlayers().contains(user))
                .orElseThrow(() -> new GameAccessDeniedException(name));
    }

    public void validatePassword(String id, String password) throws GameNotExistsException, GamePasswordInvalidException {
        Optional<Game> optionalGame = gameRepository.findById(Integer.parseInt(id));
        if (!optionalGame.isPresent()) {
            throw new GameNotExistsException();
        }

        Game game = optionalGame.get();
        if (passwordEncoder.matches(password, game.getPassword())) {
            User user = userService.getCurrentUser();
            addUser(game, user);
            return;
        }
        throw new GamePasswordInvalidException(game.getName());
    }

    public void addUser(Game game, User user) {
        if (!game.getPlayers().contains(user)) {
            game.getPlayers().add(user);
            gameRepository.save(game);
        }
    }

    private static void addTypes(Set<Type> types, Type.TypeName typeName, int... positions) {
        for (int position : positions) {
            types.add(Type.create(position, typeName));
        }
    }

}
