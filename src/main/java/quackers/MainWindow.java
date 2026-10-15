package quackers;

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

/**
 * Controller for the main GUI window.
 *
 * <p>Each {@code @FXML} field is bound by the FXML loader to the control carrying the matching
 * {@code fx:id} in {@code MainWindow.fxml}. The annotation is what allows these members to stay
 * private instead of being exposed to the rest of the application.
 */
public class MainWindow extends AnchorPane {
    /** How long the farewell stays on screen before the window closes. */
    private static final Duration EXIT_DELAY = Duration.seconds(1.5);

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Quackers quackers;

    private final Image userImage =
            new Image(this.getClass().getResourceAsStream("/images/DaUser.png"));
    private final Image quackersImage =
            new Image(this.getClass().getResourceAsStream("/images/duck.png"));

    /**
     * Prepares the window once the FXML fields have been injected.
     */
    @FXML
    public void initialize() {
        // Tying the scroll position to the container's height keeps the newest message in view.
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Injects the {@link Quackers} instance and greets the user.
     *
     * @param quackers the chatbot that answers the user's commands.
     */
    public void setQuackers(Quackers quackers) {
        this.quackers = quackers;
        showQuackers(quackers.getWelcomeMessage());
        if (quackers.getLoadingError() != null) {
            showQuackers(quackers.getLoadingError());
        }
    }

    /**
     * Shows the user's input and the reply as a pair of dialog boxes, then clears the input
     * field. Closes the window shortly afterwards if the user asked to exit.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input.isBlank()) {
            return;
        }

        String response = quackers.getResponse(input);
        dialogContainer.getChildren().add(DialogBox.getUserDialog(input, userImage));
        showQuackers(response);
        userInput.clear();

        if (quackers.isExitRequested()) {
            exitAfterDelay();
        }
    }

    /**
     * Appends a dialog box containing a message from Quackers.
     */
    private void showQuackers(String message) {
        dialogContainer.getChildren().add(DialogBox.getQuackersDialog(message, quackersImage));
    }

    /**
     * Closes the window after a pause, so the farewell can be read before the app disappears.
     */
    private void exitAfterDelay() {
        userInput.setDisable(true);
        sendButton.setDisable(true);

        PauseTransition pause = new PauseTransition(EXIT_DELAY);
        pause.setOnFinished(event -> Platform.exit());
        pause.play();
    }
}
