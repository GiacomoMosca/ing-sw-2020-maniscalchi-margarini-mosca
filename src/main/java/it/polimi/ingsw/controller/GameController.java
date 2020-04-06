package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.turn_controllers.*;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.cards.Deck;
import it.polimi.ingsw.model.players.Player;

import java.util.ArrayList;

public class GameController {

    private Game game;
    GodController apolloController,artemisController,athenaController,atlasController,demeterController,hephaestusController,limusController,minotaurController,panController,prometheusController,zeusController;

    public GameController(Game game) {
        this.game = game;
    }

    public Game getGame() {
        return game;
    }

    public void gameSetUp(){
        ArrayList<GodController> controllers;
        Deck deck = game.getDeck();
        String result;
        int i;

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
        controllers = new ArrayList<GodController>();

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

        for(Player player : game.getPlayers())
        {
            player.setGodCard(deck.getPickedCards().get(i));
            i++;
        }

        // Gestisco i turni
        while(!game.hasWinner()){
            result = game.getActivePlayer().getGodCard().getController().playTurn();
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
  
}
