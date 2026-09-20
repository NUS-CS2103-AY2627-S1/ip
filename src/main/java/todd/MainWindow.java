package todd;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/** Controls Todd's main chat window. */
public class MainWindow extends BorderPane {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Todd todd;

    /** Keeps the most recent dialog visible as the conversation grows. */
    @FXML
    public void initialize() {
        dialogContainer.heightProperty().addListener((observable, oldHeight, newHeight) ->
                Platform.runLater(() -> scrollPane.setVvalue(1.0)));
        sendButton.disableProperty().bind(Bindings.createBooleanBinding(() ->
                userInput.getText().isBlank(), userInput.textProperty()));
    }

    /** Supplies the application instance that processes commands. */
    public void setTodd(Todd todd) {
        this.todd = todd;
        dialogContainer.getChildren().add(DialogBox.getToddDialog(todd.getGuiWelcomeMessage()));
    }

    /** Sends the typed command to Todd and displays both sides of the exchange. */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText().trim();
        if (input.isEmpty()) {
            return;
        }

        sendCommand(input);
        userInput.clear();
    }

    /** Displays a command and its reply, then returns focus to the input field. */
    private void sendCommand(String input) {
        Response response = todd.getReply(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input),
                DialogBox.getToddDialog(response));
        if (response.exit()) {
            closeAfterGoodbye();
        } else {
            userInput.requestFocus();
        }
    }

    /** Lets the farewell remain visible briefly before ending the JavaFX application. */
    private void closeAfterGoodbye() {
        setDisable(true);
        PauseTransition pause = new PauseTransition(Duration.seconds(1));
        pause.setOnFinished(event -> Platform.exit());
        pause.play();
    }

    /** Shows the full task list without replacing an unfinished command. */
    @FXML
    private void handleList() {
        sendCommand("list");
    }

    /** Shows upcoming tasks through the same command flow as typed input. */
    @FXML
    private void handleReminders() {
        sendCommand("reminders");
    }

    /** Shows command examples from the built-in help. */
    @FXML
    private void handleHelp() {
        sendCommand("help");
    }
}
