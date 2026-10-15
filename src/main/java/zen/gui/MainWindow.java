package zen.gui;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import zen.Zen;

/**
 * Controller for the main GUI.
 */
public class MainWindow extends AnchorPane {
    private static final Duration EXIT_DELAY = Duration.seconds(1);

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Zen zen;

    private final Image userImage = new Image(getClass().getResourceAsStream("/images/user.png"));
    private final Image zenImage = new Image(getClass().getResourceAsStream("/images/zen.png"));

    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Sets the chatbot that processes GUI commands.
     *
     * @param zen chatbot instance to use
     */
    public void setZen(Zen zen) {
        this.zen = zen;

        dialogContainer.getChildren().add(
                DialogBox.getZenDialog(zen.getGreeting(), zenImage)
        );
        if (!zen.getInitializationError().isEmpty()) {
            dialogContainer.getChildren().add(
                    DialogBox.getZenDialog(zen.getInitializationError(), zenImage)
            );
            userInput.setDisable(true);
            sendButton.setDisable(true);
        }
    }

    /**
     * Adds the user's command and the chatbot's response to the conversation.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText().trim();
        if (input.isEmpty()) {
            return;
        }
        String response = zen.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getZenDialog(response, zenImage)
        );
        userInput.clear();
        if (zen.hasExited()) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
            scheduleExit();
        }
    }

    /** Closes the application after leaving the farewell message visible briefly. */
    private void scheduleExit() {
        PauseTransition exitPause = new PauseTransition(EXIT_DELAY);
        exitPause.setOnFinished(event -> Platform.exit());
        exitPause.play();
    }
}
