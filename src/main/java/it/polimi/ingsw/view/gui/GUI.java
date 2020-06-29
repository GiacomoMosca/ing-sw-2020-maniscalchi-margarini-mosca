package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.network.message.to_client.Ping;
import it.polimi.ingsw.network.message.to_client.ServerClosed;
import it.polimi.ingsw.network.message.to_client.ToClientMessage;
import it.polimi.ingsw.network.message.to_server.*;
import it.polimi.ingsw.view.*;
import javafx.application.Platform;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * GUI class allows the client to interact with the server.
 */
public class GUI implements UI {

    private final AtomicBoolean running;
    private final GUIManager manager;
    private final Object busyLock;
    private Socket server;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private LinkedBlockingQueue<ToClientMessage> serverQueue;
    private SynchronousQueue<Object> messageQueue;
    private String id;
    private GameView currentGame;

    /**
     * GUI constructor.
     * Creates a new GUIManager Object.
     */
    public GUI() {
        this.running = new AtomicBoolean();
        this.manager = new GUIManager();
        this.busyLock = manager.getLock();
        this.id = null;
    }

    /**
     * Allows GUI to run until the Game is over.
     * Handles the connection of the client to the server and opens a communication channel between them.
     * If an IOException or a ClassCastException occur while opening that channel, CLI stops.
     * Creates a messageQueue where, thanks to the GUIManager, the client's input will be offered.
     * Creates a serverQueue where the messages from the server will be put after deserialization.
     * Starts two threads which respectively allows the GUIManager to listen to the client input, and the GUI to listen to the server messages.
     * Every time a message from the server is put on the serverQueue, it is parsed to the corresponding action.
     */
    @Override
    public void run() {
        boolean connected;
        running.set(true);
        messageQueue = new SynchronousQueue<Object>();
        currentGame = null;

        manager.setGUI(this);
        manager.setQueue(messageQueue);
        new Thread(manager::run).start();

        String ip;
        do {
            server = new Socket();
            ip = getServerIp();
            try {
                server.connect(new InetSocketAddress(ip, 8000), 5 * 1000);
                connected = true;
            } catch (IOException e) {
                manager.initLogin();
                manager.serverErrorMessage("Server unreachable");
                connected = false;
            }
            if (connected) {
                try {
                    output = new ObjectOutputStream(server.getOutputStream());
                    input = new ObjectInputStream(server.getInputStream());
                    connected = true;
                } catch (IOException e) {
                    manager.initLogin();
                    manager.serverErrorMessage("Server is down");
                    connected = false;
                } catch (ClassCastException e) {
                    manager.initLogin();
                    manager.serverErrorMessage("Protocol violation");
                    connected = false;
                }
            }
        } while (!connected);

        serverQueue = new LinkedBlockingQueue<ToClientMessage>();
        new Thread(this::serverListener).start();
        ToClientMessage message;
        while (running.get()) {
            try {
                message = serverQueue.take();
            } catch (InterruptedException e) {
                serverClosed();
                break;
            }
            synchronized (busyLock) {
                while (!manager.setBusy(true)) {
                    try {
                        busyLock.wait();
                    } catch (InterruptedException e) {
                        //
                    }
                }
            }
            parseMessage(message);
        }
        stop();
    }

    /**
     * Continuously listens to the server.
     * When deserializing a message, puts it on the serverQueue so that it can be processed.
     * If the deserialized message is a Ping from the server, replies with a Pong.
     */
    public void serverListener() {
        ToClientMessage serverMessage;
        while (running.get()) {
            try {
                serverMessage = (ToClientMessage) input.readObject();
            } catch (IOException | ClassNotFoundException e) {
                serverMessage = new ServerClosed();
            }
            if (serverMessage instanceof Ping) pong();
            else {
                try {
                    serverQueue.put(serverMessage);
                } catch (InterruptedException e) {
                    //
                }
            }
            if (serverMessage instanceof ServerClosed) return;
        }
    }

    /**
     * Closes the communication channel with the server and closes the client.
     */
    public synchronized void stop() {
        try {
            if (server != null) server.close();
            if (input != null) input.close();
            if (output != null) output.close();
        } catch (IOException e) {
            //
        }
        Platform.exit();
    }

    /**
     * Allows receiving from the client the IP of the server he wants to connect to.
     * Calls the getServerIp method on the GUIManager so that it will ask the player to insert the Server IP Address.
     * The GUIManager will put the client's input on the messageQueue and
     *
     * @return the String written by the client
     */
    public String getServerIp() {
        while (!manager.setBusy(true)) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                //
            }
        }
        manager.getServerIp();
        return getString();
    }

    /**
     * Allows parsing a message received from the server to the corresponding action to do on the client.
     *
     * @param message the ToClientMessage to parse
     */
    public void parseMessage(ToClientMessage message) {
        message.performAction(this);
    }

    // get from queue

    /**
     * Takes a message previously put on the messageQueue by the GUIManager.
     * If no exception occurs in taking the message and casting it to a Boolean, that Boolean is returned.
     * If an InterruptedException occurs during serialization, the client is notified there was an error getting his input.
     *
     * @return the Boolean taken from the messageQueue
     */
    public boolean getBoolean() {
        boolean val = false;
        try {
            val = (Boolean) messageQueue.take();
        } catch (InterruptedException e) {
            System.out.println("Error getting input. \n");
        }
        return val;
    }

    /**
     * Takes a message previously put on the messageQueue by the GUIManager.
     * If no exception occurs in taking the message and casting it to an Integer, that Integer is returned.
     * If an InterruptedException occurs during serialization, the client is notified there was an error getting his input.
     *
     * @return the Integer taken from the messageQueue
     */
    public int getInteger() {
        int val = -1;
        try {
            val = (Integer) messageQueue.take();
        } catch (InterruptedException e) {
            System.out.println("Error getting input. \n");
        }
        return val;
    }

    /**
     * Takes a message previously put on the messageQueue by the GUIManager.
     * If no exception occurs in taking the message and casting it to an ArrayList of Integer, that ArrayList it is returned.
     * If an InterruptedException occurs during serialization, the client is notified there was an error getting his input.
     *
     * @return the ArrayList of Integers taken from the messageQueue
     */

    public ArrayList<Integer> getIntegers() {
        ArrayList<Integer> val = null;
        try {
            val = (ArrayList<Integer>) messageQueue.take();
        } catch (InterruptedException e) {
            System.out.println("Error getting input. \n");
        }
        return val;
    }

    /**
     * Takes a message previously put on the messageQueue by the GUIManager.
     * If no exception occurs in taking the message and casting it to a String, that String it is returned.
     * If an InterruptedException occurs during serialization, the client is notified there was an error getting his input.
     *
     * @return the String taken from the messageQueue
     */
    public String getString() {
        String val = null;
        try {
            val = (String) messageQueue.take();
        } catch (InterruptedException e) {
            System.out.println("Error getting input. \n");
        }
        return val;
    }

    // send to server

    /**
     * Creates a new message (Pong Message) and writes it on the ObjectOutputStream so that it can be serialized and sent to the server.
     * If an IOException occurs during serialization, the client is notified the server disconnected.
     */
    public void pong() {
        try {
            output.writeObject(new Pong(id));
        } catch (IOException e) {
            try {
                serverQueue.put(new ServerClosed());
            } catch (InterruptedException e2) {
                //
            }
        }
    }

    /**
     * Creates a new message (SendBoolean Message) and writes it on the ObjectOutputStream so that it can be serialized and sent to the server.
     * If an IOException occurs during serialization, the client is notified the server disconnected.
     *
     * @param body the boolean to send to the server in the SendBoolean Message
     */
    public void sendBoolean(boolean body) {
        try {
            output.writeObject(new SendBoolean(id, body));
        } catch (IOException e) {
            try {
                serverQueue.put(new ServerClosed());
            } catch (InterruptedException e2) {
                //
            }
        }
    }

    /**
     * Creates a new message (SendInteger Message) and writes it on the ObjectOutputStream so that it can be serialized and sent to the server.
     * If an IOException occurs during serialization, the client is notified the server disconnected.
     *
     * @param body the int to send to the server in the SendInteger Message
     */
    public void sendInteger(int body) {
        try {
            output.writeObject(new SendInteger(id, body));
        } catch (IOException e) {
            try {
                serverQueue.put(new ServerClosed());
            } catch (InterruptedException e2) {
                //
            }
        }
    }

    /**
     * Creates a new message (SendIntegers Message) and writes it on the ObjectOutputStream so that it can be serialized and sent to the server.
     * If an IOException occurs during serialization, the client is notified the server disconnected.
     *
     * @param body the ArrayList of Integers to send to the server in the SendIntegers Message
     */
    public void sendIntegers(ArrayList<Integer> body) {
        try {
            output.writeObject(new SendIntegers(id, body));
        } catch (IOException e) {
            try {
                serverQueue.put(new ServerClosed());
            } catch (InterruptedException e2) {
                //
            }
        }
    }

    /**
     * Creates a new message (SendString Message) and writes it on the ObjectOutputStream so that it can be serialized and sent to the server.
     * If an IOException occurs during serialization, the client is notified the server disconnected.
     *
     * @param body the String to send to the server in the SendString Message
     */
    public void sendString(String body) {
        try {
            output.writeObject(new SendString(id, body));
        } catch (IOException e) {
            try {
                serverQueue.put(new ServerClosed());
            } catch (InterruptedException e2) {
                //
            }
        }
    }

    // message functions

    /**
     * Allows the Player to choose some Cards between those available.
     * He could be asked to choose the 2 or 3 Cards to be used in a 2 or 3-players Game, or his own Card for the Game.
     * Thanks to the GUIManager, the question is displayed on screen; the input provided by the client is processed and sent to the Server.
     *
     * @param possibleCards an ArrayList containing all the available Cards
     * @param num           the number of Cards to choose
     * @param pickedCards   an ArrayList containing all the already picked Cards
     */
    public void chooseCards(ArrayList<CardView> possibleCards, int num, ArrayList<CardView> pickedCards) {
        manager.chooseCards(possibleCards, num, pickedCards);
        sendIntegers(getIntegers());
    }

    /**
     * Allows the Player to choose a Game name (max 12 characters and not duplicated).
     * Thanks to the GUIManager, the question is displayed on screen; the input provided by the client is processed and sent to the Server.
     *
     * @param taken if the previously chosen Game name is already taken, false otherwise
     */
    public void chooseGameName(boolean taken) {
        manager.chooseGameName(taken);
        String gameRoom = getString();
        sendString(gameRoom);
    }

    /**
     * Allows the Player to choose a Game room.
     * The available Game rooms are displayed on screen; the Player can go back to start a new Game, refresh the list or choose a Game room.
     * Thanks to the GUIManager, the input provided by the client is processed and sent to the Server.
     *
     * @param gameRooms an ArrayList of GameViews containing all the Game Rooms
     */
    public void chooseGameRoom(ArrayList<GameView> gameRooms) {
        manager.chooseGameRoom(gameRooms);
        int choice = getInteger();
        String room;
        switch (choice) {
            case 0:
                room = "/back";
                break;
            case 1:
                room = "/refresh";
                break;
            default:
                room = gameRooms.get(choice - 2).getName();
        }
        sendString(room);
    }

    /**
     * Allows the Player to choose a nickname (max 12 characters and not duplicated).
     * Thanks to the GUIManager, the question is displayed on screen; the input provided by the client is processed and sent to the Server.
     *
     * @param taken true if the previously chosen nickname is already taken, false otherwise
     */
    public void chooseNickname(boolean taken) {
        manager.chooseNickname(taken);
        id = getString();
        manager.setId(id);
        sendString(null);
    }

    /**
     * Allows the Player to choose the number of Players (2 or 3) for the Game he is creating.
     * Thanks to the GUIManager, the question is displayed on screen; the input provided by the client is processed and sent to the Server.
     */
    public void choosePlayersNumber() {
        manager.choosePlayersNumber();
        int num = getInteger();
        sendInteger(num);
    }

    /**
     * Allows the Player to choose a position between those available.
     * Thanks to the GUIManager, the question and the reason of the choice are displayed on screen; the input provided by the client is processed and sent to the Server.
     *
     * @param positions an ArrayList containing CellViews representing all the available positions
     * @param desc      the reason of the choice
     */
    public void choosePosition(ArrayList<CellView> positions, String desc) {
        manager.choosePosition(positions, desc);
        sendInteger(getInteger());
    }

    /**
     * Allows the Player to choose the starting Player.
     * Thanks to the GUIManager, the question is displayed on screen; the input provided by the client is processed and sent to the Server.
     *
     * @param players an ArrayList of PlayerViews representing all the Players involved in the Game
     */
    public void chooseStartingPlayer(ArrayList<PlayerView> players) {
        manager.chooseStartingPlayer(players);
        sendInteger(getInteger());
    }

    /**
     * Allows the Player to choose between starting a new Game or joining an existing one.
     * Thanks to the GUIManager, the question is displayed on screen; the input provided by the client is processed and sent to the Server.
     */
    public void chooseStartJoin() {
        manager.chooseStartJoin();
        int num = getInteger();
        sendBoolean(num == 1);
    }

    /**
     * Allows the Player to answer to a "yes or no question".
     * Thanks to the GUIManager, the question is displayed on screen; the input provided by the client is processed and sent to the Server.
     *
     * @param query the "yes or no question" asked to the Player
     */
    public void chooseYesNo(String query) {
        manager.chooseYesNo(query);
        sendBoolean(getBoolean());

    }

    /**
     * Thanks to the GUIManager, allows displaying the build occurred during a turn.
     * Updates the current GameView to save the new state of the Game.
     *
     * @param buildPosition the CellView representing the position of the building
     * @param godCard       the CardView representing the God Card which eventually allowed this build
     */
    public void displayBuild(CellView buildPosition, CardView godCard) {
        manager.displayBuild(buildPosition, godCard);
        currentGame.setCell(buildPosition);
    }

    /**
     * Thanks to the GUIManager, allows displaying an information about the Game.
     * Updates the current GameView to save the new state of the Game.
     *
     * @param game the GameView representing the current state of the Game
     * @param desc the information
     */
    public void displayGameInfo(GameView game, String desc) {
        manager.displayGameInfo(game, desc);
        currentGame = game;
    }

    /**
     * Thanks to the GUIManager, allows displaying a message from the server.
     *
     * @param message the message to display
     */
    public void displayMessage(String message) {
        manager.displayMessage(message);
    }

    /**
     * Thanks to the GUIManager, allows displaying the move occurred during a turn.
     * Updates the current GameView to save the new state of the Game.
     *
     * @param moves   an HashMap containing pairs of (starting position, final position) for each worker who moved or was forced to move
     * @param godCard the CardView representing the God Card which eventually allowed this move
     */
    public void displayMove(HashMap<CellView, CellView> moves, CardView godCard) {
        manager.displayMove(moves, godCard);
        moves.forEach((startPosition, endPosition) -> {
            CellView newStart = new CellView(
                    startPosition.getPosX(), startPosition.getPosY(), startPosition.getBuildLevel(), startPosition.isDomed(), null
            );
            currentGame.setCell(newStart);
            CellView newEnd = new CellView(
                    endPosition.getPosX(), endPosition.getPosY(), endPosition.getBuildLevel(), endPosition.isDomed(), startPosition.getWorker()
            );
            currentGame.setCell(newEnd);
        });
    }

    /**
     * Thanks to the GUIManager, allows displaying the starting position of a Worker.
     * Updates the current GameView to save the new state of the Game.
     *
     * @param position the starting position of a Worker
     */
    public void displayPlaceWorker(CellView position) {
        manager.displayPlaceWorker(position);
        currentGame.setCell(position);
    }

    /**
     * Thanks to the GUIManager, allows notifying the disconnection of a Player, by showing a message on screen.
     *
     * @param player the PlayerView representing the Player who disconnected
     */
    public void notifyDisconnection(PlayerView player) {
        manager.notifyDisconnection(player);
    }

    /**
     * Thanks to the GUIManager, allows notifying the Game is starting, by showing a message on screen.
     */
    public void notifyGameStarting() {
        manager.notifyGameStarting();
        getBoolean();
        sendBoolean(true);
    }

    /**
     * Thanks to the GUIManager, allows notifying the Game is over, by showing a message on screen.
     */
    public void notifyGameOver() {
        manager.notifyGameOver();
    }

    /**
     * Thanks to the GUIManager, allows notifying a Player that he lost, by showing a message on screen reporting his loss and the reason of it.
     *
     * @param reason the reason of the loss
     * @param winner the PlayerView representing the Player who eventually won, can be null
     */
    public void notifyLoss(String reason, PlayerView winner) {
        manager.notifyLoss(reason, winner);
    }

    /**
     * Thanks to the GUIManager, allows notifying a Player that he won, by showing on screen a message reporting his victory and the reason of it.
     *
     * @param reason the reason of the victory
     */
    public void notifyWin(String reason) {
        manager.notifyWin(reason);
    }

    /**
     * Thanks to the GUIManager, allows notifying the the server is down and then closes the client.
     */
    public void serverClosed() {
        manager.serverClosed();
        getBoolean();
        stop();
    }

}