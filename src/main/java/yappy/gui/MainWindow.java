package yappy.gui;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import yappy.Yappy;

/**
 * Controls Yappy's main conversation window.
 */
public class MainWindow extends AnchorPane {
    /** Brief delay that lets the user read Yappy's farewell before the GUI closes. */
    private static final Duration EXIT_DELAY = Duration.millis(800);

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox dialogContainer;

    @FXML
    private TextField userInput;

    @FXML
    private Button sendButton;

    private final Image userImage = new Image(
            this.getClass().getResourceAsStream("/images/DaUser.png"));
    private final Image yappyImage = new Image(
            this.getClass().getResourceAsStream("/images/DaYappy.png"));

    private Yappy yappy;

    /**
     * Keeps the latest dialog visible as the conversation grows.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Supplies the chatbot that handles commands and adds its welcome dialog.
     */
    public void setYappy(Yappy yappy) {
        this.yappy = yappy;
        dialogContainer.getChildren().add(
                DialogBox.getYappyDialog(yappy.getWelcomeMessage(), yappyImage, yappy.getCommandType()));
    }

    /**
     * Appends the user's input and Yappy's response, then clears the input field.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String response = yappy.getResponse(input);
        String commandType = yappy.getCommandType();

        if (input.isBlank()) {
            dialogContainer.getChildren().add(
                    DialogBox.getYappyDialog(response, yappyImage, commandType));
        } else {
            dialogContainer.getChildren().addAll(
                    DialogBox.getUserDialog(input.trim(), userImage),
                    DialogBox.getYappyDialog(response, yappyImage, commandType));
        }
        userInput.clear();
        userInput.requestFocus();

        if (yappy.shouldExit()) {
            PauseTransition exitDelay = new PauseTransition(EXIT_DELAY);
            exitDelay.setOnFinished(event -> Platform.exit());
            exitDelay.play();
        }
    }
}
