package it.polimi.ingsw.view.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.ArrayList;

/**
 * Handles the interaction between client and server during the phase of choosing the God Cards to use for the Game.
 * The Player can choose to randomize the playable God Cards pool, or to choose the God Cards that will be used in the Game.
 */
public class GameSetupController {

    private GUIManager manager = new GUIManager();

    @FXML
    private AnchorPane cover;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private HBox scrollPaneBox, centerCardsBox;
    @FXML
    private VBox centerTextBox, chooseYesNoBox;
    @FXML
    private StackPane bannerBox, confirmBox;
    @FXML
    private Text centerText, chooseYesNoText, topBannerText;
    @FXML
    private Text yesText, yesText_p, noText, noText_p, confirmText, confirmText_p;
    @FXML
    private ImageView yesButton, yesButton_p, noButton, noButton_p, confirmButton, confirmButton_p;

    private ArrayList<String> possibleCards;
    private ArrayList<String> choices;
    private int choicesNum;

    /**
     * @param manager the GUIManager to set the JoinGameController manager attribute to
     */
    public void initialize(GUIManager manager) {
        this.manager = manager;
        setDefaultValues();
    }

    /**
     * Prepares the scene.
     */
    public void setDefaultValues() {
        cover.setVisible(true);
        chooseYesNoBox.setVisible(false);
        scrollPane.setVisible(false);
        centerCardsBox.setVisible(false);
        bannerBox.setVisible(false);
        confirmBox.setVisible(false);
        confirmButton.setVisible(false);
        centerTextBox.setVisible(true);
    }

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
            setDefaultValues();
            yesButton_p.setVisible(false);
            yesText_p.setVisible(false);
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
            setDefaultValues();
            noButton_p.setVisible(false);
            noText_p.setVisible(false);
            manager.setBusy(false);
        });
        manager.putObject(false);
    }

    @FXML
    private void confirmPressed() {
        Platform.runLater(() -> {
            confirmButton_p.setVisible(true);
            confirmText_p.setVisible(true);
        });
    }

    @FXML
    private void confirmReleased() {
        Platform.runLater(() -> {
            setDefaultValues();
            confirmButton_p.setVisible(false);
            confirmText_p.setVisible(false);
            manager.setBusy(false);
        });
        ArrayList<Integer> ints = new ArrayList<Integer>();
        for (String card : choices) {
            ints.add(possibleCards.indexOf(card));
        }
        manager.putObject(ints);
    }

    /**
     * Allows the Player to choose between randomizing the playable God Cards pool or selecting the God Cards to use.
     *
     * @param query the "yes or no question" the Player should answer to
     */
    public void chooseYesNo(String query) {
        Platform.runLater(() -> {
            cover.setVisible(false);
            centerTextBox.setVisible(false);
            chooseYesNoBox.setVisible(true);
            chooseYesNoText.setText(query);
        });
    }

    /**
     * Allows preparing the scene for the choiche of the Cards.
     */
    public void displayGameInfo() {
        Platform.runLater(() -> {
            setDefaultValues();
            manager.setBusy(false);
        });
    }

    /**
     * Allows the Player to choose his own Card between those available to pick.
     * When he selected his Card, he will be able to click on the Confirm button.
     *
     * @param possibleCards an ArrayList containing all the God names associated to the available Cards
     * @param pickedCards   an ArrayList containing all the God names associated to the already picked Cards
     */
    public void chooseMyCard(ArrayList<String> possibleCards, ArrayList<String> pickedCards) {
        this.possibleCards = possibleCards;
        choices = new ArrayList<String>();
        choicesNum = 1;
        Platform.runLater(() -> {
            cover.setVisible(false);
            centerTextBox.setVisible(false);
            chooseYesNoBox.setVisible(false);
            bannerBox.setVisible(true);
            confirmBox.setVisible(true);
            topBannerText.setText("Choose your God Power");
            centerCardsBox.setVisible(true);
            for (String card : possibleCards) {
                GodImageView cardImage = new GodImageView(card);
                centerCardsBox.getChildren().add(cardImage);
            }
            for (String card : pickedCards) {
                ImageView cardImage = new ImageView("assets/gods/godCards/card_" + card + ".png");
                cardImage.setEffect(new ColorAdjust(0.0, -1.0, -0.5, 0.2));
                centerCardsBox.getChildren().add(cardImage);
            }
        });
    }

    /**
     * Allows the Player to choose a specified number of Cards between those available.
     * The Player can scroll through all Cards to select the 2 or 3 Cards to use for the Game.
     * When he selected the expected number of cards, he will be able to click on the Confirm button.
     *
     * @param possibleCards an ArrayList containing all the God names associated to the available Cards
     * @param num           the number of cards to choose
     */
    public void chooseAllCards(ArrayList<String> possibleCards, int num) {
        this.possibleCards = possibleCards;
        choices = new ArrayList<String>();
        choicesNum = num;
        Platform.runLater(() -> {
            cover.setVisible(false);
            centerTextBox.setVisible(false);
            chooseYesNoBox.setVisible(false);
            bannerBox.setVisible(true);
            confirmBox.setVisible(true);
            topBannerText.setText("Choose " + num + " God Powers");
            scrollPane.setVisible(true);
            scrollPane.setOnScroll(event -> {
                if (event.getDeltaX() == 0 && event.getDeltaY() != 0) {
                    scrollPane.setHvalue(scrollPane.getHvalue() - event.getDeltaY() / this.scrollPaneBox.getWidth());
                }
            });
            for (String card : possibleCards) {
                GodImageView cardImage = new GodImageView(card);
                scrollPaneBox.getChildren().add(cardImage);
            }
        });
    }

    /**
     * GodImageView class is used to represent each God Card as an ImageView with an associated boolean attribute telling if the Card was clicked or not.
     * It is possible to select a Card and then unselect it.
     * All the Cards are associated to a String (ie the God name), and all the God names associated to the selected Cards are put in an ArrayList.
     */
    public class GodImageView extends ImageView {

        private final String card;
        private boolean clicked = false;

        public GodImageView(String s) {
            super("assets/gods/godCards/card_" + s + ".png");
            card = s;
            setId("cardNotClicked");
            setOnMouseClicked(t -> {
                if (clicked) {
                    clicked = false;
                    setId("cardNotClicked");
                    choices.remove(card);
                    confirmButton.setVisible(false);
                } else {
                    if (choices.size() >= choicesNum) return;
                    clicked = true;
                    setId("cardClicked");
                    choices.add(card);
                    if (choices.size() >= choicesNum) confirmButton.setVisible(true);
                }
            });
        }
    }

}
