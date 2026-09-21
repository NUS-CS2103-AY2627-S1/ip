package sheppy.gui;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import sheppy.Sheppy;
import sheppy.Ui;

/** Handles interactions with Sheppy's main JavaFX window. */
public class MainWindow {
    /** Displays the conversation and allows it to scroll. */
    @FXML
    private ScrollPane scrollPane;

    /** Contains the user and Sheppy dialogue boxes. */
    @FXML
    private VBox dialogContainer;

    /** Accepts commands typed by the user. */
    @FXML
    private TextField userInput;

    @FXML
    private Button sendButton;

    private boolean exiting;

    /** Processes commands and owns the task list. */
    private Sheppy sheppy;

    /** Creates a controller for the main window. */
    public MainWindow() {
    }

    /** Keeps the newest dialogue visible when the conversation grows. */
    @FXML
    private void initialize() {
        dialogContainer.heightProperty().addListener((observable, oldValue, newValue) -> {
            scrollPane.setVvalue(1.0);
        });
    }

    /**
     * Connects the window to Sheppy and displays the opening message.
     *
     * @param sheppy the chatbot that handles commands
     */
    public void setSheppy(Sheppy sheppy) {
        this.sheppy = sheppy;
        dialogContainer.getChildren().add(DialogBox.getSheppyDialog(
                Ui.WELCOME_MESSAGE));
        if (!sheppy.getStartupMessage().isEmpty()) {
            dialogContainer.getChildren().add(DialogBox.getSheppyDialog(
                    sheppy.getStartupMessage()));
        }
    }

    /** Sends the current input to Sheppy and displays both sides of the exchange. */
    @FXML
    private void handleUserInput() {
        if (exiting) {
            return;
        }
        String input = userInput.getText().trim();
        if (input.isEmpty()) {
            return;
        }

        String response = sheppy.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input),
                DialogBox.getSheppyDialog(response));
        userInput.clear();
        userInput.requestFocus();

        if (sheppy.isExitCommand(input)) {
            exiting = true;
            userInput.setDisable(true);
            sendButton.setDisable(true);
            PauseTransition exitDelay = new PauseTransition(Duration.seconds(1));
            exitDelay.setOnFinished(event -> Platform.exit());
            exitDelay.play();
        }
    }
}
