package lily.ui;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import lily.Lily;
import lily.Lily.Response;

/** Controller for Lily's chat-style main window. */
public class MainWindow {
    /** Leaves enough time for the farewell bubble to become visible before closing. */
    private static final Duration EXIT_DELAY = Duration.millis(650);

    @FXML private ScrollPane scrollPane;
    @FXML private VBox dialogContainer;
    @FXML private TextField userInput;
    @FXML private Button sendButton;
    private Lily lily;
    private PauseTransition exitTransition;

    /** Keeps the newest response visible. */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /** Supplies the task manager used to process GUI commands. */
    public void setLily(Lily lily) {
        this.lily = lily;
        dialogContainer.getChildren().add(DialogBox.getLilyDialog(lily.getStartupMessage()));
    }

    /** Adds the user's command and Lily's reply to the conversation. */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input.isBlank()) {
            return;
        }
        Response response = lily.getResponseResult(input);
        dialogContainer.getChildren().addAll(DialogBox.getUserDialog(input),
                DialogBox.getLilyDialog(response.message(), response.isError()));
        userInput.clear();
        if (response.shouldExit()) {
            closeAfterFarewell();
        }
    }

    /** Disables further input and closes JavaFX after the farewell can be seen. */
    private void closeAfterFarewell() {
        userInput.setDisable(true);
        sendButton.setDisable(true);
        exitTransition = new PauseTransition(EXIT_DELAY);
        exitTransition.setOnFinished(event -> Platform.exit());
        exitTransition.play();
    }
}
