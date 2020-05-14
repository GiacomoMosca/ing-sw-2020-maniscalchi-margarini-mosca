package it.polimi.ingsw.controller;

import it.polimi.ingsw.exceptions.IOExceptionFromController;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.players.Player;
import it.polimi.ingsw.view.VirtualView;

public class FakeGameController extends GameController {

    public FakeGameController(VirtualView client, int num, String gameName) {
        super(client, num, gameName);
    }

    @Override
    public void addPlayer(VirtualView client) {
        Player player = new Player(client.getId(), colors.get(playerControllers.size()));
        PlayerController playerController = new PlayerController(player, client, this);
        game.addPlayer(player);
        playerControllers.add(playerController);
        gameSetUp();
    }

    public void playGame() throws IOExceptionFromController {
        String result = playerControllers.get(game.getActivePlayer()).playTurn();
        if (result.equals("winConditionAchieved") || result.equals("godConditionAchieved"))
            game.setWinner(players.get(game.getActivePlayer()));
    }

    @Override
    public void broadcastBoard(String desc, Card card) {
        //
    }
}
