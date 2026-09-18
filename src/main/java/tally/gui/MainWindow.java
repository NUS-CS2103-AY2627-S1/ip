package tally.gui;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import tally.Tally;

/**
 * Drives the main window: takes what is typed, and shows what Tally says back.
 *
 * <p>This is the window's controller rather than the window itself. MainWindow.fxml
 * names its own root, which the loader builds and hands to Main.
 */
public class MainWindow {
    /** How long the goodbye stays on screen before the window closes itself. */
    private static final Duration FAREWELL_PAUSE = Duration.seconds(1.5);

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Tally tally;
    private final Image userImage =
            new Image(this.getClass().getResourceAsStream("/images/user.png"));
    private final Image tallyImage =
            new Image(this.getClass().getResourceAsStream("/images/tally.png"));

    /**
     * Scrolls down whenever the conversation grows, so the newest message is in view.
     *
     * <p>Called by the FXML loader, which finds it by the annotation rather than by it
     * being visible from outside this package.
     */
    @FXML
    void initialize() {
        // Scrolled to whatever the pane calls its foot, rather than to 1.0, which is
        // only what that happens to be until someone sets a different maximum.
        dialogContainer.heightProperty().addListener(
                observed -> scrollPane.setVvalue(scrollPane.getVmax()));
    }

    /**
     * Attaches the chatbot this window talks to, and shows its greeting.
     *
     * @param tally the chatbot.
     */
    void setTally(Tally tally) {
        this.tally = tally;
        dialogContainer.getChildren().add(
                DialogBox.createTallyDialog(tally.getGreeting(), tallyImage));
    }

    /**
     * Sends whatever was typed to the chatbot and shows both sides of the exchange.
     *
     * <p>Saying goodbye closes the window once the farewell has been shown, rather
     * than at once, so the user sees the reply they asked for.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input.isBlank()) {
            // Nothing to send, but Send has taken the focus and has to give it back,
            // or the next thing typed goes nowhere.
            userInput.requestFocus();
            return;
        }
        showConversationTurn(input, tally.getResponse(input));
        if (tally.isExiting()) {
            scheduleExit();
        }
    }

    /**
     * Puts one turn of the conversation on screen and readies the box for the next.
     *
     * @param input what the user typed.
     * @param response what Tally said back.
     */
    private void showConversationTurn(String input, String response) {
        dialogContainer.getChildren().addAll(
                DialogBox.createUserDialog(input, userImage),
                DialogBox.createTallyDialog(response, tallyImage));
        userInput.clear();
        // Pressing Send moves the focus to the button, so the next command would be
        // typed nowhere. Put it back where the user is about to type.
        userInput.requestFocus();
    }

    /**
     * Stops the user typing again and closes the window once the goodbye has been read.
     *
     * <p>Closing at once would take the goodbye off the screen before anyone saw it.
     */
    private void scheduleExit() {
        userInput.setDisable(true);
        sendButton.setDisable(true);
        PauseTransition pause = new PauseTransition(FAREWELL_PAUSE);
        pause.setOnFinished(event -> Platform.exit());
        pause.play();
    }
}
