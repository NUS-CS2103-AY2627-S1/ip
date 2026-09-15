package larp;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Controls the main Larp window and passes commands to the application logic.
 */
public class MainWindow {
    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox dialogContainer;

    @FXML
    private TextField userInput;

    @FXML
    private Button sendButton;

    private Larp larp;

    /**
     * Creates the controller that JavaFX connects to the main-window layout.
     */
    public MainWindow() {
    }

    /**
     * Configures behavior that depends only on controls loaded from FXML.
     */
    @FXML
    public void initialize() {
        dialogContainer.heightProperty().addListener(
                observable -> scrollPane.setVvalue(1.0));
        sendButton.disableProperty().bind(Bindings.createBooleanBinding(
                () -> userInput.getText().trim().isEmpty(),
                userInput.textProperty()));

        userInput.setAccessibleText("Command input");
        sendButton.setAccessibleText("Send command");
        Platform.runLater(userInput::requestFocus);
    }

    /**
     * Supplies the application logic and displays its welcome message.
     *
     * @param larp Larp instance that will process user commands.
     */
    public void setLarp(Larp larp) {
        this.larp = larp;
        dialogContainer.getChildren().add(
                DialogBox.getLarpDialog(larp.getWelcomeMessage()));
    }

    /**
     * Displays the entered command and Larp's response, then clears the input field.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText().trim();
        if (input.isEmpty() || this.larp == null) {
            return;
        }

        String response = this.larp.getResponse(input);
        DialogBox responseDialog = Ui.isErrorMessage(response)
                ? DialogBox.getErrorDialog(response)
                : DialogBox.getLarpDialog(response);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input),
                responseDialog);
        userInput.clear();

        if (this.larp.isExitCommand(input)) {
            userInput.setDisable(true);
            PauseTransition exitDelay = new PauseTransition(Duration.millis(700));
            exitDelay.setOnFinished(event -> Platform.exit());
            exitDelay.play();
        }
    }
}
