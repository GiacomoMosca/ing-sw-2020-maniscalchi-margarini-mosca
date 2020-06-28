package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.view.CardView;
import it.polimi.ingsw.view.CellView;
import it.polimi.ingsw.view.GameView;
import it.polimi.ingsw.view.PlayerView;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * GameBoardController class handles the interaction between client and server during the Game.
 * Allows displaying the current state of the Game Board and all the notifications from the beginning until the end of the Game.
 */
public class GameBoardController {

    private final double CELLS_OFFSET = 96.4;
    @FXML
    private AnchorPane p1, p2, p3;
    @FXML
    private Text infoBox, firstPlayerID, secondPlayerID, thirdPlayerID;
    @FXML
    private GridPane buildPane, workerPane, highlightPane;
    @FXML
    private ImageView playerINFO_2P, playerINFO_3P;
    @FXML
    private ImageView playerColor_left_red, playerColor_left_green, playerColor_left_blue;
    @FXML
    private ImageView playerColor_right_red, playerColor_right_green, playerColor_right_blue;
    @FXML
    private ImageView activeModifier1, activeModifier2, activeModifier3;
    @FXML
    private ImageView playerIcon1, playerIcon2, playerIcon3;
    @FXML
    private ImageView playerHighlight1, playerHighlight2, playerHighlight3;
    @FXML
    private StackPane yesPane, noPane;
    @FXML
    private ImageView yesButton, yesButton_p, noButton, noButton_p;
    @FXML
    private Text yesText, yesText_p, noText, noText_p;
    @FXML
    private ImageView logButton, logButton_p;
    @FXML
    private TextArea log;
    @FXML
    private Text disconnectionMessage, gameOverMessage, eliminationMessage, reasonMessage;
    @FXML
    private StackPane opaquePanel1, opaquePanel2, opaquePanel3, godBox, opaqueBackground, infoScreen, winLossScreen;
    @FXML
    private ImageView confirmButton, confirmButton_p, continueButton, continueButton_p;
    @FXML
    private Text confirmText, confirmText_p, continueText, continueText_p;
    @FXML
    private ImageView winOrLossScreen;
    private GUIManager manager;
    private GameView game;
    private ArrayList<String> playersId;
    private ArrayList<CardView> godCards;
    private ArrayList<ImageView> playerHighlights;
    private ArrayList<ImageView> modifiers;
    private ArrayList<CellView> currentPositions;
    private HashMap<Integer, HighlightCell> highlightForThisCell;
    private HashMap<String, ArrayList<ImageView>> workersForThisColor;
    private HashMap<String, ImageView> fullImageForThisGod;
    private HashMap<String, StackPane> opaquePanelForThisPlayer;

    /**
     * @param manager the GUIManager to set the GameBoardController manager attribute to
     */
    public void initialize(GUIManager manager) {
        this.manager = manager;
    }

    /**
     * Creates all the data structure that are needed to handle the displaying of the Game Board.
     * Saves the nicknames of the players that are participating to the Game and their God Cards.
     * Calls three methods that will respectively handle the initialization of the Workers, the initialization of the buildings and the initialization of the information panels surrounding the Board.
     *
     * @param game the GameView representing the current state of the Game
     */
    public void initialize(GameView game) {
        Platform.runLater(() -> {
            this.game = game;
            workersForThisColor = new HashMap<>();
            highlightForThisCell = new HashMap<>();
            fullImageForThisGod = new HashMap<>();
            opaquePanelForThisPlayer = new HashMap<>();
            playersId = new ArrayList<>();
            godCards = new ArrayList<>();
            playerHighlights = new ArrayList<>();
            modifiers = new ArrayList<>();

            for (PlayerView player : game.getPlayers())
                playersId.add(player.getId());
            for (PlayerView player : game.getPlayers())
                godCards.add(player.getGodCard());

            initWorkers();
            initBuildings();
            initInfoPanels();
            manager.setBusy(false);
        });
    }

    /**
     * Handles the initialization of all the ImageViews used to represent red, green and blue workers.
     * An ArrayList of two ImageViews is used for each color. This ArrayList is put in an HashMap (which key is the Worker's color) to simplify the access to the ImageViews.
     * If setting up for a 2-players Game, only red and green Workers are added.
     * If setting up for a 3-players Game, blue Workers are added too.
     */
    public void initWorkers() {
        ArrayList<ImageView> redWorkers = new ArrayList<>();
        ArrayList<ImageView> greenWorkers = new ArrayList<>();
        redWorkers.add(new ImageView("assets/board/workers/worker_red.png"));
        redWorkers.add(new ImageView("assets/board/workers/worker_red.png"));
        greenWorkers.add(new ImageView("assets/board/workers/worker_green.png"));
        greenWorkers.add(new ImageView("assets/board/workers/worker_green.png"));

        workersForThisColor.put("g", greenWorkers);
        workersForThisColor.put("r", redWorkers);

        if (game.getPlayers().size() == 3) {
            ArrayList<ImageView> blueWorkers = new ArrayList<>();
            blueWorkers.add(new ImageView("assets/board/workers/worker_blue.png"));
            blueWorkers.add(new ImageView("assets/board/workers/worker_blue.png"));
            workersForThisColor.put("b", blueWorkers);
        }
    }

    /**
     * Handles the initialization of all the ImageViews used to highlight the Board cells when the Player is asked to make a choice.
     * An ImageView representing the highlight is added over each Board Cell and will be set as visible when needed.
     */
    public void initBuildings() {
        highlightPane.getChildren().clear();
        for (CellView cell : game.getAllCells()) {
            HighlightCell highlight = new HighlightCell("assets/board/buildings/highlight_cell.png", cell.getPosX(), cell.getPosY());
            highlight.setVisible(false);
            highlight.setId("highlight");
            highlightPane.add(highlight, cell.getPosX(), cell.getPosY());
            highlightForThisCell.put(cell.getPosX() * 10 + cell.getPosY(), highlight);
        }
    }

    /**
     * Handles the initialization of all the information panels surrounding the Board, setting as visible:
     * <p><ul>
     * <li> Players' nicknames and colors
     * <li> God Cards icons
     * </ul></p>
     * Also prepares
     * <p><ul>
     * <li> full ImageViews of the God Cards that will be displayed when a God Power is used
     * <li> opaque panels to be put on the eliminated player in a 3-players game
     * </ul></p>
     */
    public void initInfoPanels() {
        firstPlayerID.setText(playersId.get(0));
        firstPlayerID.setVisible(true);
        playerColor_right_red.setVisible(true);
        playerColor_left_red.setVisible(true);
        secondPlayerID.setText(playersId.get(1));
        secondPlayerID.setVisible(true);
        playerColor_left_green.setVisible(true);
        playerColor_right_green.setVisible(true);

        playerHighlights.add(playerHighlight1);
        playerHighlights.add(playerHighlight2);

        playerIcon1.setImage(new Image("/assets/gods/playerIcon/icon_" + godCards.get(0).getGod().toLowerCase() + ".png"));
        Tooltip.install(playerIcon1, new Tooltip(
                "★ " + godCards.get(0).getGod() + "\n" +
                        "☆ " + godCards.get(0).getTitle() + "\n" +
                        godCards.get(0).getDescription()
        ));
        Tooltip.install(activeModifier1, new Tooltip("God Power currently active during opponents' turns"));
        modifiers.add(activeModifier1);
        playerIcon2.setImage(new Image("/assets/gods/playerIcon/icon_" + godCards.get(1).getGod().toLowerCase() + ".png"));
        Tooltip.install(playerIcon2, new Tooltip(
                "★ " + godCards.get(1).getGod() + "\n" +
                        "☆ " + godCards.get(1).getTitle() + "\n" +
                        godCards.get(1).getDescription()
        ));
        Tooltip.install(activeModifier2, new Tooltip("God Power currently active during opponents' turns"));
        modifiers.add(activeModifier2);

        godBox.getChildren().clear();
        ImageView firstFullGod = new ImageView("/assets/gods/godFull/full_" + godCards.get(0).getGod().toLowerCase() + ".png");
        godBox.getChildren().add(firstFullGod);
        fullImageForThisGod.put(godCards.get(0).getGod(), firstFullGod);
        firstFullGod.setVisible(false);
        ImageView secondFullGod = new ImageView("/assets/gods/godFull/full_" + godCards.get(1).getGod().toLowerCase() + ".png");
        godBox.getChildren().add(secondFullGod);
        fullImageForThisGod.put(godCards.get(1).getGod(), secondFullGod);
        secondFullGod.setVisible(false);

        opaquePanelForThisPlayer.put(playersId.get(0), opaquePanel1);
        opaquePanelForThisPlayer.put(playersId.get(1), opaquePanel2);
        infoScreen.setVisible(false);
        winLossScreen.setVisible(false);
        infoBox.setVisible(true);

        if (game.getPlayers().size() == 2) {
            playerINFO_2P.setVisible(true);

        } else {
            playerINFO_3P.setVisible(true);
            p3.setVisible(true);
            playerColor_left_blue.setVisible(true);
            playerColor_right_blue.setVisible(true);
            playerHighlights.add(playerHighlight3);
            thirdPlayerID.setText(playersId.get(2));
            thirdPlayerID.setVisible(true);
            playerIcon3.setImage(new Image("/assets/gods/playerIcon/icon_" + godCards.get(2).getGod().toLowerCase() + ".png"));
            Tooltip.install(playerIcon3, new Tooltip(
                    "★ " + godCards.get(2).getGod() + "\n" +
                            "☆ " + godCards.get(2).getTitle() + "\n" +
                            godCards.get(2).getDescription()
            ));
            Tooltip.install(activeModifier3, new Tooltip("God Power currently active during opponents' turns"));
            modifiers.add(activeModifier3);
            ImageView thirdFullGod = new ImageView("/assets/gods/godFull/full_" + godCards.get(2).getGod().toLowerCase() + ".png");
            godBox.getChildren().add(thirdFullGod);
            fullImageForThisGod.put(godCards.get(2).getGod(), thirdFullGod);
            thirdFullGod.setVisible(false);
            opaquePanelForThisPlayer.put(playersId.get(2), opaquePanel3);
        }

        for (ImageView highlight : playerHighlights) {
            highlight.setId("highlight");
            highlight.setOnMouseClicked(t -> {
                sendStartingPlayer(playerHighlights.indexOf(highlight));
            });
        }
    }

    /**
     * Allows displaying the received information on screen or on the Log.
     * If the information tells about the elimination of a player in a 3-players Game, it is handled.
     *
     * @param game the GameView representing the current state of the Game
     * @param desc the information to display
     */
    public void displayGameInfo(GameView game, String desc) {
        String text = null;
        switch (desc) {
            case "boardSetup":
                text = "Placing workers...";
                break;
            case "gameStart":
                pushToLog("\n=== GAME START ===");
                break;
            case "turnStart":
                text = playersId.get(game.getActivePlayer()) + "'s Turn";
                pushToLog("\n=== " + playersId.get(game.getActivePlayer()) + "'s Turn ===");
                break;
            case "outOfMoves":
            case "outOfBuilds":
            case "outOfWorkers":
                eliminatePlayer(game, desc);
                break;
            default:
                break;
        }
        String finalText = text;
        ArrayList<ImageView> activeModifiers = new ArrayList<ImageView>();
        for (CardView activeModifier : game.getActiveModifiers()) {
            for (PlayerView playerView : game.getPlayers()) {
                if (playerView.getGodCard().getGod().equals(activeModifier.getGod())) {
                    activeModifiers.add(modifiers.get(game.getPlayers().indexOf(playerView)));
                    break;
                }
            }
        }
        Platform.runLater(() -> {
            if (finalText != null) infoBox.setText(finalText);
            for (ImageView modifier : modifiers) modifier.setVisible(false);
            for (ImageView modifier : activeModifiers) modifier.setVisible(true);
            manager.setBusy(false);
        });
    }

    /**
     * Handles the elimination of a Player in a 3-players Game, removing his Workers from the Board and showing an opaque panel on his nickname.
     * If the Player receiving the message is the eliminated one, he is notified of his loss.
     * Otherwise, all the other Players are notified that a player was eliminated.
     *
     * @param game the GameView representing the current state of the Game
     * @param desc the reason the Player was eliminated
     */
    private void eliminatePlayer(GameView game, String desc) {
        PlayerView eliminatedPlayer = null;
        ArrayList<ImageView> workersToRemove = new ArrayList<>();
        String reason;

        for (PlayerView player : game.getPlayers()) {
            if (!player.hasLost()) continue;
            eliminatedPlayer = player;
            break;
        }
        if (eliminatedPlayer == null || eliminatedPlayer.getId().equals(manager.getId())) return;

        workersToRemove.add(workersForThisColor.get(eliminatedPlayer.getColor()).get(0));
        workersToRemove.add(workersForThisColor.get(eliminatedPlayer.getColor()).get(1));
        switch (desc) {
            case "outOfMoves":
                reason = "(No legal moves available)";
                break;
            case "outOfWorkers":
                reason = "(All their workers were removed from the game)";
                break;
            case "outOfBuilds":
                reason = "(No legal builds available)";
                break;
            default:
                reason = null;
                break;
        }
        eliminationMessage.setText(eliminatedPlayer.getId() + " lost!\n" + reason);

        String finalEliminatedPlayer = eliminatedPlayer.getId();
        Platform.runLater(() -> {
            opaqueBackground.setVisible(true);
            infoScreen.setVisible(true);
            opaquePanelForThisPlayer.get(finalEliminatedPlayer).setVisible(true);
            eliminationMessage.setVisible(true);
            workersToRemove.get(0).setVisible(false);
            workersToRemove.get(1).setVisible(false);
            continueButton.setVisible(true);
            continueText.setVisible(true);
        });
    }

    /**
     * Allows the Player to choose the starting Player.
     * Sets the Player highlight to visible and waits for his choice.
     * , when the Player clicks on the one who will start, provides to notice his choice.
     */
    public void chooseStartingPlayer() {
        Platform.runLater(() -> {
            infoBox.setText("Choose the starting player");
            for (ImageView highlight : playerHighlights) {
                highlight.setVisible(true);
            }
        });
    }

    /**
     * When a Player is asked to choose the starting Player, this method provides sending his choice to the GUIManager, which will process it and send it to the server.
     *
     * @param value the Integer corresponding to the starting player
     */
    public void sendStartingPlayer(Integer value) {
        Platform.runLater(() -> {
            for (ImageView highlight : playerHighlights)
                highlight.setVisible(false);
            manager.setBusy(false);
            manager.putObject(value);
        });
    }

    /**
     * Allows choosing a position between all those positions available.
     * The available positions are highlighted and the reason of the choice is written in the textBox.
     *
     * @param positions an ArrayList containing CellViews representing all the available positions
     * @param desc      the reason of the choice
     */
    public void choosePosition(ArrayList<CellView> positions, String desc) {
        String text;
        switch (desc) {
            case "start1":
                text = "Choose the position for your first worker";
                break;
            case "start2":
                text = "Choose the position for your second worker";
                break;
            case "worker":
                text = "Choose the worker you want to use";
                break;
            case "move":
                text = "Choose the position to move to";
                break;
            case "build":
                text = "Choose the position to build in";
                break;
            default:
                text = null;
                break;
        }
        currentPositions = positions;
        Platform.runLater(() -> {
            infoBox.setText(text);
            highlightPane.setVisible(true);
            for (CellView position : positions) {
                highlightForThisCell.get(position.getPosX() * 10 + position.getPosY()).setVisible(true);
            }
        });
    }

    /**
     * When a Player is asked to choose a position between the highlighted ones, this method allows notifying the GUIManager of the choice, which will process it and send it to the server.
     *
     * @param cell the selected cell
     */
    public void sendPosition(HighlightCell cell) {
        int selectedCell = -1;
        for (CellView possibleCell : currentPositions) {
            if (cell.posX == possibleCell.getPosX() && cell.posY == possibleCell.getPosY()) {
                selectedCell = currentPositions.indexOf(possibleCell);
                break;
            }
        }
        manager.setBusy(false);
        manager.putObject(selectedCell);
    }

    /**
     * Allows displaying the placing down of Workers on the Board.
     * The correct worker is set to visible in his starting position.
     *
     * @param position the CellView representing the position of the Worker
     */
    public void displayPlaceWorker(CellView position) {
        Platform.runLater(() -> {
            String workerColor = position.getWorker().getColor();
            int workerNumber = position.getWorker().getNum() - 1;

            workerPane.add(workersForThisColor.get(workerColor).get(workerNumber), position.getPosX(), position.getPosY());
            manager.setBusy(false);
        });
    }

    /**
     * Allows displaying the move of a Worker or of a couple of Workers during a turn.
     * Writes on the log the starting and the final positions.
     * Creates a Transition which shows each involved Worker moving from the starting position to the final position.
     * If the move was allowed thanks to a God Power, shows its images fading before the move is displayed.
     *
     * @param moves   an HashMap containing pairs of (starting position, final position) for each worker who moved or was forced to move
     * @param godCard the CardView representing the God Card which eventually allowed this move
     */
    public void displayMove(HashMap<CellView, CellView> moves, CardView godCard) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(" ⮞\t");
        if (godCard != null) stringBuilder.append("[" + godCard.getGod() + "] ");
        stringBuilder.append("move ");
        boolean second = false;
        for (HashMap.Entry<CellView, CellView> entry : moves.entrySet()) {
            if (second) stringBuilder.append(" and ");
            second = true;
            stringBuilder.append("[" + entry.getKey().getPosX() + "," + entry.getKey().getPosY() + "]");
            stringBuilder.append(" to ");
            stringBuilder.append("[" + entry.getValue().getPosX() + "," + entry.getValue().getPosY() + "]");
        }
        pushToLog(stringBuilder.toString());

        Transition transition;
        if (moves.size() == 2) {
            ArrayList<Transition> transitions = new ArrayList<>();
            moves.forEach((startPosition, endPosition) -> {
                ImageView movingWorker = workersForThisColor.get(startPosition.getWorker().getColor()).get(startPosition.getWorker().getNum() - 1);
                TranslateTransition oneWorkerMoving = new TranslateTransition(Duration.seconds(0.5), movingWorker);
                double offsetX = endPosition.getPosX() - startPosition.getPosX();
                double offsetY = endPosition.getPosY() - startPosition.getPosY();
                oneWorkerMoving.setByX(offsetX * CELLS_OFFSET);
                oneWorkerMoving.setByY(offsetY * CELLS_OFFSET);
                transitions.add(oneWorkerMoving);
            });
            transition = new ParallelTransition(transitions.get(0), transitions.get(1));
        } else {
            TranslateTransition oneWorkerMoving = new TranslateTransition(Duration.seconds(0.5));
            moves.forEach((startPosition, endPosition) -> {
                ImageView movingWorker = workersForThisColor.get(startPosition.getWorker().getColor()).get(startPosition.getWorker().getNum() - 1);
                oneWorkerMoving.setNode(movingWorker);
                double offsetX = endPosition.getPosX() - startPosition.getPosX();
                double offsetY = endPosition.getPosY() - startPosition.getPosY();
                oneWorkerMoving.setByX(offsetX * CELLS_OFFSET);
                oneWorkerMoving.setByY(offsetY * CELLS_OFFSET);
            });
            transition = oneWorkerMoving;
        }

        if (godCard != null) {
            transition = addGodSplash(transition, godCard);
        }

        Transition finalTransition = transition;
        finalTransition.setOnFinished(e -> manager.setBusy(false));
        Platform.runLater(() -> {
            finalTransition.play();
        });
    }

    /**
     * Allows displaying the build of a Worker during a turn.
     * Writes on the log the level and the position of the build.
     * Creates a Transition which shows the new building appearing on its position.
     * If the build was allowed thanks to a God Power, shows its images fading before the build is displayed
     *
     * @param buildPosition the CellView representing the position of the building
     * @param godCard       the CardView representing the God Card which eventually allowed this move
     */
    public void displayBuild(CellView buildPosition, CardView godCard) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(" ✖\t");
        if (godCard != null) stringBuilder.append("[" + godCard.getGod() + "] ");
        stringBuilder.append("build ");
        if (buildPosition.isDomed()) stringBuilder.append("dome");
        else stringBuilder.append("level " + buildPosition.getBuildLevel());
        stringBuilder.append(" on ");
        stringBuilder.append("[" + buildPosition.getPosX() + "," + buildPosition.getPosY() + "]");
        pushToLog(stringBuilder.toString());

        Transition transition;
        ImageView newBuilding;
        ImageView firstBuilding = null;
        if (!buildPosition.isDomed())
            newBuilding = new ImageView("assets/board/buildings/build_" + buildPosition.getBuildLevel() + ".png");
        else
            newBuilding = new ImageView("assets/board/buildings/build_dome.png");
        FadeTransition buildingAppearing = new FadeTransition(Duration.seconds(0.5), newBuilding);
        newBuilding.setOpacity(0);
        buildingAppearing.setFromValue(0);
        buildingAppearing.setToValue(1);

        transition = buildingAppearing;
        if (godCard != null && godCard.getGod().equals("Hephaestus")) {
            firstBuilding = new ImageView("assets/board/buildings/build_" + (buildPosition.getBuildLevel() - 1) + ".png");
            firstBuilding.setOpacity(0);
            FadeTransition firstBuildingAppearing = new FadeTransition(Duration.seconds(0.5), firstBuilding);
            firstBuildingAppearing.setFromValue(0);
            firstBuildingAppearing.setToValue(1);
            transition = new SequentialTransition(firstBuildingAppearing, buildingAppearing);
        } else if (godCard != null && godCard.getGod().equals("Medusa")) {
            FadeTransition removingWorker = new FadeTransition(Duration.seconds(0.5),
                    workersForThisColor.get(buildPosition.getWorker().getColor()).get(buildPosition.getWorker().getNum() - 1));
            removingWorker.setFromValue(1);
            removingWorker.setToValue(0);
            transition = new SequentialTransition(removingWorker, buildingAppearing);
        }
        if (godCard != null) {
            transition = addGodSplash(transition, godCard);
        }

        Transition finalTransition = transition;
        finalTransition.setOnFinished(e -> manager.setBusy(false));
        ImageView finalFirstBuilding = firstBuilding;
        Platform.runLater(() -> {
            if (godCard != null && godCard.getGod().equals("Hephaestus"))
                buildPane.add(finalFirstBuilding, buildPosition.getPosX(), buildPosition.getPosY());
            buildPane.add(newBuilding, buildPosition.getPosX(), buildPosition.getPosY());
            finalTransition.play();
        });
    }

    /**
     * Allows creating a new SequentialTransition including the Transition received as argument (which represents a move or a build) and a new FadeTransition showing the involved God Power fading in and out.
     *
     * @param transition the starting transition
     * @param godCard    the CardView representing the God Card involved in the current turn
     * @return a new SequentialTransition
     */
    private SequentialTransition addGodSplash(Transition transition, CardView godCard) {
        fullImageForThisGod.get(godCard.getGod()).setVisible(true);
        FadeTransition godFadeIn = new FadeTransition(Duration.seconds(0.3), fullImageForThisGod.get(godCard.getGod()));
        godFadeIn.setFromValue(0);
        godFadeIn.setToValue(1);
        FadeTransition godFadeOut = new FadeTransition(Duration.seconds(1), fullImageForThisGod.get(godCard.getGod()));
        godFadeOut.setFromValue(1);
        godFadeOut.setToValue(0);
        return new SequentialTransition(godFadeIn, godFadeOut, transition);
    }

    /**
     * Allows the Player to answer to the "yes or no question" received as argument.
     * The question is written in the textBox and two buttons to choose "yes" or "no" are set to visible.
     *
     * @param query the "yes or no question" the Player should answer to
     */
    public void chooseYesNo(String query) {
        Platform.runLater(() -> {
            infoBox.setText(query);
            yesPane.setVisible(true);
            noPane.setVisible(true);
        });
    }

    /**
     * Notifies a Player that he lost.
     * Shows him a defeat screen, reporting that he lost and the reason of his loss.
     *
     * @param reason the reason of the loss
     * @param player the PlayerView representing the Player who eventually won, can be null
     */
    public void notifyLoss(String reason, PlayerView player) {
        winOrLossScreen.setImage(new Image("/assets/graphics/defeatScreen.png"));
        String lossReason = null;
        switch (reason) {
            case "outOfMoves":
                lossReason = "No legal moves available!";
                break;
            case "outOfWorkers":
                lossReason = "All your workers were removed from the game!";
                break;
            case "outOfBuilds":
                lossReason = "No legal builds available!";
                break;
            case "godConditionAchieved":
                lossReason = player.getId() + "'s worker achieved his god's win condition!";
                break;
            case "winConditionAchieved":
                lossReason = player.getId() + "'s worker reached the top level!";
                break;
            default:
                break;
        }
        String finalLossReason = lossReason;
        Platform.runLater(() -> {
            reasonMessage.setText(finalLossReason);
            winLossScreen.setVisible(true);
        });
    }

    /**
     * Notifies a Player that he won.
     * Shows him a victory screen, reporting that he won and the reason of his victory.
     *
     * @param reason the reason of the victory
     */
    public void notifyWin(String reason) {
        winOrLossScreen.setImage(new Image("/assets/graphics/victoryScreen.png"));
        String winReason;
        switch (reason) {
            case "godConditionAchieved":
                winReason = "You achieved your god's win condition!";
                break;
            case "winConditionAchieved":
                winReason = "You reached the top level!";
                break;
            default:
                winReason = "All other players were eliminated!";
        }
        String finalWinReason = winReason;
        Platform.runLater(() -> {
            reasonMessage.setText(finalWinReason);
            winLossScreen.setVisible(true);
        });
    }

    public void displayMessage(String query) {
        Platform.runLater(() -> {
            infoBox.setText(query);
            manager.setBusy(false);
        });
    }

    /**
     * @param message the String to be written on the Log
     */
    private void pushToLog(String message) {
        Platform.runLater(() -> {
            log.setText(log.getText() + "\n" + message);
            log.selectPositionCaret(log.getLength());
            log.deselect();
        });
    }

    //FXML

    @FXML
    private void yesPressed() {
        Platform.runLater(() -> {
            yesButton_p.setVisible(true);
            yesText_p.setVisible(true);
        });
    }

    @FXML
    private void yesReleased() {
        Platform.runLater(() -> {
            yesButton_p.setVisible(false);
            yesText_p.setVisible(false);
            yesPane.setVisible(false);
            noPane.setVisible(false);
            manager.setBusy(false);
        });
        manager.putObject(true);
    }

    @FXML
    private void noPressed() {
        Platform.runLater(() -> {
            noButton_p.setVisible(true);
            noText_p.setVisible(true);
        });
    }

    @FXML
    private void noReleased() {
        Platform.runLater(() -> {
            noButton_p.setVisible(false);
            noText_p.setVisible(false);
            yesPane.setVisible(false);
            noPane.setVisible(false);
            manager.setBusy(false);
        });
        manager.putObject(false);
    }

    @FXML
    private void confirmButtonPressed() {
        Platform.runLater(() -> {
            confirmText.setVisible(false);
            confirmButton_p.setVisible(true);
            confirmText_p.setVisible(true);
        });
    }

    @FXML
    private void confirmButtonReleased() {
        Platform.runLater(() -> {
            confirmText.setVisible(true);
            confirmButton_p.setVisible(false);
            confirmText_p.setVisible(false);
            winLossScreen.setVisible(false);
            manager.setBusy(false);
        });
    }

    @FXML
    private void continueButtonPressed() {
        Platform.runLater(() -> {
            continueButton_p.setVisible(true);
            continueText.setVisible(false);
            continueText_p.setVisible(true);
        });
    }

    @FXML
    private void continueButtonReleased() {
        Platform.runLater(() -> {
            continueButton_p.setVisible(false);
            continueText_p.setVisible(false);
            infoScreen.setVisible(false);
            disconnectionMessage.setVisible(false);
            eliminationMessage.setVisible(false);
            opaqueBackground.setVisible(false);
            manager.setBusy(false);
        });
    }

    @FXML
    private void logOn() {
        Platform.runLater(() -> {
            log.setVisible(true);
            logButton.setVisible(false);
            logButton_p.setVisible(true);
        });
    }

    @FXML
    private void logOff() {
        Platform.runLater(() -> {
            log.setVisible(false);
            logButton.setVisible(true);
            logButton_p.setVisible(false);
        });
    }

    /**
     * HighlightCell class extends ImageView and is used to
     * A property of HighlightCell objects is to react to a MouseEvent (click) and to send the clicked position to the GUIManager.
     */
    private class HighlightCell extends ImageView {

        public int posX, posY;

        /**
         * HighlightCell constructor.
         * Sets the posX and posY attributes as the values received as arguments and defines a property.
         *
         * @param s the String to associate to this HighlightCell
         * @param x the x-coordinate of this HighlightCell
         * @param y the y-coordinate of this HighlightCell
         */
        public HighlightCell(String s, int x, int y) {
            super(s);
            posX = x;
            posY = y;
            setOnMouseClicked(t -> {
                for (CellView cell : currentPositions)
                    highlightForThisCell.get(cell.getPosX() * 10 + cell.getPosY()).setVisible(false);
                new Thread(() -> sendPosition(this)).start();
            });
        }
    }
}