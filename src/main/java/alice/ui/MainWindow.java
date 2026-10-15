package alice.ui;

import alice.Alice;
import alice.Alice.Response;
import alice.parser.Command;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Connects the main chat-window layout to Alice's command processing.
 */
public class MainWindow {
    private static final Duration FAREWELL_DELAY = Duration.millis(1500);

    private Alice alice;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox dialogContainer;

    @FXML
    private TextField userInput;

    /**
     * Adds Alice's greeting after the FXML controls have been created.
     */
    @FXML
    public void initialize() {
        DialogBox welcomeDialog = DialogBox.getBotDialog(Ui.getWelcomeMessage());
        welcomeDialog.getStyleClass().add("welcome-dialog");
        dialogContainer.getChildren().add(welcomeDialog);
        scrollToBottom();
    }

    /**
     * Supplies the shared Alice instance used to process chat commands.
     *
     * @param alice The bot instance for this application session.
     */
    public void setAlice(Alice alice) {

        this.alice = alice;
    }

    /**
     * Sends the text-field command to Alice and displays both messages.
     */
    @FXML
    private void handleUserInput() {
        String command = userInput.getText().trim();
        if (command.isEmpty()) {
            return;
        }
        dialogContainer.getChildren().add(DialogBox.getUserDialog(command));
        Response response = alice.getResponseResult(command);
        DialogBox responseDialog = response.isError()
                ? DialogBox.getErrorDialog(response.message())
                : DialogBox.getBotDialog(response.message());
        dialogContainer.getChildren().add(responseDialog);
        scrollToBottom();
        userInput.clear();
        if (command.equals(Command.BYE.getCommandWord())) {
            closeAfterFarewell();
        }
    }

    /**
     * Keeps the farewell visible briefly before closing the application.
     */
    private void closeAfterFarewell() {
        userInput.setDisable(true);
        PauseTransition farewellPause = new PauseTransition(FAREWELL_DELAY);
        farewellPause.setOnFinished(event -> Platform.exit());
        farewellPause.play();
    }

    /**
     * Scrolls to the latest message after JavaFX has recalculated the chat layout.
     */
    private void scrollToBottom() {
        Platform.runLater(() -> scrollPane.setVvalue(1.0));
    }
}
