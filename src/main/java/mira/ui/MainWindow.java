package mira.ui;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import mira.Mira;

/**
 * Controls the main chat window and connects it to Mira's command engine.
 */
public class MainWindow extends AnchorPane {
    private static final Duration EXIT_DELAY = Duration.seconds(1.0);

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox dialogContainer;

    @FXML
    private TextField userInput;

    @FXML
    private Button sendButton;

    private Mira mira;

    /**
     * Keeps the newest dialog visible whenever the conversation grows.
     */
    @FXML
    public void initialize() {
        dialogContainer.heightProperty().addListener((observable, oldHeight, newHeight) ->
                scrollPane.setVvalue(1.0));
    }

    /**
     * Supplies the command engine after the FXML controller is loaded.
     *
     * @param mira Mira instance used to process user input.
     */
    public void setMira(Mira mira) {
        this.mira = mira;
        dialogContainer.getChildren().add(DialogBox.getMiraDialog(mira.getWelcomeMessage()));
        userInput.requestFocus();
    }

    /**
     * Sends the current text to Mira and appends both sides of the conversation.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText().trim();
        if (input.isEmpty()) {
            userInput.requestFocus();
            return;
        }

        String response = mira.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input),
                DialogBox.getMiraDialog(response));
        userInput.clear();

        if (input.equalsIgnoreCase("bye")) {
            userInput.setPromptText("Conversation ended");
            userInput.setDisable(true);
            sendButton.setDisable(true);
            PauseTransition exitDelay = new PauseTransition(EXIT_DELAY);
            exitDelay.setOnFinished(event -> Platform.exit());
            exitDelay.play();
        } else {
            userInput.requestFocus();
        }
    }
}
