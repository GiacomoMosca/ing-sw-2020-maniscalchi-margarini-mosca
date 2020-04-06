package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.turn_controllers.*;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.cards.Deck;
import it.polimi.ingsw.model.players.Player;
import it.polimi.ingsw.model.players.Worker;

import java.util.ArrayList;

public class GameController {

    private Game game;
    private ArrayList<Player> players;

    public GameController(Game game) {
        this.game = game;
    }

    public Game getGame() {
        return game;
    }

    public void gameSetUp() {
        Deck deck = game.getDeck();
        String result;
        int i;

        GodController
                apolloController,
                artemisController,
                athenaController,
                atlasController,
                demeterController,
                hephaestusController,
                limusController,
                minotaurController,
                panController,
                prometheusController,
                zeusController;

        apolloController = new ApolloController(this);
        artemisController = new ArtemisController(this);
        athenaController = new AthenaController(this);
        atlasController = new AtlasController(this);
        demeterController = new DemeterController(this);
        hephaestusController = new HephaestusController(this);
        limusController = new LimusController(this);
        minotaurController = new MinotaurController(this);
        panController = new PanController(this);
        prometheusController = new PrometheusController(this);
        zeusController = new ZeusController(this);
        ArrayList<GodController> controllers = new ArrayList<GodController>();

        controllers.add(apolloController);
        controllers.add(artemisController);
        controllers.add(athenaController);
        controllers.add(atlasController);
        controllers.add(demeterController);
        controllers.add(hephaestusController);
        controllers.add(limusController);
        controllers.add(minotaurController);
        controllers.add(panController);
        controllers.add(prometheusController);
        controllers.add(zeusController);

        for (GodController godController : controllers){
            deck.addCard(godController.generateCard());
        }

        deck.pickRandom(game.getPlayerNum());
        i=0;

        players = game.getPlayers();
        for(Player player : players)
        {
            player.setGodCard(deck.getPickedCards().get(i));
            player.getController().setGodController(deck.getPickedCards().get(i).getController());
            player.getController().getClient().displayMessage("You are " + deck.getPickedCards().get(i).getGod() + "\n");
            i++;
        }

        placeWorkers();

        displayBoard();

        // Gestisco i turni
        while(!game.hasWinner()){
            result = game.getActivePlayer().getController().playTurn();
            if(result.equals("NEXT"))
                game.getNextPlayer(game.getActivePlayer());
            else
                if (result.equals("LOST"))
                    game.setWinner(game.getNextPlayer(game.getActivePlayer()));
                else
                    if(result.equals("WON"))
                        game.setWinner(game.getActivePlayer());
        }

    }

    private void placeWorkers() {
        Worker wA1 = new Worker(players.get(0));
        wA1.setPosition(game.getBoard().getCell(0,0));
        game.getBoard().getCell(0,0).setWorker(wA1);
        players.get(0).addWorker(wA1);
        Worker wA2 = new Worker(players.get(0));
        wA2.setPosition(game.getBoard().getCell(0,4));
        game.getBoard().getCell(0,4).setWorker(wA2);
        players.get(0).addWorker(wA2);
        Worker wB1 = new Worker(players.get(1));
        wB1.setPosition(game.getBoard().getCell(4,0));
        game.getBoard().getCell(4,0).setWorker(wB1);
        players.get(1).addWorker(wB1);
        Worker wB2 = new Worker(players.get(1));
        wB2.setPosition(game.getBoard().getCell(4,4));
        game.getBoard().getCell(4,4).setWorker(wB2);
        players.get(1).addWorker(wB2);
    }

    public void displayBoard() {
        for (Player p : players)
            p.getController().getClient().displayBoard(game.getBoard());
    }
  
}
