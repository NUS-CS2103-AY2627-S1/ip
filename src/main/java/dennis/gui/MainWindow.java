package dennis.gui;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Controller for {@code MainWindow.fxml}: passes user input to the
 * {@link GuiResponder} and shows both sides of the conversation as
 * {@link DialogBox} bubbles.
 */
public class MainWindow {
    /** How long the farewell message stays visible before the window closes. */
    private static final Duration EXIT_DELAY = Duration.seconds(1.5);

    /** Scrolls to reveal the newest messages. */
    @FXML
    private ScrollPane scrollPane;

    /** Vertical stack that holds every dialog bubble. */
    @FXML
    private VBox dialogContainer;

    /** Text box where the user types commands. */
    @FXML
    private TextField userInput;

    /** Button that submits the text in {@link #userInput}. */
    @FXML
    private Button sendButton;

    /** Produces Dennis's replies; injected by {@link MainApp} after loading. */
    private GuiResponder responder;

    /** Creates the controller; JavaFX then populates the {@code @FXML} fields. */
    public MainWindow() {
    }

    /**
     * Injects the backend session and shows Dennis's greeting. Called by
     * {@link MainApp} once the FXML has been loaded.
     *
     * @param guiResponder the session that generates replies
     */
    public void setResponder(GuiResponder guiResponder) {
        responder = guiResponder;
        dialogContainer.getChildren().add(
                DialogBox.getDennisDialog(responder.getGreeting(), false));
    }

    /** Keeps the scroll pane pinned to the latest message. */
    @FXML
    private void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Handles the Send button and the Enter key: shows the user's text, then
     * Dennis's reply, then clears the input field. Blank input is ignored. If
     * the command was {@code bye}, the window closes shortly afterwards.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input.isEmpty()) {
            return;
        }

        String response = responder.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input),
                DialogBox.getDennisDialog(response, responder.isLastResponseError()));
        userInput.clear();

        if (responder.isExitRequested()) {
            closeAfterDelay();
        }
    }

    /**
     * Disables further input and closes the application after {@link #EXIT_DELAY},
     * so the farewell message stays on screen briefly before the window goes.
     */
    private void closeAfterDelay() {
        userInput.setDisable(true);
        sendButton.setDisable(true);

        PauseTransition pause = new PauseTransition(EXIT_DELAY);
        pause.setOnFinished(event -> Platform.exit());
        pause.play();
    }
}
