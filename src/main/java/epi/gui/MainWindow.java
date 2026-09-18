package epi.gui;

import epi.CommandResult;
import epi.Epi;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

/** Controls the main Epi chat window and delegates commands to the Epi backend. */
public class MainWindow {
    @FXML
    private VBox dialogContainer;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private TextField userInput;

    private final Epi epi = new Epi("./data/epi.txt", false);

    /**
     * Initializes automatic scrolling and displays the greeting and any loading warnings.
     * Called by the FXML loader after the controls have been injected.
     */
    @FXML
    public void initialize() {
        dialogContainer.heightProperty().addListener(observable -> scrollPane.setVvalue(1.0));
        addEpiMessage("Meowdy! I'm Epi. What can I help you with today?");
        if (!epi.getLoadingWarnings().isEmpty()) {
            dialogContainer.getChildren().add(DialogBox.error(String.join("\n", epi.getLoadingWarnings())));
        }
    }

    /**
     * Processes nonblank input and displays the complete response in one message card.
     * Error responses receive distinct styling; focus returns to the cleared input field.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText().trim();
        if (input.isEmpty()) {
            return;
        }
        addUserMessage(input);
        CommandResult result = epi.processCommandResult(input);
        String response = String.join("\n", result.lines());
        dialogContainer.getChildren().add(result.error() ? DialogBox.error(response) : DialogBox.epi(response));
        userInput.clear();
        userInput.requestFocus();
    }

    /**
     * Appends a user message to the conversation.
     *
     * @param message Complete user input to display.
     */
    private void addUserMessage(String message) {
        dialogContainer.getChildren().add(DialogBox.user(message));
    }

    /**
     * Appends a normal Epi response to the conversation.
     *
     * @param message Complete response text, including any line breaks.
     */
    private void addEpiMessage(String message) {
        dialogContainer.getChildren().add(DialogBox.epi(message));
    }
}
