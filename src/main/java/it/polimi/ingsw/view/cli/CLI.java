package it.polimi.ingsw.view.cli;

import it.polimi.ingsw.network.message.to_client.Ping;
import it.polimi.ingsw.network.message.to_client.ToClientMessage;
import it.polimi.ingsw.network.message.to_server.*;
import it.polimi.ingsw.view.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * CLI class allows the client to interact with the server.
 */
public class CLI implements UI {

    private final AtomicBoolean running;
    private Socket server;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private LinkedBlockingQueue<ToClientMessage> serverQueue;
    private SynchronousQueue<String> messageQueue;
    private String id;
    private GameView currentGame;

    /**
     * CLI constructor.
     */
    public CLI() {
        this.running = new AtomicBoolean();
        this.id = null;
    }

    /**
     * Allows CLI to run until the Game is over.
     * Handles the connection of the client to the server and opens a communication channel between them.
     * If an IOException or a ClassCastException occur while opening that channel, CLI stops.
     * Creates a messageQueue where the input provided by the client will be offered, and a serverQueue where the messages from the server will be put after deserialization.
     * Starts two threads which respectively allows listening to the client input and listening to the server messages.
     * Everytime a message from the server is put on the serverQueue, it is parsed to the corresponding action.
     */
    public void run() {
        running.set(true);
        currentGame = null;
        messageQueue = new SynchronousQueue<String>();
        new Thread(this::inputListener).start();

        String ip = getServerIp();
        server = new Socket();
        try {
            server.connect(new InetSocketAddress(ip, 8000), 5 * 1000);
        } catch (IOException e) {
            System.out.println("Server unreachable. ");
            stop();
            return;
        }
        System.out.println("Connected! ");

        try {
            output = new ObjectOutputStream(server.getOutputStream());
            input = new ObjectInputStream(server.getInputStream());
        } catch (IOException e) {
            System.out.println("Server is down. ");
            stop();
            return;
        } catch (ClassCastException e) {
            System.out.println("Protocol violation. ");
            stop();
            return;
        }

        serverQueue = new LinkedBlockingQueue<ToClientMessage>();
        new Thread(this::serverListener).start();
        ToClientMessage message;
        while (running.get()) {
            try {
                message = serverQueue.take();
            } catch (InterruptedException e) {
                System.out.println("Disconnected. ");
                break;
            }
            parseMessage(message);
        }
        stop();
    }

    /**
     * Permanently listens to the client, taking its input from the command line and offering it on the messageQueue so that it can be processed and sent to the server.
     */
    private void inputListener() {
        Scanner scanner = new Scanner(System.in);
        while (running.get()) {
            String input = scanner.nextLine();
            switch (input) {
                // more commands go here
                case "/quit":
                    quit();
                    break;
            }
            messageQueue.offer(input);
        }
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
                running.compareAndSet(true, false);
                System.out.println("Disconnected. ");
                break;
            }
            if (serverMessage instanceof Ping) pong();
            else {
                try {
                    serverQueue.put(serverMessage);
                } catch (InterruptedException e) {
                    //
                }
            }
        }
    }

    /**
     * Calls the stop method.
     */
    private void quit() {
        stop();
    }

    /**
     * If the Game is over, closes the communication channel with the server.
     */
    public synchronized void stop() {
        if (!running.compareAndSet(true, false)) return;
        System.out.println("\nPress ENTER to quit. ");
        try {
            if (server != null) server.close();
            if (input != null) input.close();
            if (output != null) output.close();
        } catch (IOException e) {
            //
        }
    }

    /**
     * Allows receiving from the client the IP of the server he wants to connect to.
     * Prints to the command line the request and then calls getString so that the input written by the client can be read and returned.
     *
     * @return the String written by the client
     */
    public String getServerIp() {
        System.out.println("\nServer IP address: ");
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

    /**
     * Takes a message previously put on the messageQueue.
     * If no exception occurs in the parsing of the message to an Integer, that Integer is returned.
     * If a NumberFormatException occurs during parsing, the client is notified he wrote an invalid input.
     * If an InterruptedException occurs during serialization, the client is notified there was an error getting his input.
     *
     * @return the Integer taken from the messageQueue
     */
    private int getInt() {
        while (true) {
            try {
                return Integer.parseInt(messageQueue.take());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. ");
            } catch (InterruptedException e) {
                System.out.println("Error getting input. \n");
            }
        }
    }

    /**
     * Takes a message previously put on the messageQueue.
     * If no exception occurs in taking the message, it is returned.
     * If an InterruptedException occurs during serialization, the client is notified there was an error getting his input.
     *
     * @return the String taken from the messageQueue
     */
    private String getString() {
        while (true) {
            try {
                return messageQueue.take();
            } catch (InterruptedException e) {
                System.out.println("Error getting input. \n");
            }
        }
    }

    /**
     * Creates a new message (Pong Message) and writes it on the ObjectOutputStream so that it can be serialized and sent to the server.
     * If an IOException occurs during serialization, the client is notified the server disconnected.
     */
    public void pong() {
        try {
            output.writeObject(new Pong(id));
        } catch (IOException e) {
            System.out.println("Disconnected. ");
            stop();
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
            System.out.println("Disconnected. ");
            stop();
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
            System.out.println("Disconnected. ");
            stop();
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
            System.out.println("Disconnected. ");
            stop();
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
            System.out.println("Disconnected. ");
            stop();
        }
    }

    /**
     * Allows the Player to choose some Cards between those available.
     * He could be asked to choose the 2 or 3 Cards to be used in a 2 or 3-players Game, or his own Card for the Game.
     * The question is displayed on screen; the input provided by the client is processed and sent to the Server.
     *
     * @param possibleCards an ArrayList containing all the available Cards
     * @param num           the number of Cards to choose
     * @param pickedCards   an ArrayList containing all the already picked Cards
     */
    public void chooseCards(ArrayList<CardView> possibleCards, int num, ArrayList<CardView> pickedCards) {
        StringBuilder string = new StringBuilder();
        if (num > 1) string.append("\nChoose the " + num + " God Powers that will be used for this game: \n");
        else string.append("\nPick your God Power: \n");
        for (int i = 0; i < possibleCards.size(); i++) {
            string.append(i + ": ");
            string.append(possibleCards.get(i).getGod() + "\n");
        }
        if (pickedCards != null) for (CardView pickedCard : pickedCards) {
            string.append("X: ");
            string.append(pickedCard.getGod() + "\n");
        }
        System.out.println(string);
        ArrayList<Integer> choices = new ArrayList<Integer>();
        for (int i = 0; i < num; i++) {
            int choice = getInt();
            while (choice < 0 || choice >= possibleCards.size() || choices.contains(choice)) {
                System.out.println("Invalid input. ");
                choice = getInt();
            }
            System.out.println("Picked " + possibleCards.get(choice).getGod());
            choices.add(choice);
        }
        sendIntegers(choices);
    }

    /**
     * Allows the Player to choose a Game name (max 12 characters and not duplicated).
     * The question is displayed on screen; the input provided by the client is processed and sent to the Server.
     *
     * @param taken if the previously chosen Game name is already taken, false otherwise
     */
    public void chooseGameName(boolean taken) {
        if (taken) System.out.println("\nName already taken. ");
        else System.out.println("\nChoose a name for your game room: ");
        String gameRoom;
        while (true) {
            gameRoom = getString();
            if (gameRoom.length() > 12) System.out.println("Invalid input (max 12 characters). ");
            else break;
        }
        sendString(gameRoom);
    }

    /**
     * Allows the Player to choose a Game room.
     * The available Game rooms are displayed on screen; the Player can go back to start a new Game, refresh the list or choose a Game room.
     * The input provided by the client is processed and sent to the Server.
     *
     * @param gameRooms an ArrayList of GameViews containing all the Game Rooms
     */
    public void chooseGameRoom(ArrayList<GameView> gameRooms) {
        StringBuilder string = new StringBuilder();
        string.append("\n0: Back ");
        string.append("\n1: Refresh list \n");
        string.append("\nGame rooms: ");
        int i = 1;
        for (GameView game : gameRooms) {
            i++;
            string.append("\n");
            string.append(i + ": ");
            string.append(game.getName());
            string.append(" (" + game.getPlayers().size() + "/" + game.getPlayerNum() + ") ");
        }
        System.out.println(string);
        int choice = getInt();
        while (choice < 0 || choice > i) {
            System.out.println("Invalid input. ");
            choice = getInt();
        }
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
     * The question is displayed on screen; the input provided by the client is processed and sent to the Server.
     *
     * @param taken true if the previously chosen nickname is already taken, false otherwise
     */
    public void chooseNickname(boolean taken) {
        if (taken) System.out.println("\nNickname already taken. ");
        else System.out.println("\nChoose your nickname: ");
        while (true) {
            id = getString();
            if (id.length() > 12) System.out.println("Invalid input (max 12 characters). ");
            else break;
        }
        sendString(null);
    }

    /**
     * Allows the Player to choose the number of Players (2 or 3) for the Game he is creating.
     * The question is displayed on screen; the input provided by the client is processed and sent to the Server.
     */
    public void choosePlayersNumber() {
        System.out.println("\nSetting up a new game! Choose the number of players (2 or 3):");
        int num = getInt();
        while (num < 2 || num > 3) {
            System.out.println("Invalid input. ");
            num = getInt();
        }
        sendInteger(num);
    }

    /**
     * Allows the Player to choose a position between those available.
     * The question and the reason of the choice are displayed on screen; the input provided by the client is processed and sent to the Server.
     *
     * @param positions an ArrayList containing CellViews representing all the available positions
     * @param desc      the reason of the choice
     */
    public void choosePosition(ArrayList<CellView> positions, String desc) {
        StringBuilder string = new StringBuilder();
        string.append("\n");
        switch (desc) {
            case "start1":
                string.append("(Worker 1) Choose the starting position for your worker:");
                break;
            case "start2":
                string.append("(Worker 2) Choose the starting position for your worker:");
                break;
            case "worker":
                string.append("Choose a worker:");
                break;
            case "move":
                string.append("Choose a position to move to:");
                break;
            case "build":
                string.append("Choose a position to build in:");
                break;
        }
        string.append("\n");
        for (int i = 0; i < positions.size(); i++) {
            CellView cell = positions.get(i);
            if (i > 0) {
                string.append(", ");
                if (i % 5 == 0) string.append("\n");
            }
            string.append(i + ": ");
            string.append("[" + cell.getPosX() + ", " + cell.getPosY() + "]");
        }
        string.append("\n");
        System.out.println(string);
        int choice = getInt();
        while (choice < 0 || choice >= positions.size()) {
            System.out.println("Invalid input. ");
            choice = getInt();
        }
        sendInteger(choice);
    }

    /**
     * Allows the Player to choose the starting Player.
     * The question is displayed on screen; the input provided by the client is processed and sent to the Server.
     *
     * @param players an ArrayList of PlayerViews representing all the Players involved in the Game
     */
    public void chooseStartingPlayer(ArrayList<PlayerView> players) {
        StringBuilder string = new StringBuilder();
        string.append("\nChoose the starting player: \n");
        for (int i = 0; i < players.size(); i++) {
            string.append(i + ": ");
            string.append(players.get(i).getId() + "\n");
        }
        System.out.println(string);
        int choice = getInt();
        while (choice < 0 || choice >= players.size()) {
            System.out.println("Invalid input. ");
            choice = getInt();
        }
        sendInteger(choice);
    }

    /**
     * Allows the Player to choose between starting a new Game or joining an existing one.
     * The question is displayed on screen; the input provided by the client is processed and sent to the Server.
     */
    public void chooseStartJoin() {
        System.out.println("\n1: Start a new game \n2: Join a game ");
        int num = getInt();
        while (num < 1 || num > 2) {
            System.out.println("Invalid input. ");
            num = getInt();
        }
        sendBoolean(num == 1);
    }

    /**
     * Allows the Player to answer to a "yes or no question".
     * The question is displayed on screen; the input provided by the client is processed and sent to the Server.
     *
     * @param query the "yes or no question" asked to the Player
     */
    public void chooseYesNo(String query) {
        System.out.println("\n" + query + " (y/n) ");
        String choice = getString();
        while (!choice.equals("y") && !choice.equals("n")) {
            System.out.println("Invalid input. ");
            choice = getString();
        }
        boolean res = choice.equals("y");
        sendBoolean(res);
    }

    /**
     * Allows displaying the build occurred during a turn, by showing the updated Game Board.
     *
     * @param buildPosition the CellView representing the position of the building
     * @param godCard       the CardView representing the God Card which eventually allowed this build
     */
    public void displayBuild(CellView buildPosition, CardView godCard) {
        currentGame.setCell(buildPosition);
        displayBoard();
    }

    /**
     * Allows displaying an information about the Game, by showing a message on screen.
     *
     * @param game the GameView representing the current state of the Game
     * @param desc the String describing the information to show
     */
    public void displayGameInfo(GameView game, String desc) {
        currentGame = game;
        // TO DO: check if game is ok?
        // TO DO: display player info? description?
        displayBoard();
    }

    public void displayMessage(String message) {
        System.out.println("\n" + message);
    }

    /**
     * Allows displaying the move occurred during a turn, by showing the updated Game Board.
     *
     * @param moves   an HashMap containing pairs of (starting position, final position) for each worker who moved or was forced to move
     * @param godCard the CardView representing the God Card which eventually allowed this move
     */
    public void displayMove(HashMap<CellView, CellView> moves, CardView godCard) {
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
        displayBoard();
    }

    /**
     * Allows displaying the starting position of a Worker, by showing the updated Game Board.
     *
     * @param position the starting position of a Worker
     */
    public void displayPlaceWorker(CellView position) {
        currentGame.setCell(position);
        displayBoard();
    }

    /**
     * Allows notifying the disconnection of a Player, by showing a message on screen.
     *
     * @param player the PlayerView representing the Player who disconnected
     */
    public void notifyDisconnection(PlayerView player) {
        System.out.println("\n" + player.getId() + " has disconnected. ");
    }

    /**
     * Allows notifying the Game is over, by showing a message on screen.
     */
    public void notifyGameOver() {
        System.out.println("\nGame over! \n\n\n\n\n");
    }

    /**
     * Allows notifying the Game is starting, by showing a message on screen.
     */
    public void notifyGameStarting() {
        System.out.println("\nGame is starting! Press ENTER to continue. ");
        getString();
        sendBoolean(true);
    }

    /**
     * Allows notifying a Player that he lost, by showing a message on screen reporting his loss and the reason of it.
     *
     * @param reason the reason of the loss
     * @param winner the PlayerView representing the Player who eventually won, can be null
     */
    public void notifyLoss(String reason, PlayerView winner) {
        StringBuilder string = new StringBuilder();
        string.append("You lost! ");
        if (winner != null) {
            string.append(winner.getId() + " won!");
        } else {
            switch (reason) {
                case "outOfMoves":
                    string.append("(No legal moves available)\n");
                    break;
                case "outOfWorkers":
                    string.append("(All workers have been removed from the game)\n");
                    break;
                default:
                    break;
            }
        }
        System.out.println(string);
    }

    /**
     * Allows notifying a Player that he won, by showing on screen a message reporting his victory and the reason of it.
     *
     * @param reason the reason of the victory
     */
    public void notifyWin(String reason) {
        StringBuilder string = new StringBuilder();
        string.append("Congratulations! You won! ");
        switch (reason) {
            case "winConditionAchieved":
                string.append("(Win condition achieved)\n");
                break;
            case "outOfWorkers":
                string.append("(All other players were eliminated)\n");
                break;
            default:
                break;
        }
        System.out.println(string);
    }

    /**
     * Allows displaying the current state of the Game Board on the CLI, using the following notation:
     * <p><ul>
     * <li> " " if the cell is unoccupied
     * <li> "X"  if the cell has a Dome
     * <li> "1" if the cell is at building level 1
     * <li> "2" if the cell is at building level 2
     * <li> "3" if the cell is at building level 3
     * <li> "r" if the cell is occupied by a red Worker
     * <li> "g" if the cell is occupied by a green Worker
     * <li> "b" if the cell is occupied by a blue Worker
     * </ul></p>
     */
    private void displayBoard() {
        StringBuilder string = new StringBuilder();
        string.append("\n    0  1  2  3  4 ");
        string.append("\n");
        for (int i = 0; i < 5; i++) {
            string.append("  ----------------");
            string.append("\n");
            string.append(i + " ");
            for (int j = 0; j < 5; j++) {
                CellView cell = currentGame.getCell(i, j);
                string.append("|");
                if (cell.isDomed()) string.append("X");
                else string.append(cell.getBuildLevel() == 0 ? " " : cell.getBuildLevel());
                if (cell.hasWorker()) string.append(cell.getWorker().getColor());
                else string.append(" ");
            }
            string.append("|");
            string.append("\n");
        }
        string.append("  ----------------");
        string.append("\n");
        System.out.println(string);
    }

}
