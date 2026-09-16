package zucc.ui;

import java.util.Objects;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import zucc.Zucc;

/**
 * Controls the main JavaFX window and presents Zucc's responses as a conversation.
 */
public final class MainWindow extends Ui {
    /** Scroll-pane position that reveals the most recent dialog. */
    private static final double SCROLL_BOTTOM_POSITION = 1.0;

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

    @FXML
    private Circle statusDot;

    /** Application service that processes commands from this window. */
    private Zucc zucc;

    /**
     * Configures behavior that depends only on controls loaded from FXML.
     */
    @FXML
    private void initialize() {
        // FXMLLoader must inject every control before this controller becomes usable.
        assert scrollPane != null && dialogContainer != null && userInput != null
                && sendButton != null && statusLabel != null && statusDot != null
                : "MainWindow.fxml must inject all required controls";
        dialogContainer.heightProperty().addListener(
                (observable, oldHeight, newHeight) -> scrollPane.setVvalue(SCROLL_BOTTOM_POSITION));
    }

    /**
     * Connects the window to the initialized application service.
     *
     * @param zucc application service used to process commands.
     */
    public void setZucc(Zucc zucc) {
        this.zucc = Objects.requireNonNull(zucc);
        showGreeting();
    }

    /**
     * Gives keyboard focus to the command field after the stage becomes visible.
     */
    public void focusInput() {
        Platform.runLater(userInput::requestFocus);
    }

    /**
     * Shows a startup error and prevents commands from being submitted.
     *
     * @param message reason the application is unavailable.
     */
    public void showUnavailable(String message) {
        showError(message);
        setStatus("UNAVAILABLE", "status-dot-unavailable");
        userInput.setPromptText("Zucc could not start");
        disableInput();
    }

    /**
     * Adds one response from Zucc to the conversation.
     *
     * @param message response to display.
     */
    @Override
    public void showMessage(String message) {
        dialogContainer.getChildren().add(DialogBox.createZuccDialog(message));
    }

    /**
     * Adds a visually prominent, recoverable error to the conversation.
     *
     * @param message explanation of the error and how to recover from it.
     */
    @Override
    public void showError(String message) {
        dialogContainer.getChildren().add(DialogBox.createErrorDialog(message));
    }

    /**
     * Sends nonblank user input to Zucc and displays it in the conversation.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText().strip();
        if (input.isEmpty() || zucc == null) {
            return;
        }

        dialogContainer.getChildren().add(DialogBox.createUserDialog(input));
        userInput.clear();

        boolean isExit = zucc.executeCommand(input, this);
        if (isExit) {
            setStatus("SESSION ENDED", "status-dot-ended");
            userInput.setPromptText("Session ended");
            disableInput();
        }
    }

    /** Updates the status text and its visual indicator. */
    private void setStatus(String text, String dotStyleClass) {
        statusLabel.setText(text);
        statusDot.getStyleClass().removeAll("status-dot-unavailable", "status-dot-ended");
        statusDot.getStyleClass().add(dotStyleClass);
    }

    /** Disables controls that submit new commands. */
    private void disableInput() {
        userInput.setDisable(true);
        sendButton.setDisable(true);
    }
}
