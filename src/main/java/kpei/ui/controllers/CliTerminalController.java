package kpei.ui.controllers;

import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 * Controller for the CLI Terminal component simulating a command-line environment.
 */
public class CliTerminalController {

    @FXML
    private TextArea terminalOutput;

    @FXML
    private TextField commandInput;

    private Consumer<String> commandConsumer;

    /**
     * Initializes the terminal component by configuring non-editable text area and auto-scrolling.
     */
    @FXML
    public void initialize() {
        terminalOutput.setEditable(false);
        terminalOutput.setWrapText(true);
        terminalOutput.setFocusTraversable(false);
    }

    /**
     * Sets the consumer invoked when the user submits a command.
     *
     * @param consumer Consumer taking the trimmed command string.
     */
    public void setCommandConsumer(Consumer<String> consumer) {
        this.commandConsumer = consumer;
    }

    /**
     * Handles the user submitting a command via Enter key press in the input field.
     */
    @FXML
    private void handleUserInput() {
        String input = commandInput.getText();
        if (input == null || input.isBlank()) {
            return;
        }

        String trimmed = input.trim();
        commandInput.clear();

        // Print the prompt symbol and user command in the terminal
        appendOutput("> " + trimmed + "\n");

        if (commandConsumer != null) {
            commandConsumer.accept(trimmed);
        }
    }

    /**
     * Appends text to the terminal output area, ensuring updates occur on the JavaFX Application Thread.
     *
     * @param text Text to append to the terminal.
     */
    public void appendOutput(String text) {
        if (Platform.isFxApplicationThread()) {
            terminalOutput.appendText(text);
        } else {
            Platform.runLater(() -> terminalOutput.appendText(text));
        }
    }
}
