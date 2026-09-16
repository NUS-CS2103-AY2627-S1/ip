package kibo.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import kibo.CommandResponse;
import kibo.Kibo;

/**
 * Controls Kibo's main chat window.
 */
public class MainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Kibo kibo;

    /**
     * Creates a controller for the main FXML window.
     */
    public MainWindow() {
    }

    /**
     * Configures automatic scrolling after the FXML controls are loaded.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
        Platform.runLater(userInput::requestFocus);
    }

    /**
     * Supplies the chatbot that processes commands entered in this window.
     *
     * @param kibo chatbot used by the window.
     */
    public void setKibo(Kibo kibo) {
        this.kibo = kibo;
        dialogContainer.getChildren().add(DialogBox.getKiboDialog(kibo.getWelcomeMessage()));

        String loadingError = kibo.getLoadingErrorMessage();
        if (!loadingError.isEmpty()) {
            dialogContainer.getChildren().add(DialogBox.getErrorDialog(loadingError));
            disableInput();
        }
    }

    /**
     * Sends the current text to Kibo and displays both sides of the exchange.
     */
    @FXML
    private void handleUserInput() {
        String userText = userInput.getText();
        CommandResponse response = kibo.getCommandResponse(userText.trim());
        DialogBox reply = response.isError()
                ? DialogBox.getErrorDialog(response.getMessage())
                : DialogBox.getKiboDialog(response.getMessage());

        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(userText),
                reply);
        userInput.clear();

        if (kibo.isExitRequested()) {
            disableInput();
        }
    }

    /**
     * Prevents more commands after Kibo exits or cannot load its data.
     */
    private void disableInput() {
        userInput.setDisable(true);
        sendButton.setDisable(true);
    }
}
