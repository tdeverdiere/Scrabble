package fr.tdeverdiere.scrabble.controller;

import fr.tdeverdiere.scrabble.domain.Game;
import fr.tdeverdiere.scrabble.exception.GameAccessDeniedException;
import fr.tdeverdiere.scrabble.exception.GameNotExistsException;
import fr.tdeverdiere.scrabble.exception.GamePasswordInvalidException;
import fr.tdeverdiere.scrabble.repository.GameRepository;
import fr.tdeverdiere.scrabble.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class GameController {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GameService gameService;

    @MessageMapping("/turn")
    @SendTo("/topic/turn")
    public Game turn(Game game) throws Exception {
        return game;
    }

    @GetMapping("/games/{id}")
    @ResponseBody
    public Game getGame(@PathVariable("id") String id) throws GameAccessDeniedException, GameNotExistsException {
        return gameService.getGame(id);
    }

    @RequestMapping(value = "/p/games", method = {RequestMethod.GET, RequestMethod.POST})
    public String games(Model model) {
        List<Game> games = gameRepository.findAll();
        model.addAttribute("games", games);

        return "games";
    }

    @PostMapping("/p/gamepassword")
    public String validateGamePassword(@RequestParam("gameId") String gameId, @RequestParam("password") String password, Model model) {
        try {
            gameService.validatePassword(gameId, password);
            return "redirect:/index.html?game=" + gameId;
        } catch (GamePasswordInvalidException e) {
            model.addAttribute("passwordinvalid", "Password is invalid");
            model.addAttribute("gameId", gameId);
            model.addAttribute("gameName", e.getName());
            return "gamepassword";
        } catch (GameNotExistsException e) {
            return "redirect:/p/games";
        }
    }

    @GetMapping("/p/game")
    public String chooseGame(@RequestParam(value = "gameId", required = false) String gameId, Model model) {
        if (gameId == null) {
            return "newgame";
        }

        try {
            Game game = gameService.getGame(gameId);
            return "redirect:/index.html?game=" + game.getId();
        } catch (GameAccessDeniedException e) {
            model.addAttribute("gameId", gameId);
            model.addAttribute("gameName", e.getName());
            return "gamepassword";
        } catch (GameNotExistsException e) {
            return "redirect:/p/games";
        }
    }

    @PostMapping("/p/game")
    public String createGame(@RequestParam String name, @RequestParam String password) {
        Game game = gameService.createNewGame(name, password);
        return "redirect:/index.html?game=" + game.getId();
    }

}
