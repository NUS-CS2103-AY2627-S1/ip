package seedu.goat.ui;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import seedu.goat.Goat;

/**
 * Controller for the chat window: owns the transcript and the input controls.
 * <p>
 * The controller does no task handling of its own. It passes what the user typed to the
 * chatbot and shows whatever comes back, which keeps the window ignorant of what any
 * command means.
 */
public class MainWindow {

    /** How long the goodbye stays on screen before the window closes itself. */
    private static final Duration CLOSING_DELAY = Duration.seconds(1.5);

    /** Scrolls the transcript; kept pinned to the newest message. */
    @FXML
    private ScrollPane scrollPane;

    /** Holds one dialog box per message, oldest first. */
    @FXML
    private VBox dialogContainer;

    /** Where the user types a command. */
    @FXML
    private TextField userInput;

    /** Sends what has been typed, for users who would rather click than press Enter. */
    @FXML
    private Button sendButton;

    /** The chatbot answering in this window. */
    private Goat goat;

    /**
     * Shows new replies without locking the scroll position.
     * <p>
     * Waits for layout after the transcript grows, then scrolls to the bottom.
     * Keeping the value unbound lets the user scroll back through earlier replies.
     */
    @FXML
    public void initialize() {
        assert scrollPane != null && dialogContainer != null : "FXML fills the controls first";
        dialogContainer.heightProperty().addListener((observable, oldHeight, newHeight) ->
                Platform.runLater(() -> scrollPane.setVvalue(1.0)));
    }

    /**
     * Attaches the chatbot and shows its opening messages.
     *
     * @param goat the chatbot this window talks to.
     */
    public void setGoat(Goat goat) {
        this.goat = goat;
        addGoatDialog(goat.getGreeting());

        // Anything notable about the save file is a separate message, so that a restored
        // list or a damaged file is not buried at the end of the greeting.
        String loadOutcome = goat.getLoadOutcome();
        if (!loadOutcome.isEmpty()) {
            addGoatDialog(loadOutcome);
        }
    }

    /** Adds the user's message and GOAT's reply to the transcript, then clears the field. */
    @FXML
    private void handleUserInput() {
        assert goat != null : "setGoat runs before the window can accept input";
        String input = userInput.getText();
        if (input.isBlank()) {
            // An empty send would otherwise post a blank bubble and a complaint.
            return;
        }

        dialogContainer.getChildren().add(DialogBox.getUserDialog(input, "You"));
        addGoatDialog(goat.getResponse(input));
        userInput.clear();

        if (goat.isFinished()) {
            closeAfterGoodbye();
        }
    }

    /**
     * Adds one of GOAT's replies to the transcript.
     *
     * @param message the reply text.
     */
    private void addGoatDialog(String message) {
        dialogContainer.getChildren().add(DialogBox.getGoatDialog(message, "GOAT"));
    }

    /**
     * Closes the window a moment after a bye.
     * <p>
     * Closing immediately would take the goodbye off the screen before it could be read,
     * so the controls are disabled to show the conversation is over and the window is
     * left up briefly. The pause runs on the JavaFX thread, so the reply is painted
     * rather than held back until the window is already closing.
     */
    private void closeAfterGoodbye() {
        userInput.setDisable(true);
        sendButton.setDisable(true);

        PauseTransition pause = new PauseTransition(CLOSING_DELAY);
        pause.setOnFinished(event -> Platform.exit());
        pause.play();
    }
}
