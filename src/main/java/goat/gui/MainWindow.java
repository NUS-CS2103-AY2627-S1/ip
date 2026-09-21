package goat.gui;

import goat.Goat;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Drives the main window: reads what is typed, asks Goat, shows both.
 *
 * The controller holds no chatbot logic of its own. Every line the user types
 * goes straight to {@link Goat#getResponse(String)}, and whatever comes back is
 * shown. The only decision made here is which of the three turn styles to use,
 * and that is answered by asking Goat whether its reply was a complaint.
 */
public class MainWindow extends AnchorPane {

    /** How long Goat's farewell stays on screen before the window closes. */
    private static final Duration FAREWELL_PAUSE = Duration.seconds(1.5);

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox dialogContainer;

    @FXML
    private TextField userInput;

    @FXML
    private Button sendButton;

    @FXML
    private Label statusLabel;

    private Goat goat;

    /** Keeps the newest message in view as the conversation grows. */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
        // Nothing useful can come of an empty command, so the button says so
        // rather than letting the user press it and be told off.
        sendButton.disableProperty().bind(userInput.textProperty().isEmpty());
    }

    /**
     * Hands this window the chatbot it should talk to, and shows its greeting.
     *
     * @param goat the chatbot driving the conversation
     */
    public void setGoat(Goat goat) {
        this.goat = goat;
        String welcome = goat.getWelcomeMessage();
        dialogContainer.getChildren().add(goat.isLastResponseAnError()
                ? DialogBox.getErrorDialog(welcome)
                : DialogBox.getGoatDialog(welcome));
        userInput.requestFocus();
    }

    /**
     * Sends whatever is in the text field to Goat and shows both sides of the
     * exchange.
     *
     * Wired to both the Send button and the Enter key by MainWindow.fxml.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText().trim();
        if (input.isEmpty()) {
            return;
        }
        String response = goat.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input),
                goat.isLastResponseAnError()
                        ? DialogBox.getErrorDialog(response)
                        : DialogBox.getGoatDialog(response));
        userInput.clear();
        // Typing the next command should not need a click first.
        userInput.requestFocus();

        if (goat.isExitRequested()) {
            closeAfterFarewell();
        }
    }

    /**
     * Closes the window, but not before the farewell has been read.
     *
     * Exiting the instant "bye" is typed would hide the reply Goat just gave,
     * so the window waits first. Input is disabled during the pause, so nothing
     * can be typed into a chatbot that is on its way out.
     */
    private void closeAfterFarewell() {
        userInput.setDisable(true);
        statusLabel.setText("closing...");
        PauseTransition pause = new PauseTransition(FAREWELL_PAUSE);
        pause.setOnFinished(event -> Platform.exit());
        pause.play();
    }
}
