package happybot;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Controls the main HappyBot window.
 */
public class MainWindow extends AnchorPane {
    private static final Duration EXIT_DELAY = Duration.seconds(3);

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox dialogContainer;

    @FXML
    private TextField userInput;

    @FXML
    private Button sendButton;

    private HappyBot happyBot;

    /**
     * Configures behavior that depends only on controls from the FXML view.
     */
    @FXML
    public void initialize() {
        dialogContainer.heightProperty().addListener(observable -> scrollPane.setVvalue(1.0));
    }

    /**
     * Supplies the chatbot and displays its welcome message.
     *
     * @param happyBot The chatbot that responds to commands.
     */
    public void setHappyBot(HappyBot happyBot) {
        this.happyBot = happyBot;
        dialogContainer.getChildren().add(DialogBox.getBotDialog(happyBot.getWelcomeMessage()));
        // Requests focus after JavaFX finishes displaying and laying out the window.
        Platform.runLater(userInput::requestFocus);
    }

    /**
     * Displays the user's command and response, allowing time to read the farewell before exiting.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText().strip();
        if (input.isEmpty()) {
            return;
        }

        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input),
                DialogBox.getBotDialog(happyBot.getResponse(input)));
        userInput.clear();

        if (happyBot.isExitCommand(input)) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
            // Keep the JavaFX thread free to display the farewell while waiting to close.
            PauseTransition exitPause = new PauseTransition(EXIT_DELAY);
            exitPause.setOnFinished(event -> Platform.exit());
            exitPause.play();
        }
    }
}
