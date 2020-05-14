package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.view.CellView;
import it.polimi.ingsw.view.GameView;
import it.polimi.ingsw.view.PlayerView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GameBoardController {

    ImageView workerRed1, workerRed2;
    ImageView workerGreen1, workerGreen2;
    ImageView workerBlue1, workerBlue2;
    CellView workerRed1StartingPosition, WorkerRed2StartingPosition;        //to make the movement from -> to. need from view
    CellView workerGreen1StartingPosition, WorkerGreen2StartingPosition;
    CellView workerBlue1StartingPosition, getWorkerBlue2StartingPosition;


    AnchorPane anchorPane;
    GridPane gridPane;

    private Stage boardStage;
    private Scene boardScene;

    private GameView board;
    private ArrayList<String> players;
    private ArrayList<ImageView> workers;

    private Map<String, ImageView> imageViewForThisColor;
    private Map<Integer, Image> levelForACell;             //tengo un'immagine e ne faccio una copia quando serve


    public GameBoardController(GameView board){
        this.board=board;
        players=new ArrayList<>();
        workers=new ArrayList<>();
        imageViewForThisColor=new HashMap<>();
        levelForACell=new HashMap<>();

        for (PlayerView player : board.getPlayers()) {
            this.players.add(player.getColor());
        }
    }

    public void initialize() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/gameBoard.fxml"));
        anchorPane=(AnchorPane)loader.load();
        //VBox vBox=loader.load();
        //StackPane stackPane=loader.load();
        gridPane=(GridPane) anchorPane.getChildren().get(1);
        //gridPane=(GridPane) vBox.getChildren().get(1);
        //gridPane=(GridPane) stackPane.getChildren().get(1);

        boardStage=new Stage();
        boardStage.setTitle("SANTORINI");
        boardScene=new Scene(anchorPane, 960, 540);
        //boardScene=new Scene(vBox, 960, 540);
        //boardScene=new Scene(stackPane, 960, 540);

        /*boardScene.widthProperty().addListener(new ChangeListener<Number>() {
            @Override public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
                System.out.println("Width: " + newSceneWidth);
                boardStage.setResizable(true);
                stackPane.autosize();
                }
        });
        boardScene.heightProperty().addListener(new ChangeListener<Number>() {
            @Override public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
                System.out.println("Height: " + newSceneHeight);
                boardStage.setWidth((double)newSceneHeight);
                stackPane.autosize();
            }
        });*/

        //creo i due worker per ogni giocatore
        workerRed1=new ImageView("assets/workers/workerRed.png");
        workerRed2=new ImageView("assets/workers/workerRed.png");
        workerGreen1=new ImageView("assets/workers/workerGreen.png");
        workerGreen2=new ImageView("assets/workers/workerGreen.png");
        workerRed1.setFitWidth(66);
        workerRed1.setFitHeight(66);
        workerGreen1.setFitWidth(66);
        workerGreen1.setFitHeight(66);
        workerRed2.setFitWidth(66);
        workerRed2.setFitHeight(66);
        workerGreen2.setFitWidth(66);
        workerGreen2.setFitHeight(66);
        gridPane.add(workerRed1, 0, 0);
        gridPane.add(workerRed2, 0, 0);
        gridPane.add(workerGreen1, 0,0);
        gridPane.add(workerGreen2, 0, 0);
        workerRed1.setVisible(false);
        workerRed2.setVisible(false);
        workerGreen1.setVisible(false);
        workerGreen2.setVisible(false);


        workers.add(workerRed1);
        workers.add(workerRed2);
        workers.add(workerGreen1);
        workers.add(workerGreen2);

        imageViewForThisColor.put("red1", workerRed1);
        imageViewForThisColor.put("red2", workerRed2);
        imageViewForThisColor.put("green1", workerGreen1);
        imageViewForThisColor.put("green2", workerGreen2);


        if (players.size()==3){
            workerBlue1=new ImageView("assets/workers/workerBlue.png");
            workerBlue2=new ImageView("assets/workers/workerBlue.png");
            workerBlue1.setFitWidth(66);
            workerBlue1.setFitHeight(66);
            workerBlue2.setFitWidth(66);
            workerBlue2.setFitHeight(66);
            gridPane.add(workerBlue1, 0,0);
            gridPane.add(workerBlue2, 0,0);
            workerBlue1.setVisible(false);
            workerBlue2.setVisible(false);

            workers.add(workerBlue1);
            workers.add(workerBlue2);

            imageViewForThisColor.put("blue1", workerBlue1);
            imageViewForThisColor.put("blue2", workerBlue2);
        }

        Image level1=new Image("assets/buildings/build1.png");
        Image level2=new Image("assets/buildings/build2.png");
        Image level3=new Image("assets/buildings/build3.png");

        levelForACell.put(1,level1);
        levelForACell.put(2, level2);
        levelForACell.put(3, level3);

    }

    //sistemare layout interno alle singole celle: codice o css?
    //modificare in modo da mostrare solo le modifiche rispetto allo stato precedente
    //aggiungere bene riconoscimento worker
    //idea: mettere un nodo in ogni cella e gestire da lì le immagini in ognuna
    public void updateBoard(GameView board){

        for (CellView cell : board.getAllCells()){
            if (cell.getBuildLevel()!=0){
                for(int i=1; i<=cell.getBuildLevel(); i++){
                    ImageView level=new ImageView(levelForACell.get(i));
                    level.setFitWidth(66);
                    level.setFitHeight(66);
                    level.setVisible(true);
                    GridPane.setHalignment(level, HPos.CENTER);
                    GridPane.setMargin(level, new Insets(0,2,4,0));
                    gridPane.add(level, cell.getPosX(), cell.getPosY());
                }
            }
            if (cell.hasWorker()){
                GridPane.setRowIndex(imageViewForThisColor.get(cell.getWorkerColor()), cell.getPosY());
                GridPane.setColumnIndex(imageViewForThisColor.get(cell.getWorkerColor()), cell.getPosX());
                GridPane.setHalignment(imageViewForThisColor.get(cell.getWorkerColor()), HPos.CENTER);
                imageViewForThisColor.get(cell.getWorkerColor()).setVisible(true);
                imageViewForThisColor.get(cell.getWorkerColor()).toFront();

            }
            if (cell.isDomed()){
                ImageView dome=new ImageView("assets/buildings/dome.png");
                dome.setFitWidth(66);
                dome.setFitHeight(66);
                gridPane.add(dome, cell.getPosX(), cell.getPosY());
                GridPane.setHalignment(dome, HPos.CENTER);
                dome.setVisible(true);
            }
        }
    }

    //access is package-private
    Stage getBoardStage(){
        return this.boardStage;
    }

    Scene getBoardScene(){
        return this.boardScene;
    }


    /**
     *
     * //Method for finding node in gridpane:
     * Node getNode(GridPane gridPane, int col, int row) {
     *     for (Node node : gridPane.getChildren()) {
     *         if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) {
     *             return node;
     *         }
     *     }
     *     return null;
     * }
     *
     */

}
