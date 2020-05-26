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
    private Text centerText, chooseYesNoText, topBannerText, confirmText;
    @FXML
    private ImageView yesButton, noButton, confirmButton;

    private ArrayList<String> possibleCards;
    private ArrayList<String> choices;
    private int choicesNum;

    public void initialize(GUIManager manager) {
        this.manager = manager;
        yesButton.setOnMouseClicked(t -> {
            clickYesNo(true);
        });
        noButton.setOnMouseClicked(t -> {
            clickYesNo(false);
        });
        confirmButton.setOnMouseClicked(t -> {
            clickConfirm();
        });
    }

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

    public void clickYesNo(boolean value) {
        Platform.runLater(() -> {
            setDefaultValues();
            manager.setBusy(false);
        });
        manager.putObject(value);
    }

    public void clickConfirm() {
        Platform.runLater(() -> {
            setDefaultValues();
            scrollPaneBox.getChildren().clear();
            centerCardsBox.getChildren().clear();
            manager.setBusy(false);
        });
        ArrayList<Integer> ints = new ArrayList<Integer>();
        for (String card : choices) {
            ints.add(possibleCards.indexOf(card));
        }
        manager.putObject(ints);
    }

    public void chooseYesNo(String query) {
        Platform.runLater(() -> {
            cover.setVisible(false);
            centerTextBox.setVisible(false);
            chooseYesNoBox.setVisible(true);
            chooseYesNoText.setText(query);
        });
    }

    public void displayGameInfo() {
        Platform.runLater(() -> {
            setDefaultValues();
            manager.setBusy(false);
        });
    }

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

    public class GodImageView extends ImageView {

        private final String card;
        private boolean clicked = false;

        public GodImageView(String s) {
            super("assets/gods/godCards/card_" + s + ".png");
            card = s;
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
