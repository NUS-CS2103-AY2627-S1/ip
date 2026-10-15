package clarry.ui;

import clarry.Clarry;
import clarry.Response;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * Controls Clarry's main chat window.
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

    private Clarry clarry;

    /** Keeps the newest chat message visible as the conversation grows. */
    @FXML
    public void initialize() {
        assert scrollPane != null : "FXML must inject the scroll pane";
        assert dialogContainer != null : "FXML must inject the dialog container";
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Supplies the chatbot backend after the FXML view has loaded.
     *
     * @param clarry chatbot backend
     */
    public void setClarry(Clarry clarry) {
        assert clarry != null : "Main window requires a Clarry backend";
        this.clarry = clarry;
        dialogContainer.getChildren().add(
                DialogBox.getClarryDialog(Ui.getWelcomeMessage()));
    }

    /** Sends non-blank input to Clarry and displays both sides of the exchange. */
    @FXML
    private void handleUserInput() {
        assert clarry != null : "Clarry backend must be set before input is handled";
        assert userInput != null && sendButton != null : "FXML must inject the input controls";
        String input = userInput.getText();
        if (input.isBlank()) {
            return;
        }

        Response response = clarry.getCommandResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input),
                DialogBox.getClarryDialog(response.text().stripLeading(), response.isError()));
        userInput.clear();
        userInput.requestFocus();

        if (clarry.isExitRequested()) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
        }
    }
}
