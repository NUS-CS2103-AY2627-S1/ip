package sallman.gui;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import sallman.Sallman;

/**
 * Controller for the main window.
 * <p>
 * Based on the SE-EDU JavaFX tutorial (https://se-education.org/guides/tutorials/javaFx.html),
 * extended here to style errors differently and to close the window after a
 * short pause when the user says goodbye.
 */
public class MainWindow {

    /** How long the goodbye stays on screen before the window closes. */
    private static final Duration EXIT_DELAY = Duration.seconds(1.5);

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Sallman sallman;

    private final Image sallmanImage = new Image(this.getClass().getResourceAsStream("/images/sallman.png"));

    /**
     * Makes the conversation scroll to the newest message as it grows, keeps
     * Send switched off until there is something to send, and puts the cursor
     * in the input box so the user can type straight away.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
        sendButton.disableProperty().bind(
                Bindings.createBooleanBinding(() -> userInput.getText().isBlank(), userInput.textProperty()));
        Platform.runLater(userInput::requestFocus);
    }

    /**
     * Hands the window the chatbot it should talk to, and shows its greeting.
     *
     * @param sallman the chatbot answering the user's commands
     */
    public void setSallman(Sallman sallman) {
        this.sallman = sallman;
        dialogContainer.getChildren().add(
                DialogBox.getSallmanDialog(sallman.getGreeting(), sallmanImage, ""));
    }

    /**
     * Shows what the user typed and the reply to it, then clears the input box.
     * <p>
     * A reply reporting a problem is drawn differently from an ordinary one, so
     * that it is noticed. A command that ends the session leaves its reply on
     * screen briefly before the window closes, so the goodbye can be read.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input.isBlank()) {
            return;
        }
        String response = sallman.getResponse(input);
        DialogBox reply = sallman.isLastResponseError()
                ? DialogBox.getErrorDialog(response, sallmanImage)
                : DialogBox.getSallmanDialog(response, sallmanImage, sallman.getLastCommandType());
        dialogContainer.getChildren().addAll(DialogBox.getUserDialog(input), reply);
        userInput.clear();

        if (sallman.isExitRequested()) {
            PauseTransition pause = new PauseTransition(EXIT_DELAY);
            pause.setOnFinished(event -> Platform.exit());
            pause.play();
        }
    }
}
