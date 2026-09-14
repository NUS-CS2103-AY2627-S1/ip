package toot.ui;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import toot.Toot;

/**
 * Controls Toot's main chat window.
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

    private Toot toot;

    /**
     * Keeps the newest dialog visible when the conversation grows.
     */
    @FXML
    public void initialize() {
        dialogContainer.heightProperty().addListener(observable -> scrollPane.setVvalue(1.0));
    }

    /**
     * Injects the chatbot and displays its startup message.
     *
     * @param toot Chatbot that processes commands.
     */
    public void setToot(Toot toot) {
        this.toot = toot;
        dialogContainer.getChildren().add(DialogBox.getTootDialog(toot.getStartupMessage()));
    }

    /**
     * Sends one command and appends the command and response to the conversation.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText().strip();
        if (input.isEmpty()) {
            return;
        }

        String response = toot.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input),
                DialogBox.getTootDialog(response));
        userInput.clear();

        if (input.equals("bye")) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
            PauseTransition exitDelay = new PauseTransition(Duration.seconds(1.0));
            exitDelay.setOnFinished(event -> Platform.exit());
            exitDelay.play();
        }
    }
}
