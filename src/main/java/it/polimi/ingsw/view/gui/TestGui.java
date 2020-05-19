package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.view.*;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;


/**
 * class to start GUI without playing an entire game
 *
 */
public class TestGui extends Application {

    GameView preparedBoard;

    @FXML
    private TextField serverIP;

    public static void main(String[] args) {
        TestGui.launch();
    }

    public void start(Stage stage) throws IOException {
        /*
        ArrayList<PlayerView> players=new ArrayList<>();
        PlayerView player1=new PlayerView("player1", "red", "card1", false);
        PlayerView player2=new PlayerView("player2", "green", "card2", false);
        PlayerView player3=new PlayerView("player3", "blue", "card3", false);

        ArrayList<CardView> cardViews=new ArrayList<>();
        CardView card1=new CardView("god1", "title1", "description1");
        CardView card2=new CardView("god2", "title2", "description2");
        CardView card3=new CardView("god3", "title3", "description3");

        players.add(player1);
        players.add(player2);
        players.add(player3);

        CellView[][] board = new CellView[5][5];
        board[0][0] = new CellView(0, 0, 1, false, null);
        board[0][1] = new CellView(0, 1, 1, false,"red1");
        board[0][2] = new CellView(0, 2, 1, false, null);
        board[0][3] = new CellView(0, 3, 1, false, "green1");
        board[0][4] = new CellView(0, 4, 1, false, null);
        board[1][0] = new CellView(1, 0, 1, false, "blue1");
        board[1][1] = new CellView(1, 1, 1, false, null);
        board[1][2] = new CellView(1, 2, 1, false, null);
        board[1][3] = new CellView(1, 3, 1, false, null);
        board[1][4] = new CellView(1, 4, 1, true, null);
        board[2][0] = new CellView(2, 0, 1, false, null);
        board[2][1] = new CellView(2, 1, 2, false, "blue2");
        board[2][2] = new CellView(2, 2, 2, true, null);
        board[2][3] = new CellView(2, 3, 1, false, null);
        board[2][4] = new CellView(2, 4, 1, false, "red2");
        board[3][0] = new CellView(3, 0, 1, false, null);
        board[3][1] = new CellView(3, 1, 1, false, "green2");
        board[3][2] = new CellView(3, 2, 1, false, null);
        board[3][3] = new CellView(3, 3, 3, false, null);
        board[3][4] = new CellView(3, 4, 1, false, null);
        board[4][0] = new CellView(4, 0, 1, false, null);
        board[4][1] = new CellView(4, 1, 1, false, null);
        board[4][2] = new CellView(4, 2, 1, false, null);
        board[4][3] = new CellView(4, 3, 1, false, null);
        board[4][4] = new CellView(4, 4, 1, true, null);


        preparedBoard=new GameView("test",3,players, board, cardViews);
        */

        GUI gui=new GUI();
        //gui.initBoard(preparedBoard);
        //gui.displayBoard(preparedBoard, "desc", card1);

    }
}
