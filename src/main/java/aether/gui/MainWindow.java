package aether.gui;

import aether.Aether;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/** Controls the main chat window and passes commands to Aether's application logic. */
public class MainWindow {
    private static final Duration EXIT_DELAY = Duration.millis(700);

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox dialogContainer;

    @FXML
    private TextField userInput;

    @FXML
    private Button sendButton;

    private Aether aether;

    /** Keeps the latest chat message visible as the conversation grows. */
    @FXML
    public void initialize() {
        dialogContainer.heightProperty().addListener(observable -> scrollPane.setVvalue(1.0));
    }

    /**
     * Injects the chatbot and displays its greeting after the FXML controls are ready.
     *
     * @param aether the chatbot that processes commands
     */
    public void setAether(Aether aether) {
        this.aether = aether;
        dialogContainer.getChildren().add(DialogBox.getAetherDialog(aether.getWelcomeMessage()));
        userInput.requestFocus();
    }

    /** Sends the text field contents to Aether and adds both sides of the exchange to the chat. */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String displayedInput = input.isBlank() ? "(empty command)" : input;
        Aether.CommandResult result = aether.getCommandResult(input);

        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(displayedInput),
                result.isError()
                        ? DialogBox.getErrorDialog(result.getMessage())
                        : DialogBox.getAetherDialog(result.getMessage()));
        userInput.clear();

        if (input.trim().equals("bye")) {
            closeAfterFarewell();
        } else {
            userInput.requestFocus();
        }
    }

    /** Disables further input and briefly leaves the farewell visible before closing the application. */
    private void closeAfterFarewell() {
        userInput.setDisable(true);
        sendButton.setDisable(true);
        PauseTransition pause = new PauseTransition(EXIT_DELAY);
        pause.setOnFinished(event -> Platform.exit());
        pause.play();
    }
}
