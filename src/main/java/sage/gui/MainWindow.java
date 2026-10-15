package sage.gui;

import java.util.Objects;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import sage.Sage;
import sage.ui.Ui;

/**
 * Controls the main Sage chat window.
 */
public class MainWindow {
    private static final Duration EXIT_DELAY = Duration.seconds(1.5);

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;
    @FXML
    private Button helpButton;
    @FXML
    private VBox helpPanel;

    private Sage sage;
    private String previousCommand = "";

    /**
     * Creates the controller used by the main-window FXML loader.
     */
    public MainWindow() {
    }

    /**
     * Initializes the conversation display after its FXML controls are injected.
     */
    @FXML
    private void initialize() {
        dialogContainer.heightProperty().addListener((observable, oldHeight, newHeight) ->
                scrollPane.setVvalue(scrollPane.getVmax()));
        helpPanel.managedProperty().bind(helpPanel.visibleProperty());
        sendButton.disableProperty().bind(Bindings.createBooleanBinding(
                () -> userInput.getText().isBlank() || userInput.isDisable(),
                userInput.textProperty(), userInput.disableProperty()));
        userInput.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.UP && userInput.getText().isEmpty()) {
                userInput.setText(previousCommand);
                userInput.positionCaret(previousCommand.length());
                event.consume();
            }
        });
        Platform.runLater(userInput::requestFocus);
    }

    /**
     * Connects this window to the long-lived Sage backend.
     *
     * @param sage The backend that processes commands and retains task state.
     */
    public void setSage(Sage sage) {
        this.sage = Objects.requireNonNull(sage);
        dialogContainer.getChildren().add(DialogBox.getSageDialog(Ui.WELCOME_MESSAGE));
        String startupMessage = sage.getStartupMessage();
        if (!startupMessage.isBlank()) {
            dialogContainer.getChildren().add(DialogBox.getErrorDialog(startupMessage));
        }
    }

    /**
     * Sends the entered command to Sage and displays both sides of the exchange.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText().strip();
        if (input.isEmpty()) {
            userInput.clear();
            return;
        }

        Sage activeSage = Objects.requireNonNull(sage, "Sage must be set before accepting input.");
        previousCommand = input;
        String response = activeSage.getResponse(input);
        DialogBox responseDialog = activeSage.hasError()
                ? DialogBox.getErrorDialog(response) : DialogBox.getSageDialog(response);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input), responseDialog);
        if (activeSage.hasError()) {
            userInput.selectAll();
        } else {
            userInput.clear();
        }
        userInput.requestFocus();
        Platform.runLater(() -> scrollPane.setVvalue(scrollPane.getVmax()));

        if (activeSage.isExit()) {
            closeAfterFarewell();
        }
    }

    /**
     * Expands or collapses a compact reference without adding messages to the conversation.
     */
    @FXML
    private void toggleHelp() {
        boolean shouldShowHelp = !helpPanel.isVisible();
        helpPanel.setVisible(shouldShowHelp);
        helpButton.setText(shouldShowHelp ? "Hide help" : "Commands");
        helpButton.setAccessibleText(shouldShowHelp ? "Hide command reference" : "Show command reference");
        userInput.requestFocus();
    }

    /**
     * Disables further input and closes the app after the farewell can be read.
     */
    private void closeAfterFarewell() {
        userInput.setDisable(true);
        helpButton.setDisable(true);

        PauseTransition exitDelay = new PauseTransition(EXIT_DELAY);
        exitDelay.setOnFinished(event -> Platform.exit());
        exitDelay.play();
    }
}
