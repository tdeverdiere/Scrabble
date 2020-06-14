package fr.tdeverdiere.scrabble.controller;

import fr.tdeverdiere.scrabble.domain.Game;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class GameController {

    @MessageMapping("/turn")
    @SendTo("/topic/turn")
    public Game turn(Game game) throws Exception {
        return game;
    }

}
