package rei.gui;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import rei.Rei;

/** Controls the main chatbot window and passes user commands to Rei. */
public class MainWindow extends AnchorPane {
    private static final String GREETING = "Hey there, my name is Rei!\nHow can I help you today?";
    private static final Duration EXIT_DELAY = Duration.seconds(1.2);

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Rei rei;

    /** Configures automatic scrolling after the FXML controls have been injected. */
    @FXML
    public void initialize() {
        // Missing controls indicate an FXML wiring error, not invalid user input.
        assert scrollPane != null && dialogContainer != null && userInput != null && sendButton != null
                : "FXML must inject all main window controls";
        dialogContainer.heightProperty().addListener(observable -> scrollPane.setVvalue(1.0));
    }

    /** Injects the chatbot backend and displays its initial messages. */
    public void setRei(Rei rei) {
        this.rei = rei;
        dialogContainer.getChildren().add(DialogBox.getReiDialog(GREETING));
        if (!rei.getStartupMessage().isEmpty()) {
            dialogContainer.getChildren().add(DialogBox.getErrorDialog(rei.getStartupMessage()));
        }
        Platform.runLater(userInput::requestFocus);
    }

    /** Sends the typed command, displays Rei's reply, and handles the {@code bye} command. */
    @FXML
    private void handleUserInput() {
        // MainApp must supply the backend before users can send commands.
        assert rei != null : "Rei must be injected before handling user input";
        String input = userInput.getText().trim();
        if (input.isEmpty()) {
            return;
        }

        Rei.CommandResult result = rei.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input),
                result.isError() ? DialogBox.getErrorDialog(result.response())
                        : DialogBox.getReiDialog(result.response()));
        userInput.clear();

        if (result.shouldExit()) {
            disableInputAndClose();
        }
    }

    /** Prevents further commands and closes the window after the farewell can be read. */
    private void disableInputAndClose() {
        userInput.setDisable(true);
        sendButton.setDisable(true);

        PauseTransition pause = new PauseTransition(EXIT_DELAY);
        pause.setOnFinished(event -> getScene().getWindow().hide());
        pause.play();
    }
}
