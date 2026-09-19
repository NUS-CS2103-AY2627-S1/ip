package ollie;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

/**
 * Controls Ollie's main chat window.
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

    private Ollie ollie;

    @FXML
    private void initialize() {
        dialogContainer.heightProperty().addListener((observable, oldHeight, newHeight) ->
                scrollPane.setVvalue(1.0));
    }

    /**
     * Supplies the chatbot that processes commands for this window.
     *
     * @param ollie Chatbot used to process commands.
     */
    public void setOllie(Ollie ollie) {
        this.ollie = ollie;
        dialogContainer.getChildren().add(
                DialogBox.getOllieDialog("Mission control online. I'm Ollie—here to keep "
                        + "your day in orbit. Type help to open the flight manual."));
        userInput.requestFocus();
    }

    /**
     * Sends the current input to Ollie and displays both sides of the exchange.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText().trim();
        if (input.isEmpty()) {
            return;
        }

        Ollie.CommandResponse commandResponse = ollie.getCommandResponse(input);
        DialogBox responseDialog = commandResponse.isError()
                ? DialogBox.getErrorDialog(commandResponse.message())
                : DialogBox.getOllieDialog(commandResponse.message());
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input),
                responseDialog);
        userInput.clear();

        if (commandResponse.isExit()) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
        }
    }
}
