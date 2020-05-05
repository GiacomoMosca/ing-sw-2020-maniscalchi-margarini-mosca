package it.polimi.ingsw.network.server;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.view.VirtualView;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {

    private int port;
    private ServerSocket socket;
    private static ArrayList<GameController> gameControllers;
    private static ArrayList<String> gameRooms;
    private static ArrayList<String> playersId;


    public Server(int port) {
        this.port = port;
        this.gameControllers = new ArrayList<GameController>();
        this.gameRooms = new ArrayList<String>();
        this.playersId = new ArrayList<String>();
    }

    public void start() {
        try {
            socket = new ServerSocket(port);
        } catch (IOException e) {
            System.out.println("cannot open server socket");
            System.exit(1);
            return;
        }

        while (true) {
            try {
                Socket client = socket.accept();
                new Thread(() -> {
                    String gameName=null, nickname=null, roomsList=null;
                    int playerNum=-1, gameRoom=-1, choice=-1, i=1;
                    boolean test=false;
                    ObjectOutputStream outputStream = null;
                    ObjectInputStream inputStream = null;

                    try {
                        outputStream = new ObjectOutputStream(client.getOutputStream());
                        inputStream = new ObjectInputStream(client.getInputStream());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    VirtualView player = new VirtualView(client, inputStream, outputStream);

                    try {
                        while (!test) {
                            nickname = player.chooseNickname();
                            synchronized (playersId) {
                                if (!playersId.contains(nickname)) {
                                    playersId.add(nickname);
                                    test = true;
                                } else
                                    player.displayMessage("Nickname already taken");
                            }
                        }

                        choice = player.chooseInt("\n1. Start a new game, 2. Join a game",2);

                        test = false;
                        if (choice == 1) {
                            while (!test) {
                                gameName = player.chooseGameName();
                                playerNum = player.choosePlayersNumber();
                                synchronized (gameRooms) {
                                    if (!gameRooms.contains(gameName)) {
                                        gameRooms.add(gameName);
                                        GameController gameController = new GameController(player,playerNum,gameName);
                                        player.displayMessage("Waiting for players...");
                                        gameControllers.add(gameController);
                                        test = true;
                                    } else
                                        player.displayMessage("This room already exists");
                                }
                            }
                        }
                        else {
                            while (!test) {
                                if(gameRooms.size()!=0) {
                                    gameRoom = player.chooseGameRoom(new ArrayList<String>(gameRooms));
                                    if (gameRoom != 0)
                                        test=true;
                                }
                                else
                                    player.chooseInt("\nNo room available\n1. Refresh list",1);
                            }
                            addPlayer(player, gameRooms.get(gameRoom-1));
                        }
                } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }).start();
            } catch (IOException e) {
                System.out.println("connection dropped");
            }
        }
    }

    public void addPlayer(VirtualView player, String gameName) {
        new Thread(() -> {
            int i = 0;
            while (!gameControllers.get(i).getGameName().equals(gameName))
                i++;
            try {
                player.displayMessage("Joining " + gameName + " room...");
                gameControllers.get(i).addPlayer(player);
                if (gameControllers.get(i).checkPlayersNumber()) {
                    gameRooms.remove(gameName);
                    gameControllers.get(i).gameSetUp();
                }
                else {
                    player.displayMessage("Waiting for players...");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

}
