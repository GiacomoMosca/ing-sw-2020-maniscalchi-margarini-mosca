package it.polimi.ingsw.network.server;


import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.network.message.to_client.DisplayMessage;
import it.polimi.ingsw.view.VirtualView;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;


public class Server {
  private int port;
  private ServerSocket socket;
  private GameController gameController;
  int i;

  public Server(int port) {
	this.port = port;
	gameController = null;
  }

  public void start() {

	i = 1;

	try {
	  socket = new ServerSocket(port);
	} catch (IOException e) {
	  System.out.println("cannot open server socket");
	  System.exit(1);
	  return;
	}
	while(gameController == null || !gameController.checkPlayers()) {
	  addPlayer();
	}
	gameController.gameSetUp();
  }

  public synchronized void addPlayer(){
	Socket csocket = null;
	  try {
	  csocket = socket.accept();
	} catch (IOException e) {
	  e.printStackTrace();
	}
	ObjectOutputStream output = null;
	try {
	  output = new ObjectOutputStream(csocket.getOutputStream());
	} catch (IOException e) {
	  e.printStackTrace();
	}
	ObjectInputStream input = null;
	try {
	  input = new ObjectInputStream(csocket.getInputStream());
	} catch (IOException e) {
	  e.printStackTrace();
	}
	VirtualView player = new VirtualView(csocket, input, output);
	player.setId("Player " + i);
	System.out.println("Player " + i + " connected\n\n");
	i++;
	if (gameController == null) {
	  gameController = new GameController(player, /*p1.getNum()*/ 2);
	}
	else {
	  gameController.addPlayer(player);
	}

  }
}
