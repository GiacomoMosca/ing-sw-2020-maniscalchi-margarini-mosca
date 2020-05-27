package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.view.CardView;
import it.polimi.ingsw.view.CellView;
import it.polimi.ingsw.view.GameView;
import it.polimi.ingsw.view.PlayerView;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.HashMap;

//TO DO: player eliminato -> oscurarlo dal pannello (se 3 giocatori)

//TO DO: bottoni in css

//TO DO: rimuovere le immagini dopo averle usate

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
    private ImageView playerIcon1, playerIcon2, playerIcon3;
    @FXML
    private ImageView playerHighlight1, playerHighlight2, playerHighlight3;
    @FXML
    private Button yesButton, noButton;
    @FXML
    private StackPane godBox;
    private GUIManager manager;
    private GameView game;
    private ArrayList<String> playersId;
    private ArrayList<CardView> godCards;
    private ArrayList<ImageView> playerHighlights;
    private ArrayList<CellView> currentPositions;
    private HashMap<Integer, HighlightCell> highlightForThisCell;
    private HashMap<String, ArrayList<ImageView>> workersForThisColor;
    private HashMap<String, ImageView> fullImageForThisGod;

    public void initialize(GUIManager manager) {
        this.manager = manager;
    }

    public void initialize(GameView game) {
        Platform.runLater(() -> {
            this.game = game;
            workersForThisColor = new HashMap<>();
            highlightForThisCell = new HashMap<>();
            fullImageForThisGod = new HashMap<>();
            playersId = new ArrayList<>();
            godCards = new ArrayList<>();
            playerHighlights = new ArrayList<>();

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

    public void initBuildings() {
        highlightPane.getChildren().clear();
        for (CellView cell : game.getAllCells()) {
            HighlightCell highlight = new HighlightCell("assets/board/buildings/highlight_cell.png", cell.getPosX(), cell.getPosY());
            highlight.setVisible(false);
            highlightPane.add(highlight, cell.getPosX(), cell.getPosY());
            highlightForThisCell.put(cell.getPosX() * 10 + cell.getPosY(), highlight);
        }
    }

    public void initInfoPanels() {
        firstPlayerID.setText(playersId.get(0));
        secondPlayerID.setText(playersId.get(1));

        playerHighlights.add(playerHighlight1);
        playerHighlights.add(playerHighlight2);

        playerIcon1.setImage(new Image("/assets/gods/playerIcon/icon_" + godCards.get(0).getGod().toLowerCase() + ".png"));
        Tooltip.install(playerIcon1, new Tooltip(
                "★ " + godCards.get(0).getGod() + "\n" +
                        "☆ " + godCards.get(0).getTitle() + "\n" +
                        godCards.get(0).getDescription()
        ));
        playerIcon2.setImage(new Image("/assets/gods/playerIcon/icon_" + godCards.get(1).getGod().toLowerCase() + ".png"));
        Tooltip.install(playerIcon2, new Tooltip(
                "★ " + godCards.get(1).getGod() + "\n" +
                        "☆ " + godCards.get(1).getTitle() + "\n" +
                        godCards.get(1).getDescription()
        ));

        godBox.getChildren().clear();
        ImageView firstFullGod = new ImageView("/assets/gods/godFull/full_" + godCards.get(0).getGod().toLowerCase() + ".png");
        godBox.getChildren().add(firstFullGod);
        fullImageForThisGod.put(godCards.get(0).getGod(), firstFullGod);
        firstFullGod.setVisible(false);
        ImageView secondFullGod = new ImageView("/assets/gods/godFull/full_" + godCards.get(1).getGod().toLowerCase() + ".png");
        godBox.getChildren().add(secondFullGod);
        fullImageForThisGod.put(godCards.get(1).getGod(), secondFullGod);
        secondFullGod.setVisible(false);


        if (game.getPlayers().size() == 2) {
            playerINFO_2P.setVisible(true);

        } else {
            playerINFO_3P.setVisible(true);
            p3.setVisible(true);
            playerHighlights.add(playerHighlight3);
            thirdPlayerID.setText(playersId.get(2));
            playerIcon3.setImage(new Image("/assets/gods/playerIcon/icon_" + godCards.get(2).getGod().toLowerCase() + ".png"));
            Tooltip.install(playerIcon3, new Tooltip(
                    "★ " + godCards.get(2).getGod() + "\n" +
                            "☆ " + godCards.get(2).getTitle() + "\n" +
                            godCards.get(2).getDescription()
            ));
            ImageView thirdFullGod = new ImageView("/assets/gods/godFull/full_" + godCards.get(2).getGod().toLowerCase() + ".png");
            godBox.getChildren().add(thirdFullGod);
            fullImageForThisGod.put(godCards.get(2).getGod(), thirdFullGod);
            thirdFullGod.setVisible(false);
        }

        for (ImageView highlight : playerHighlights)
            highlight.setOnMouseClicked(t -> {
                sendStartingPlayer(playerHighlights.indexOf(highlight));
            });

        BackgroundImage yesButtonBackgroundImage = new BackgroundImage(
                new Image(getClass().getResource("/assets/buttons/btn_small_blue.png").toExternalForm()),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT
        );
        BackgroundImage noButtonBackgroundImage = new BackgroundImage(
                new Image(getClass().getResource("/assets/buttons/btn_small_blue.png").toExternalForm()),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT
        );
        Background yesButtonBackground = new Background(yesButtonBackgroundImage);
        Background noButtonBackground = new Background(noButtonBackgroundImage);

        yesButton.setBackground(yesButtonBackground);
        noButton.setBackground(noButtonBackground);
        yesButton.setVisible(false);
        noButton.setVisible(false);
        yesButton.setOnMouseClicked(t -> {
            sendYesNoChoice(true);
        });
        noButton.setOnMouseClicked(t -> {
            sendYesNoChoice(false);
        });
    }

    public void displayGameInfo(GameView game, String desc) {
        String text;
        if (desc.equals("gameSetup2")) text = "Placing workers...";
        else text = playersId.get(game.getActivePlayer()) + "'s Turn";
        Platform.runLater(() -> {
            infoBox.setText(text);
            manager.setBusy(false);
        });
    }

    public void chooseStartingPlayer() {
        Platform.runLater(() -> {
            infoBox.setText("Choose the starting player");
            for (ImageView highlight : playerHighlights) {
                highlight.setVisible(true);
            }
        });
    }

    public void sendStartingPlayer(Integer value) {
        Platform.runLater(() -> {
            for (ImageView highlight : playerHighlights)
                highlight.setVisible(false);
            manager.setBusy(false);
            manager.putObject(value);
        });
    }

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

    public void displayPlaceWorker(CellView position) {
        Platform.runLater(() -> {
            String workerColor = position.getWorker().getColor();
            int workerNumber = position.getWorker().getNum() - 1;

            workerPane.add(workersForThisColor.get(workerColor).get(workerNumber), position.getPosX(), position.getPosY());
            manager.setBusy(false);
        });
    }

    public void displayMove(HashMap<CellView, CellView> moves, CardView godCard) {
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

    public void displayBuild(CellView buildPosition, CardView godCard) {
        if (buildPosition.hasWorker()) System.out.println("worker here!");
        Transition transition;
        ImageView newBuilding;
        if (!buildPosition.isDomed())
            newBuilding = new ImageView("assets/board/buildings/build_" + buildPosition.getBuildLevel() + ".png");
        else
            newBuilding = new ImageView("assets/board/buildings/build_dome.png");
        FadeTransition buildingAppearing = new FadeTransition(Duration.seconds(0.5), newBuilding);
        buildingAppearing.setFromValue(0);
        buildingAppearing.setToValue(1);

        transition = buildingAppearing;
        if (godCard != null) {
            transition = addGodSplash(transition, godCard);
        }

        Transition finalTransition = transition;
        finalTransition.setOnFinished(e -> manager.setBusy(false));
        Platform.runLater(() -> {
            buildPane.add(newBuilding, buildPosition.getPosX(), buildPosition.getPosY());
            finalTransition.play();
        });
    }

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

    public void chooseYesNo(String query) {
        Platform.runLater(() -> {
            infoBox.setText(query);
            yesButton.setVisible(true);
            noButton.setVisible(true);
        });
    }

    public void sendYesNoChoice(boolean answer) {
        Platform.runLater(() -> {
            yesButton.setVisible(false);
            noButton.setVisible(false);
            manager.setBusy(false);
            manager.putObject(answer);
        });
    }

    //TO DO

    public void notifyDisconnection() {
        Platform.runLater(() -> {
            infoBox.setText("Disconnected!");
            manager.setBusy(false);
        });
    }

    public void notifyGameOver() {
        Platform.runLater(() -> {
            infoBox.setText("Game over!");
            manager.setBusy(false);
        });
    }

    public void notifyLoss() {
        Platform.runLater(() -> {
            infoBox.setText("You lose!");
            manager.setBusy(false);
        });
    }

    public void notifyWin() {
        Platform.runLater(() -> {
            infoBox.setText("You win!");
            manager.setBusy(false);
        });
    }

    public void displayMessage(String query) {
        Platform.runLater(() -> {
            infoBox.setText(query);
            manager.setBusy(false);
        });
    }

    private class HighlightCell extends ImageView {

        public int posX, posY;

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