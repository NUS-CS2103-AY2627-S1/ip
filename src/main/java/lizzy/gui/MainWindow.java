package lizzy.gui;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import lizzy.Lizzy;

/**
 * Handles user interaction in Lizzy's main window.
 */
public class MainWindow {
    private static final Duration EXIT_DELAY = Duration.seconds(2.0);

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;
    /**
     * Generates responses for commands entered in the window.
     */
    private Lizzy lizzy;

    /**
     * Connects the window to a chatbot session and displays its greeting.
     *
     * @param lizzy the chatbot that will process user commands
     */
    public void setLizzy(Lizzy lizzy) {
        this.lizzy = lizzy;
        dialogContainer.getChildren().add(
                DialogBox.getLizzyDialog("Hello! I'm Lizzy.\nWhat brings you here today?\n"
                        + "Type help whenever you would like a quick command guide."));
        lizzy.getStartupError().ifPresent(error ->
                dialogContainer.getChildren().add(DialogBox.getErrorDialog(error)));
        scrollToLatestMessage();
    }

    /**
     * Places keyboard focus in the command field.
     */
    public void focusInput() {
        userInput.requestFocus();
    }

    /**
     * Sends the current text to Lizzy and displays both sides of the exchange.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText().strip();
        if (input.isEmpty()) {
            return;
        }

        dialogContainer.getChildren().add(DialogBox.getUserDialog(input));
        Lizzy.Response response = lizzy.getResponseWithStatus(input);
        DialogBox responseDialog = response.isError()
                ? DialogBox.getErrorDialog(response.text())
                : DialogBox.getLizzyDialog(response.text());
        dialogContainer.getChildren().add(responseDialog);
        scrollToLatestMessage();
        userInput.clear();
        if (response.isExit()) {
            closeAfterFarewell();
            return;
        }
        userInput.requestFocus();
    }

    /**
     * Prevents further input and closes the application after the farewell can be read.
     */
    private void closeAfterFarewell() {
        userInput.setDisable(true);
        sendButton.setDisable(true);
        PauseTransition exitDelay = new PauseTransition(EXIT_DELAY);
        exitDelay.setOnFinished(event -> Platform.exit());
        exitDelay.play();
    }

    /**
     * Reveals the latest exchange after JavaFX has laid out the new dialog boxes.
     */
    private void scrollToLatestMessage() {
        Platform.runLater(() -> scrollPane.setVvalue(1.0));
    }
}
