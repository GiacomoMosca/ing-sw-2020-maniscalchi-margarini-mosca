package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.turn_controllers.*;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.Deck;

import java.util.ArrayList;

public class GameController {

    private Game game;
    GodController apolloController,artemisController,athenaController,atlasController,demeterController,hephaestusController,hermesController,limusController,minotaurController,panController,prometheusController,zeusController;

    public GameController(Game game) {
        this.game = game;
    }

    public Game getGame() {
        return game;
    }

    public void gameSetUp(){
        ArrayList<GodController> controllers;
        Deck deck;

        apolloController = new ApolloController(this);
        artemisController = new ArtemisController(this);
        athenaController = new AthenaController(this);
        atlasController = new AtlasController(this);
        demeterController = new DemeterController(this);
        hephaestusController = new HephaestusController(this);
        hermesController = new HermesController(this);
        limusController = new LimusController(this);
        minotaurController = new MinotaurController(this);
        panController = new PanController(this);
        prometheusController = new PrometheusController(this);
        zeusController = new ZeusController(this);
        controllers = new ArrayList<GodController>();
        deck = new Deck();

        controllers.add(apolloController);
        controllers.add(artemisController);
        controllers.add(athenaController);
        controllers.add(atlasController);
        controllers.add(demeterController);
        controllers.add(hephaestusController);
        controllers.add(hermesController);
        controllers.add(limusController);
        controllers.add(minotaurController);
        controllers.add(panController);
        controllers.add(prometheusController);
        controllers.add(zeusController);

        for (GodController godController : controllers){
            deck.addCard(godController.generateCard());
        }

    }
}
