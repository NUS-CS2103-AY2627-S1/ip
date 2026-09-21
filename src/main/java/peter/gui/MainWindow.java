package peter.gui;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import peter.Peter;

/**
 * Controller for the main window: sends what the user types to the chatbot and
 * displays the reply.
 *
 * <p>This class does not extend the root's type. The FXML names a concrete
 * root, so the loader builds that root itself and creates this controller
 * separately; extending it would only produce a second, unused pane.
 */
public class MainWindow {

    /** Long enough for the farewell to be read before the window disappears. */
    private static final Duration EXIT_DELAY = Duration.seconds(1.5);

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox dialogContainer;

    @FXML
    private TextField userInput;

    @FXML
    private Button sendButton;

    private final Image userImage =
            new Image(MainWindow.class.getResourceAsStream("/images/User.png"));
    private final Image peterImage =
            new Image(MainWindow.class.getResourceAsStream("/images/Peter.png"));

    private Peter peter;

    /**
     * Prepares the window once the loader has injected the controls.
     *
     * <p>This work cannot happen in the constructor, where every {@code @FXML}
     * field is still null.
     */
    @FXML
    public void initialize() {
        // Following the container's height keeps the newest message in view
        // without any manual scrolling.
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Attaches the chatbot and opens the conversation with its greeting.
     *
     * <p>The greeting belongs here rather than in {@code initialize}, which
     * runs before the chatbot has been supplied.
     *
     * <p>A startup loading failure leaves the saved tasks unknown, so the
     * controls are disabled rather than allowing commands that would operate
     * on an empty list and overwrite the file. This mirrors the console, which
     * reports the error and stops.
     *
     * @param peter chatbot that answers the user's commands.
     */
    public void setPeter(Peter peter) {
        this.peter = peter;

        say(peter.getGreeting());
        if (peter.hasLoadingError()) {
            say(peter.getLoadingErrorMessage());
            setInputEnabled(false);
        }
    }

    /**
     * Adds the user's command and the chatbot's reply to the transcript, then
     * clears the input field ready for the next command.
     *
     * <p>A command that asks to exit closes the window shortly afterwards, so
     * that typing {@code bye} ends the app just as it does in the console.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input.isBlank()) {
            return;
        }

        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getPeterDialog(peter.getResponse(input), peterImage));
        userInput.clear();

        if (peter.isExitRequested()) {
            scheduleExit();
        }
    }

    /**
     * Closes the window after a short pause.
     *
     * <p>The controls are disabled straight away so that no further command
     * can be entered after the farewell, and the pause runs on a timer rather
     * than a sleep, which would freeze the window instead of showing it.
     */
    private void scheduleExit() {
        setInputEnabled(false);

        PauseTransition pause = new PauseTransition(EXIT_DELAY);
        pause.setOnFinished(event -> Platform.exit());
        pause.play();
    }

    /** Adds one message from the chatbot to the transcript. */
    private void say(String text) {
        dialogContainer.getChildren().add(DialogBox.getPeterDialog(text, peterImage));
    }

    /** Enables or disables both ways of entering a command. */
    private void setInputEnabled(boolean isEnabled) {
        userInput.setDisable(!isEnabled);
        sendButton.setDisable(!isEnabled);
    }
}
