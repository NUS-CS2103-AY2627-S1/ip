package bubba;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Controls Bubba's main chat window.
 */
public class MainWindow {
    private static final Duration EXIT_DELAY = Duration.seconds(1);

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Bubba bubba;

    /**
     * Configures automatic scrolling after the FXML controls are loaded.
     */
    @FXML
    public void initialize() {
        dialogContainer.heightProperty().addListener(
                observable -> scrollPane.setVvalue(1.0));
        Platform.runLater(userInput::requestFocus);
    }

    /**
     * Supplies the chatbot and displays its initial message.
     *
     * @param bubba Chatbot used to process commands.
     */
    public void setBubba(Bubba bubba) {
        assert bubba != null : "The main window requires a Bubba instance";

        this.bubba = bubba;
        dialogContainer.getChildren().add(
                DialogBox.getBubbaDialog(bubba.getWelcomeMessage()));
    }

    @FXML
    private void handleUserInput() {
        assert bubba != null : "Bubba must be initialized before handling input";

        String input = userInput.getText().trim();
        if (input.isEmpty()) {
            return;
        }

        String response = bubba.getResponse(input);
        DialogBox responseDialog = bubba.isLastResponseError()
                ? DialogBox.getErrorDialog(response)
                : DialogBox.getBubbaDialog(response);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input),
                responseDialog);
        userInput.clear();

        if (bubba.isExitRequested()) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
            PauseTransition exitDelay = new PauseTransition(EXIT_DELAY);
            exitDelay.setOnFinished(event -> Platform.exit());
            exitDelay.play();
        }
    }
}
