package it.polimi.ingsw.network.server;


import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.view.PlayerInterface;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;


public class Server {
  private int port;
  private Map<String, ClientHandler> clients;
  private ServerSocket socket;
  private GameController gameController;
  private Boolean gameStarted;

  public Server(int port) {
    this.port = port;
    clients = new HashMap();
  }

  public void start() {

    try {
      socket = new ServerSocket(port);
    } catch (IOException e) {
      System.out.println("cannot open server socket");
      System.exit(1);
      return;
    }
    gameStarted=false;
    while(true) {
      if((gameController!=null)&&(gameController.checkPlayers))
       break;
      new Thread(() -> {addPlayer();}).start();
    }
    gameController.gameSetUp();
  }

  public synchronized void addPlayer(){
    Socket csocket = socket.accept();
    ObjectOutputStream output = new ObjectOutputStream(csocket.getOutputStream());
    ObjectInputStream input = new ObjectInputStream(csocket.getInputStream());
    PlayerInterface p1 = new PlayerInterface(csocket, input, output);
    gameController = new GameController(p1, p1.getNum());
  }
}
