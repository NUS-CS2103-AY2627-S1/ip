package avon.gui;

import avon.Avon;
import avon.command.CommandType;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Controls Avon's primary chat window.
 */
public class MainWindow extends AnchorPane {
    private static final int EXIT_DELAY_MILLISECONDS = 500;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox dialogContainer;

    @FXML
    private TextField userInput;

    @FXML
    private HBox inputBar;

    private Avon avon;

    /**
     * Keeps the latest dialog visible and displays Avon's greeting.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
        addDialogs(DialogBox.getAvonDialog(
                "Hark! I am Avon. How may my hand or wit now serve thy need?"));
    }

    /**
     * Supplies the application logic used to answer user commands.
     *
     * @param avon the Avon command engine.
     */
    public void setAvon(Avon avon) {
        this.avon = avon;
    }

    /**
     * Sends the entered command to Avon and appends both sides of the exchange.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String consoleResponse = avon.getResponse(input);
        String response = GuiResponseFormatter.formatAvonResponse(consoleResponse);
        addDialogs(
                DialogBox.getUserDialog(input),
                DialogBox.getAvonDialog(response,
                        GuiResponseFormatter.isErrorResponse(consoleResponse)));
        userInput.clear();

        if (input.strip().equals(CommandType.BYE.getKeyword())) {
            scheduleExit();
        }
    }

    /**
     * Disables command entry and closes Avon after its farewell has been displayed.
     */
    private void scheduleExit() {
        inputBar.setDisable(true);
        PauseTransition exitDelay = new PauseTransition(Duration.millis(EXIT_DELAY_MILLISECONDS));
        exitDelay.setOnFinished(event -> Platform.exit());
        exitDelay.play();
    }

    /**
     * Appends any number of dialogs to the conversation in their supplied order.
     *
     * @param dialogBoxes the dialogs to append.
     */
    private void addDialogs(DialogBox... dialogBoxes) {
        dialogContainer.getChildren().addAll(dialogBoxes);
    }
}
