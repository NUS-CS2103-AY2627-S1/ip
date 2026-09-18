package vega;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/** Controls Vega's main chat window. */
public class MainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox dialogContainer;

    @FXML
    private TextField userInput;

    @FXML
    private Button sendButton;

    private Vega vega;

    /** Keeps the latest dialog visible as the conversation grows. */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Supplies the Vega instance that handles user commands.
     *
     * @param vega Chatbot used by this window.
     */
    public void setVega(Vega vega) {
        this.vega = vega;
        dialogContainer.getChildren().add(DialogBox.getVegaDialog(vega.getWelcomeMessage()));
    }

    /** Sends the current input to Vega and displays both messages. */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText().strip();
        if (input.isEmpty()) {
            return;
        }

        String response = vega.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input),
                DialogBox.getVegaDialog(response));
        userInput.clear();

        if (vega.isExitRequested()) {
            sendButton.setDisable(true);
            userInput.setDisable(true);
            Platform.runLater(Platform::exit);
        }
    }
}
