package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.view.CardView;
import it.polimi.ingsw.view.CellView;
import it.polimi.ingsw.view.GameView;
import it.polimi.ingsw.view.PlayerView;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.HashMap;


//cosa non molto bella: illuminazione build parte mentre worker si muove. si può cliccare sulla cella in cui costruire
//ancora prima della fine della mossa

//non riesco a portare in primo piano l'apparizione in grande della carta. e bisogna centrare le img nello stackpane

//mi sa che il potere di Efesto viene mostrato dopo essere stato usato

//TO DO: player eliminato -> oscurarlo dal pannello (se 3 giocatori)

//TO DO: bottoni in css

//TO DO: rimuovere le immagini dopo averle usate

public class GameBoardController {

    @FXML
    private Text textBox, firstPlayerID, secondPlayerID, thirdPlayerID, activePlayer;
    @FXML
    private GridPane gridPane;
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

    private final double CELLS_OFFSET = 96.4;

    private GameView game;
    private ArrayList<String> playersId;
    private ArrayList<String> godCards;
    private ArrayList<ImageView> playerHighlights;
    private HashMap<String, ArrayList<ImageView>> workersForThisColor;
    private HashMap<Point2D, ImageView> highlightForThisCell;
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
        redWorkers.add(new ImageView("assets/workers/worker_red.png"));
        redWorkers.add(new ImageView("assets/workers/worker_red.png"));
        greenWorkers.add(new ImageView("assets/workers/worker_green.png"));
        greenWorkers.add(new ImageView("assets/workers/worker_green.png"));

        workersForThisColor.put("g", greenWorkers);
        workersForThisColor.put("r", redWorkers);

        if (game.getPlayers().size() == 3) {
            ArrayList<ImageView> blueWorkers = new ArrayList<>();
            blueWorkers.add(new ImageView("assets/workers/worker_blue.png"));
            blueWorkers.add(new ImageView("assets/workers/worker_blue.png"));
            workersForThisColor.put("b", blueWorkers);
        }
    }

    public void initBuildings() {
        for (CellView cell : game.getAllCells()) {
            ImageView highlight = new ImageView("assets/buildings/highlight_cell.png");
            gridPane.add(highlight, cell.getPosX(), cell.getPosY());
            gridPane.setHalignment(highlight, HPos.CENTER);
            highlight.setVisible(false);
            highlightForThisCell.put(new Point2D(cell.getPosX(), cell.getPosY()), highlight);
        }
    }

    public void initInfoPanels() {
        firstPlayerID.setText(playersId.get(0));
        secondPlayerID.setText(playersId.get(1));

        playerColor_left_red.setVisible(true);
        playerColor_left_green.setVisible(true);
        playerColor_right_red.setVisible(true);
        playerColor_right_green.setVisible(true);
        playerColor_left_red.toFront();
        playerColor_left_green.toFront();
        playerColor_right_red.toFront();
        playerColor_right_green.toFront();

        playerHighlights.add(playerHighlight1);
        playerHighlights.add(playerHighlight2);

        playerIcon1.setImage(new Image("/assets/cards/playerIcon/icon_" + godCards.get(0) + ".png"));
        playerIcon1.setVisible(true);
        playerIcon2.toFront();
        playerIcon2.setImage(new Image("/assets/cards/playerIcon/icon_" + godCards.get(1) + ".png"));
        playerIcon2.toFront();
        playerIcon2.setVisible(true);

        ImageView firstFullGod=new ImageView("/assets/cards/godFull/full_" + godCards.get(0) + ".png");
        godBox.getChildren().add(firstFullGod);
        fullImageForThisGod.put(godCards.get(0), firstFullGod);
        firstFullGod.setVisible(false);
        ImageView secondFullGod=new ImageView("/assets/cards/godFull/full_" + godCards.get(1) + ".png");
        godBox.getChildren().add(secondFullGod);
        fullImageForThisGod.put(godCards.get(1), secondFullGod);
        secondFullGod.setVisible(false);


        if (game.getPlayers().size() == 2) {
            playerINFO_2P.setVisible(true);
            thirdPlayerID.setVisible(false);

        } else {
            playerINFO_3P.setVisible(true);
            playerColor_left_blue.setVisible(true);
            playerColor_right_blue.setVisible(true);
            playerColor_left_blue.toFront();
            playerColor_right_blue.toFront();
            playerHighlights.add(playerHighlight3);

            thirdPlayerID.setText(playersId.get(2));

            playerIcon3.setImage(new Image("/assets/cards/playerIcon/icon_" + godCards.get(2) + ".png"));
            playerIcon3.toFront();
            playerIcon3.setVisible(true);

            ImageView thirdFullGod = new ImageView("/assets/cards/godFull/full_" + godCards.get(2) + ".png");
            godBox.getChildren().add(thirdFullGod);
            fullImageForThisGod.put(godCards.get(2), thirdFullGod);
            thirdFullGod.setVisible(false);
        }

        BackgroundImage yesButtonBackgroundImage = new BackgroundImage(new Image(getClass().getResource("/assets/infoBox/button_yes.png").toExternalForm()), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
        BackgroundImage noButtonBackgroundImage = new BackgroundImage(new Image(getClass().getResource("/assets/infoBox/button_no.png").toExternalForm()), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
        Background yesButtonBackground = new Background(yesButtonBackgroundImage);
        Background noButtonBackground = new Background(noButtonBackgroundImage);

        yesButton.setBackground(yesButtonBackground);
        noButton.setBackground(noButtonBackground);
        yesButton.setVisible(false);
        noButton.setVisible(false);
    }

    public void displayGameInfo(GameView game, String desc) {
        Platform.runLater(() -> {
            activePlayer.setText("[" + playersId.get(game.getActivePlayer()) + "'s turn]");
            textBox.setText(desc);
            manager.setBusy(false);
        });
    }

    public void chooseStartingPlayer() {
        Platform.runLater(() -> {
            textBox.setText("Choose starting player");
            for (ImageView highlight : playerHighlights) {
                highlight.setVisible(true);
                highlight.setOnMouseClicked(t -> {
                    sendStartingPlayer(playerHighlights.indexOf(highlight));
                });
            }
            manager.setBusy(false);
        });
    }

    public void sendStartingPlayer(Integer value) {
        Platform.runLater(() -> {
            for (ImageView highlight : playerHighlights)
                highlight.setVisible(false);
        });
        manager.putObject(value);
    }

    public void choosePosition(ArrayList<CellView> positions, String desc) {
        Platform.runLater(() -> {
            textBox.setText(desc);
            for (CellView position : positions) {
                Point2D point = new Point2D(position.getPosX(), position.getPosY());
                highlightForThisCell.get(point).setVisible(true);
                highlightForThisCell.get(point).toFront();
                highlightForThisCell.get(point).setOnMouseClicked(t -> {
                    for (CellView cell : positions) {
                        Point2D clickedCell = new Point2D(cell.getPosX(), cell.getPosY());
                        highlightForThisCell.get(clickedCell).setVisible(false);
                    }
                    manager.putObject(positions.indexOf(position));
                });
            }
            manager.setBusy(false);
        });
    }

    public void displayPlaceWorker(CellView position) {
        Platform.runLater(() -> {
            textBox.setText("Display place worker");

            String workerColor = position.getWorker().getColor();
            int workerNumber = position.getWorker().getNum() - 1;

            gridPane.add(workersForThisColor.get(workerColor).get(workerNumber), position.getPosX(), position.getPosY());
            GridPane.setHalignment(workersForThisColor.get(workerColor).get(workerNumber), HPos.CENTER);                    //serve?
            workersForThisColor.get(workerColor).get(workerNumber).toFront();
            workersForThisColor.get(workerColor).get(workerNumber).setVisible(true);
            manager.setBusy(false);
        });
    }

    //sicuramente si può abbreviare
    public void displayMove(HashMap<CellView, CellView> moves, CardView godCard) {
        Platform.runLater(() -> {
            textBox.setText("Display move");

            if (moves.size() == 2) {
                ArrayList<Transition> transitions = new ArrayList<>();
                moves.forEach((startPosition, endPosition) -> {
                    ImageView movingWorker = workersForThisColor.get(startPosition.getWorker().getColor()).get(startPosition.getWorker().getNum() - 1);
                    TranslateTransition oneWorkerMoving = new TranslateTransition(Duration.seconds(1), movingWorker);
                    movingWorker.toFront();

                    double offsetX = endPosition.getPosX() - startPosition.getPosX();
                    double offsetY = endPosition.getPosY() - startPosition.getPosY();

                    oneWorkerMoving.setByX(offsetX * CELLS_OFFSET);
                    oneWorkerMoving.setByY(offsetY * CELLS_OFFSET);
                    transitions.add(oneWorkerMoving);
                });
                ParallelTransition twoWorkersMoving = new ParallelTransition(transitions.get(0), transitions.get(1));

                if (godCard != null) {
                    textBox.setText("Display godPower");
                    FadeTransition godAppearing = new FadeTransition(Duration.seconds(2), fullImageForThisGod.get(godCard.getGod()));
                    fullImageForThisGod.get(godCard.getGod()).setVisible(true);
                    fullImageForThisGod.get(godCard.getGod()).toFront();
                    godAppearing.setFromValue(1);
                    godAppearing.setToValue(0);

                    SequentialTransition sequentialTransition = new SequentialTransition(godAppearing, twoWorkersMoving);
                    sequentialTransition.play();
                } else
                    twoWorkersMoving.play();

            } else {
                TranslateTransition oneWorkerMoving = new TranslateTransition(Duration.seconds(1));
                moves.forEach((startPosition, endPosition) -> {
                    ImageView movingWorker = workersForThisColor.get(startPosition.getWorker().getColor()).get(startPosition.getWorker().getNum() - 1);   //check
                    oneWorkerMoving.setNode(movingWorker);
                    movingWorker.toFront();
                    double offsetX = endPosition.getPosX() - startPosition.getPosX();
                    double offsetY = endPosition.getPosY() - startPosition.getPosY();
                    oneWorkerMoving.setByX(offsetX * CELLS_OFFSET);
                    oneWorkerMoving.setByY(offsetY * CELLS_OFFSET);
                });

                if (godCard != null) {
                    textBox.setText("Display godPower");
                    FadeTransition godAppearing = new FadeTransition(Duration.seconds(2), fullImageForThisGod.get(godCard.getGod()));
                    fullImageForThisGod.get(godCard.getGod()).setVisible(true);
                    fullImageForThisGod.get(godCard.getGod()).toFront();
                    godAppearing.setFromValue(1);
                    godAppearing.setToValue(0);

                    SequentialTransition sequentialTransition = new SequentialTransition(godAppearing, oneWorkerMoving);
                    sequentialTransition.play();

                } else
                    oneWorkerMoving.play();
            }
            manager.setBusy(false);
        });
    }

    public void displayBuild(CellView buildPosition, CardView godCard) {
        Platform.runLater(() -> {
            textBox.setText("Display build");
            FadeTransition buildingAppearing = new FadeTransition(Duration.seconds(2));
            buildingAppearing.setFromValue(0);
            buildingAppearing.setToValue(1);

            ImageView newBuilding;
            if (!buildPosition.isDomed())
                newBuilding = new ImageView("assets/buildings/build_" + buildPosition.getBuildLevel() + ".png");
            else
                newBuilding = new ImageView("assets/buildings/build_dome.png");

            gridPane.add(newBuilding, buildPosition.getPosX(), buildPosition.getPosY());
            GridPane.setHalignment(newBuilding, HPos.CENTER);
            buildingAppearing.setNode(newBuilding);
            newBuilding.toFront();

            if (buildPosition.hasWorker())
                workersForThisColor.get(buildPosition.getWorker().getColor()).get(buildPosition.getWorker().getNum() - 1).toFront();
                //esempio Zeus: se worker costruisce sotto di sé non viene asfaltato ;)

            if (godCard != null) {
                textBox.setText("Display godPower");
                FadeTransition godAppearing = new FadeTransition(Duration.seconds(2), fullImageForThisGod.get(godCard.getGod()));
                fullImageForThisGod.get(godCard.getGod()).setVisible(true);
                fullImageForThisGod.get(godCard.getGod()).toFront();
                godAppearing.setFromValue(1);
                godAppearing.setToValue(0);

                SequentialTransition sequentialTransition = new SequentialTransition(godAppearing, buildingAppearing);
                sequentialTransition.play();
            } else
                buildingAppearing.play();
            manager.setBusy(false);
        });
    }

    public void chooseYesNo(String query) {
        Platform.runLater(() -> {
            textBox.setText(query);
            yesButton.setVisible(true);
            noButton.setVisible(true);

            yesButton.setOnMouseClicked(t -> {
                sendYesNoChoice(true);
            });

            noButton.setOnMouseClicked(t -> {
                sendYesNoChoice(false);
            });
            manager.setBusy(false);
        });
    }

    public void sendYesNoChoice(boolean answer) {
        Platform.runLater(() -> {
            yesButton.setVisible(false);
            noButton.setVisible(false);
        });
        manager.putObject(answer);
    }

    //TO DO

    public void notifyDisconnection() {
        Platform.runLater(() -> {
            textBox.setText("Disconnected!");
            manager.setBusy(false);
        });
    }

    public void notifyGameOver() {
        Platform.runLater(() -> {
            textBox.setText("Game over!");
            manager.setBusy(false);
        });
    }

    public void notifyLoss() {
        Platform.runLater(() -> {
            textBox.setText("You lose!");
            manager.setBusy(false);
        });
    }

    public void notifyWin() {
        Platform.runLater(() -> {
            textBox.setText("You win!");
            manager.setBusy(false);
        });
    }

    public void displayMessage(String query) {
        Platform.runLater(() -> {
            textBox.setText(query);
            manager.setBusy(false);
        });
    }
}