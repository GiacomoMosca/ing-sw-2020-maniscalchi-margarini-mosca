package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.view.CardView;
import it.polimi.ingsw.view.CellView;
import it.polimi.ingsw.view.GameView;
import it.polimi.ingsw.view.PlayerView;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

//TO DO: rimozione definitiva di worker dalla board
//vedere come funziona onFinished su animazione...
public class GameBoardController {

    private final double offset=96.4;

    private AnchorPane anchorPane;
    private GridPane gridPane;
    private Stage boardStage;
    private Scene boardScene;

    private GameView gameView;
    private CellView clickedCell = null;
    private ArrayList<PlayerView> players;
    private ImageView highlight;

    private HashMap<String, ArrayList<ImageView>> hashMapForThisColor;
    private ArrayList<ImageView> redWorkers;
    private ArrayList<ImageView> greenWorkers;
    private ArrayList<ImageView> blueWorkers;

    private HashMap<Integer, Image> levelForACell;
    private HashMap<CellView, ImageView> highlightForThisCell;
    private ArrayList<ImageView> activeHighlights;

    GameBoardController(GameView gameView){
        this.gameView=gameView;
        players=new ArrayList(gameView.getPlayers());
        levelForACell=new HashMap<>();
        hashMapForThisColor=new HashMap<>();
        highlightForThisCell=new HashMap<>();
        redWorkers=new ArrayList<>();
        greenWorkers=new ArrayList<>();
        blueWorkers=new ArrayList<>();
        activeHighlights=new ArrayList<>();
    }

    Stage getBoardStage(){
        return this.boardStage;
    }

    Scene getBoardScene(){
        return this.boardScene;
    }

    void initialize() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/gameBoard.fxml"));
        anchorPane=(AnchorPane)loader.load();
        gridPane=(GridPane) anchorPane.getChildren().get(1);
        boardStage=new Stage();
        boardStage.setTitle("SANTORINI");
        boardScene=new Scene(anchorPane, 1280, 720);

        initWorkers();
        initBuildings();
    }

    public void initWorkers(){
        redWorkers.add(new ImageView("assets/workers/workerRed.png"));
        redWorkers.add(new ImageView("assets/workers/workerRed.png"));
        hashMapForThisColor.put("red", redWorkers);

        greenWorkers.add(new ImageView("assets/workers/workerGreen.png"));
        greenWorkers.add(new ImageView("assets/workers/workerGreen.png"));
        hashMapForThisColor.put("green", greenWorkers);

        if (gameView.getPlayers().size()==3){
            blueWorkers.add(new ImageView("assets/workers/workerBlue.png"));
            blueWorkers.add(new ImageView("assets/workers/workerBlue.png"));
            hashMapForThisColor.put("blue", blueWorkers);
        }
    }

    //OCCHIO: alla chiamata di initBuildings devo già avere una gameview!
    public void initBuildings(){
        Image level1=new Image("assets/buildings/build1.png");
        Image level2=new Image("assets/buildings/build2.png");
        Image level3=new Image("assets/buildings/build3.png");

        levelForACell.put(1, level1);
        levelForACell.put(2, level2);
        levelForACell.put(3, level3);

        for (CellView cell : gameView.getAllCells()){
            highlight=new ImageView("assets/buildings/highlight.png");
            gridPane.add(highlight, cell.getPosX(), cell.getPosY());
            highlightForThisCell.put(cell, highlight);
            gridPane.setHalignment(highlight, HPos.CENTER);
            highlight.setVisible(false);
        }
    }

    public void placeWorker(CellView position){
        String workerColor=position.getWorker().getColor();
        int workerNumber=position.getWorker().getNum()-1;
        gridPane.add(hashMapForThisColor.get(workerColor).get(workerNumber), position.getPosX(), position.getPosY());
        GridPane.setHalignment(hashMapForThisColor.get(workerColor).get(workerNumber), HPos.CENTER);
        hashMapForThisColor.get(workerColor).get(workerNumber).setVisible(true);
    }

    //TO DO: quando due worker vengono mossi in una sola volta?? esiste parallel transition (Apollo, Minotaur), oppure
    //creare entrambe le transizioni e farle partire fuori
    protected void moveWorker(HashMap<CellView, CellView> moves, CardView godCard){
        moves.forEach((startPosition, endPosition) -> {
            TranslateTransition transition = new TranslateTransition(Duration.seconds(1));
            ImageView movingWorker = hashMapForThisColor.get(startPosition.getWorker().getColor()).get(startPosition.getWorker().getNum()-1);
            int offsetX = endPosition.getPosX() - startPosition.getPosX();
            int offsetY = endPosition.getPosY() - startPosition.getPosY();
            movingWorker.toFront();
            transition.setToX(movingWorker.getX()+(offsetX*offset));
            transition.setToY(movingWorker.getY()+(offsetY*offset));
            transition.setNode(movingWorker);
            transition.play();
        });

        for (ImageView activeHighlight : activeHighlights){
            activeHighlight.setVisible(false);
            activeHighlights.remove(activeHighlight);
        }
    }

    protected void buildLevel(CellView buildPosition, CardView godCard){

        FadeTransition transition=new FadeTransition(Duration.seconds(1));
        ImageView newBuilding=new ImageView(levelForACell.get(buildPosition.getBuildLevel()));

        gridPane.add(newBuilding, buildPosition.getPosX(), buildPosition.getPosY());

        newBuilding.setVisible(true);
        GridPane.setHalignment(newBuilding, HPos.CENTER);
        gridPane.add(newBuilding, buildPosition.getPosX(), buildPosition.getPosY());
        newBuilding.toFront();                              //occhio a questo quando apollo (?) si costruisce sotto
                                                            //if godCard è Apollo mettere visibile il worker

        transition.setNode(newBuilding);
        transition.setFromValue(0);                         //opacity
        transition.setToValue(1);
        transition.play();

        for (ImageView activeHighlight : activeHighlights){
            activeHighlight.setVisible(false);
            activeHighlights.remove(activeHighlight);
        }
    }

    protected Integer prepareChoice(ArrayList<CellView> positions){
        clickedCell=null;

        for (CellView position : positions) {
            activeHighlights.add(highlightForThisCell.get(position));
            highlightForThisCell.get(position).setVisible(true);
            highlightForThisCell.get(position).toFront();
            highlightForThisCell.get(position).setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent paramT){
                    clickedCell=position;
                }
            });
        }
        return (positions.indexOf(clickedCell));
    }


    /**
     * /////COSE MAGARI UTILI PER TUTTI
     *
     * //per modificare dal codice i valori dei margini di un elemento in una cella della tabella
     * GridPane.setMargin(level, new Insets(0,2,4,0));
     *
     * //trovare un nodo nella tabella
     * Node getNode(GridPane gridPane, int col, int row) {
     *     for (Node node : gridPane.getChildren()) {
     *         if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) {
     *             return node;
     *         }
     *     }
     *     return null;
     * }
     *
     * //rilevare cella cliccata
     * private void chooseCell(MouseEvent event) {
     *         Node source = (Node)event.getSource();
     *         Integer colIndex = GridPane.getColumnIndex(source);
     *         Integer rowIndex = GridPane.getRowIndex(source);
     *         System.out.printf("Mouse clicked cell in [%d, %d]%n", colIndex, rowIndex);
     *     }
     *
     * //cose che ho cancellato ma che non sono sicura di voler abbandonare per sempre
     *     protected void prepareBuildPhase(ArrayList<CellView> positions){
     *         for (CellView position : positions){
     *             //vado a pescare l'highligh di ogni cella in cui è possibile costruire e lo rendo visibile
     *             highlightForThisCell.get(position).setVisible(true);
     *             highlightForThisCell.get(position).toFront();
     *             highlightForThisCell.get(position).setOnMouseClicked(new EventHandler<MouseEvent>() {
     *                 @Override
     *                 public void handle(MouseEvent event) {
     *                     Node source = (Node) event.getSource();
     *                     Integer colIndex = GridPane.getColumnIndex(source);
     *                     Integer rowIndex = GridPane.getRowIndex(source);
     *                     System.out.printf("Mouse clicked cell [%d, %d]%n", colIndex, rowIndex);
     *                     int previousLevel = position.getBuildLevel();
     *                     //buildLevel(positions, colIndex, rowIndex, previousLevel); non chiamata da qui
     *                 }
     *             });
     *         }
     *         return;
     *     }
     *
     *
     * //metodo laborioso per spostare i worker
     *             switch (offsetX){
     *                 case 0:
     *                     //don't change column
     *                     transition.setToX(movingWorker.getX());
     *                     break;
     *                 case 1:
     *                     transition.setToX(movingWorker.getX()+offset);
     *                     break;
     *                 case -1:
     *                     transition.setToX(movingWorker.getX()-offset);
     *                     break;
     *                 case 2:
     *                     transition.setToX(movingWorker.getX()+(2*offset));
     *                     break;
     *                 case -2:
     *                     transition.setToX(movingWorker.getX()-(2*offset));
     *                     break;
     *             }
     *             switch (offsetY){
     *                 case 0:
     *                     //don't change row
     *                     transition.setToY(movingWorker.getY());
     *                     break;
     *                 case 1:
     *                     transition.setToY(movingWorker.getY()+offset);
     *                     break;
     *                 case -1:
     *                     transition.setToY(movingWorker.getY()-offset);
     *                     break;
     *                 case 2:
     *                     transition.setToY(movingWorker.getY()+(2*offset));
     *                     break;
     *                 case -2:
     *                     transition.setToY(movingWorker.getY()-(2*offset));
     *                     break;
     *             }
     *
     *
     * // primo metodo di visualizzazione board
     *    void updateBoard(GameView board){
     *         for (CellView cell : board.getAllCells()){
     *             if (cell.getBuildLevel()!=0){
     *                 for(int i=1; i<=cell.getBuildLevel(); i++){
     *                     ImageView level=new ImageView(levelForACell.get(i));
     *                     level.setVisible(true);
     *                     GridPane.setHalignment(level, HPos.CENTER);
     *                     gridPane.add(level, cell.getPosX(), cell.getPosY());
     *                 }
     *             }
     *             if (cell.hasWorker()){
     *                 GridPane.setRowIndex(imageViewForThisColor.get(cell.getWorkerColor()), cell.getPosY());
     *                 GridPane.setColumnIndex(imageViewForThisColor.get(cell.getWorkerColor()), cell.getPosX());
     *                 GridPane.setHalignment(imageViewForThisColor.get(cell.getWorkerColor()), HPos.CENTER);
     *                 imageViewForThisColor.get(cell.getWorkerColor()).setVisible(true);
     *                 imageViewForThisColor.get(cell.getWorkerColor()).toFront();
     *             }
     *             if (cell.isDomed()){
     *                 ImageView dome=new ImageView("assets/buildings/dome.png");
     *                 gridPane.add(dome, cell.getPosX(), cell.getPosY());
     *                 GridPane.setHalignment(dome, HPos.CENTER);
     *                 dome.setVisible(true);
     *             }
     *         }
     *     }
     **/


}
