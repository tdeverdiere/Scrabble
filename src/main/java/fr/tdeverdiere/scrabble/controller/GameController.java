package fr.tdeverdiere.scrabble.controller;

import fr.tdeverdiere.scrabble.domain.Game;
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
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Optional;

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

    @RequestMapping(value = "/games", method = {RequestMethod.GET, RequestMethod.POST})
    public String games(Model model) {
        List<Game> games = gameRepository.findAll();
        model.addAttribute("games", games);

        return "games";
    }

    @GetMapping("/games/{id}")
    @ResponseBody
    public Game getGame(@PathVariable("id") String id) {
        Optional<Game> game = gameRepository.findById(Integer.parseInt(id));

        return game.get();
    }

    @PostMapping("/gamesnew")
    public String newGame() {
        Game game = gameService.createNewGame();
        return "redirect:/index.html?game=" + game.getId();
    }

}
