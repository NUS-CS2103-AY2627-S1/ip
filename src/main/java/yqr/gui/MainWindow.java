package yqr.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import yqr.CommandResult;
import yqr.Duke;

/**
 * Controls the main chat window and forwards user input to the chatbot.
 */
public class MainWindow {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Duke duke;

    /**
     * Keeps the newest message visible when the dialog container grows.
     */
    @FXML
    private void initialize() {
        dialogContainer.heightProperty().addListener(observable -> scrollPane.setVvalue(1.0));
    }

    /**
     * Connects this controller to the chatbot and displays its greeting.
     *
     * @param duke chatbot used to process commands.
     */
    public void setDuke(Duke duke) {
        this.duke = duke;
        DialogBox welcomeDialog = duke.hasLoadingError()
                ? DialogBox.getErrorDialog(duke.getWelcomeMessage())
                : DialogBox.getYqrDialog(duke.getWelcomeMessage());
        dialogContainer.getChildren().add(welcomeDialog);
        userInput.requestFocus();
    }

    /**
     * Sends non-empty text to the chatbot and displays both sides of the exchange.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText().trim();
        if (input.isEmpty() || duke == null || duke.hasExited()) {
            return;
        }

        CommandResult result = duke.getCommandResult(input);
        DialogBox responseDialog = result.isError()
                ? DialogBox.getErrorDialog(result.text())
                : DialogBox.getYqrDialog(result.text());
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input), responseDialog);
        userInput.clear();

        if (duke.hasExited()) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
        }
    }
}
