package benbot;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

/** Controls the main BenBot conversation window defined in FXML. */
public class MainWindow {
    private static final Duration EXIT_DELAY = Duration.millis(900);

    /** The scrollable conversation viewport. */
    @FXML
    private ScrollPane scrollPane;

    /** Contains the user and BenBot dialog boxes in chronological order. */
    @FXML
    private VBox dialogContainer;

    /** Accepts commands typed by the user. */
    @FXML
    private TextField userInput;

    /** Submits the current command. */
    @FXML
    private Button sendButton;

    /** The chatbot that processes commands entered in this window. */
    private BenBot benBot;

    /** Keeps the latest dialog visible as the conversation grows. */
    @FXML
    public void initialize() {
        dialogContainer.heightProperty().addListener(observable -> scrollPane.setVvalue(1.0));
    }

    /**
     * Connects this controller to the chatbot and displays the opening message.
     *
     * @param benBot the chatbot that should process user commands.
     */
    public void setBenBot(BenBot benBot) {
        this.benBot = benBot;
        dialogContainer.getChildren().add(DialogBox.getBotDialog(Ui.getWelcomeMessage()));
        String loadFailureMessage = benBot.getLoadFailureMessage();
        if (!loadFailureMessage.isEmpty()) {
            dialogContainer.getChildren().add(DialogBox.getBotDialog(loadFailureMessage));
        }
        Platform.runLater(userInput::requestFocus);
    }

    /**
     * Saves data before a manual window close and keeps the window open if saving fails.
     * A successful {@code bye} command has already saved the data, so it can close immediately.
     *
     * @param event the window-close request that can be canceled when saving fails.
     */
    void handleCloseRequest(WindowEvent event) {
        if (benBot == null || benBot.isExitRequested()) {
            return;
        }

        String storageError = benBot.save();
        if (storageError.isEmpty()) {
            return;
        }

        event.consume();
        dialogContainer.getChildren().add(DialogBox.getBotDialog(storageError));
        Platform.runLater(userInput::requestFocus);
    }

    /** Adds the user's command and BenBot's response to the conversation. */
    @FXML
    private void handleUserInput() {
        if (benBot == null) {
            return;
        }

        String input = userInput.getText().trim();
        String response = benBot.getResponse(input);
        if (!input.isEmpty()) {
            dialogContainer.getChildren().add(DialogBox.getUserDialog(input));
        }
        dialogContainer.getChildren().add(DialogBox.getBotDialog(response));
        userInput.clear();

        if (benBot.isExitRequested()) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
            PauseTransition exitDelay = new PauseTransition(EXIT_DELAY);
            exitDelay.setOnFinished(event -> Platform.exit());
            exitDelay.play();
        }
    }
}
